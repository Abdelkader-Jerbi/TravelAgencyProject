package controllers;
import entities.Enumnom;
import entities.Vol;
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

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ChercherVolController implements Initializable {
    @FXML
    private javafx.scene.control.RadioButton allerSimpleRadio;

    @FXML
    private javafx.scene.control.RadioButton allerRetourRadio;

    @FXML
    private javafx.scene.control.Label labelRetour;
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
    private final CrudVol crudVol = new CrudVol();


    @FXML
    private ChoiceBox<Enumnom> categorieChoiceBox;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger la liste des pays pour les ComboBox
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
                            departField.getItems().addAll(pays);
                            destinationField.getItems().addAll(pays);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        categorieChoiceBox.getItems().addAll(Enumnom.values());

        rechercherButton.setOnAction(event -> {
            System.out.println("Bouton cliqué !");
            chercherVol();});
    }
    private void chercherVol() {
        System.out.println("Début recherche...");
        String depart = departField.getValue();

        String destination = destinationField.getValue();

        Enumnom categorie = categorieChoiceBox.getValue();


        if (depart == null || depart.trim().isEmpty()) {
            afficherAlerte("Champ manquant", "Départ vide", "Veuillez saisir un lieu de départ.");
            return;
        }

        if (destination == null || destination.trim().isEmpty()) {
            afficherAlerte("Champ manquant", "Destination vide", "Veuillez saisir une destination.");
            return;
        }

        LocalDate localDate = dateField.getValue();
        List<Vol> volsTrouves;


        try {
            if (localDate == null) {
                volsTrouves =crudVol.chercherVolParDepartEtDestination(depart,destination);
            }else {
                String dbDateString = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate retourDate = dateRetourField.getValue();
                String dateRetourString = retourDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                System.out.println("Départ : " + depart);
                System.out.println("Destination : " + destination);
                System.out.println("Date : " + dbDateString);
                System.out.println("Catégorie : " + categorie);
// Recherche complète avec date et catégorie
                System.out.println("Avant appel à crudVol...");
                 volsTrouves = crudVol.chercherVol(depart, destination, dbDateString, dateRetourString, categorie);
                System.out.println("Après appel à crudVol...");
                System.out.println("Résultats: " + volsTrouves);
            }
            if (!volsTrouves.isEmpty()) {
                System.out.println("existe");
                afficherAlerte("Succès", "Vol(s) trouvé(s)", "Nombre de vols trouvés : " + volsTrouves.size());


                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReserverVol.fxml"));
                    Parent root = loader.load();

                    ReserverVolController controller = loader.getController();
                    controller.setVols(volsTrouves);

                    Stage stage = (Stage) rechercherButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("n'existe pas");
                afficherAlerte("Aucun vol", "Aucun vol trouvé", "Veuillez modifier les critères de recherche.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }
    private void afficherAlerte(String titre, String enTete, String contenu) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(enTete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

}
