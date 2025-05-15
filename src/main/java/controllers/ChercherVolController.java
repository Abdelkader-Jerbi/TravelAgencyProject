package controllers;
import entities.Enumnom;
import entities.Vol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CrudVol;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.scene.control.ComboBox;
import entities.StatutVol;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ChercherVolController implements Initializable {
    @FXML
    private RadioButton allerSimpleRadio;
    @FXML
    private RadioButton allerRetourRadio;
    @FXML
    private Label labelRetour;
    @FXML
    private ComboBox<String> departField;
    @FXML
    private ComboBox<String> destinationField;
    @FXML
    private DatePicker dateField;
    @FXML
    private DatePicker dateRetourField;
    @FXML
    private Button rechercherButton;
    @FXML
    private ChoiceBox<Enumnom> categorieChoiceBox;
    
    private final CrudVol crudVol = new CrudVol();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        destinationField.setEditable(true);
        departField.setEditable(true);
        
        // Configuration des champs de recherche
        departField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterComboBox(departField, newValue);
        });

        destinationField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterComboBox(destinationField, newValue);
        });

        // Configuration du type de vol
        allerSimpleRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            dateRetourField.setDisable(newVal);
        });

        // Charger la liste des pays
        loadCountries();

        // Configuration de la catégorie
        categorieChoiceBox.getItems().addAll(Enumnom.values());

        // Configuration du bouton de recherche
        rechercherButton.setOnAction(event -> chercherVol());
    }

    @FXML
    public void handleRechercherButtonHover() {
        rechercherButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                                "-fx-background-radius: 8; -fx-padding: 12 35; -fx-font-size: 14px;");
    }

    @FXML
    public void handleRechercherButtonExit() {
        rechercherButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; " +
                                "-fx-background-radius: 8; -fx-padding: 12 35; -fx-font-size: 14px;");
    }

    private void loadCountries() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://restcountries.com/v3.1/all"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        JSONArray array = new JSONArray(json);
                        List<String> pays = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject country = array.getJSONObject(i);
                            String name = country.getJSONObject("name").getString("common");
                            pays.add(name);
                        }
                        Platform.runLater(() -> {
                            departField.getItems().clear();
                            destinationField.getItems().clear();
                            departField.getItems().addAll(pays);
                            destinationField.getItems().addAll(pays);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void filterComboBox(ComboBox<String> comboBox, String filter) {
        if (filter == null || filter.isEmpty()) {
            comboBox.show();
            return;
        }

        ObservableList<String> filteredItems = FXCollections.observableArrayList();
        for (String item : comboBox.getItems()) {
            if (item.toLowerCase().contains(filter.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        comboBox.setItems(filteredItems);
        comboBox.show();
    }

    private void chercherVol() {
        String depart = departField.getValue();
        String destination = destinationField.getValue();
        Enumnom categorie = categorieChoiceBox.getValue();
        LocalDate localDate = dateField.getValue();
        LocalDate retourDate = dateRetourField.getValue();

        // Validation des champs obligatoires
        if (depart == null || depart.trim().isEmpty()) {
            afficherAlerte("Champ manquant", "Départ vide", "Veuillez saisir un lieu de départ.");
            return;
        }

        if (destination == null || destination.trim().isEmpty()) {
            afficherAlerte("Champ manquant", "Destination vide", "Veuillez saisir une destination.");
            return;
        }

        try {
            List<Vol> volsTrouves;
            
            // Si c'est un aller-retour, vérifier la date de retour
            if (allerRetourRadio.isSelected() && retourDate == null) {
                afficherAlerte("Date manquante", "Date de retour requise", "Veuillez sélectionner une date de retour pour un vol aller-retour.");
                return;
            }

            // Si c'est un aller simple, la date de retour doit être null
            if (allerSimpleRadio.isSelected()) {
                retourDate = null;
            }

            // Recherche selon les critères
            if (localDate == null) {
                // Recherche sans date
                volsTrouves = crudVol.chercherVolParDepartEtDestination(depart, destination);
            } else {
                // Recherche avec date
                String dbDateString = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String dateRetourString = retourDate != null ? 
                    retourDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
                
                // Si une catégorie est sélectionnée, l'utiliser dans la recherche
                if (categorie != null) {
                    volsTrouves = crudVol.chercherVol(depart, destination, dbDateString, dateRetourString, categorie);
                } else {
                    // Recherche sans catégorie
                    volsTrouves = crudVol.chercherVol(depart, destination, dbDateString, dateRetourString, null);
                }
            }

            // Filtrer les vols disponibles
            LocalDate aujourdHui = LocalDate.now();
            List<Vol> volsDisponibles = volsTrouves.stream()
                .filter(vol -> {
                    LocalDate dateDepartVol = ((java.sql.Date) vol.getDate()).toLocalDate();
                    return vol.getStatut() == StatutVol.Disponible && !dateDepartVol.isBefore(aujourdHui);
                })
                .sorted(Comparator.comparing(Vol::getDate))
                .collect(Collectors.toList());

            if (!volsDisponibles.isEmpty()) {
                afficherAlerte("Succès", "Vol(s) trouvé(s)", "Nombre de vols trouvés : " + volsDisponibles.size());
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReserverVol.fxml"));
                    Parent root = loader.load();

                    ReserverVolController controller = loader.getController();
                    controller.setVols(volsDisponibles);

                    Stage stage = (Stage) rechercherButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    afficherAlerte("Erreur", "Erreur de navigation", "Impossible d'afficher les résultats.");
                }
            } else {
                afficherAlerte("Aucun vol", "Aucun vol trouvé", "Veuillez modifier les critères de recherche.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de la recherche", "Une erreur est survenue lors de la recherche des vols.");
        }
    }

    private void afficherAlerte(String titre, String enTete, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(enTete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
