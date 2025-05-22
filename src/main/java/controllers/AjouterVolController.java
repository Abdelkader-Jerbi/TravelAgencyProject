package controllers;

import entities.CategorieVol;
import entities.Enumnom;
import entities.StatutVol;
import entities.Vol;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import services.CrudVol;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterVolController  implements Initializable {

    @FXML
    private ComboBox<String> departField;

    @FXML
    private ComboBox<String> destinationField;


    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker dateRetourPicker;

    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<Enumnom> categorieComboBox;
    @FXML
    private ComboBox<StatutVol> statutComboBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categorieComboBox.getItems().setAll(Enumnom.values());
        statutComboBox.getItems().setAll(StatutVol.values());
        departField.setEditable(true);
        destinationField.setEditable(true);

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


    @FXML
    public void ajouterVol(ActionEvent event) {
        String depart = departField.getValue();
        StatutVol statut = statutComboBox.getValue();
        String destination = destinationField.getValue();
        LocalDate dateDepart = datePicker.getValue();
        LocalDate dateRetour = dateRetourPicker.getValue();
        String prixText = prixField.getText();
        Enumnom selectedNom = categorieComboBox.getValue();

        if (statut == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez sélectionner un statut.");
            return;
        }

        if (depart.isEmpty() || destination.isEmpty() || dateDepart == null || dateRetour == null || prixText.isEmpty() || selectedNom == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.");
            return;
        }
        if (dateRetour.isBefore(dateDepart)) {
            showAlert(Alert.AlertType.ERROR, "La date de retour doit être postérieure à la date de départ.");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Prix invalide.");
            return;
        }

        // Vérifier si la catégorie existe dans la base de données
        CrudVol crudVol = new CrudVol();
        CategorieVol categorie = null;
        try {
            categorie = crudVol.getCategorieByNom(selectedNom.toString());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la vérification de la catégorie.");
            e.printStackTrace();
            return;
        }

        // Si la catégorie n'existe pas dans la base de données, afficher une alerte
        if (categorie == null) {
            showAlert(Alert.AlertType.ERROR, "La catégorie sélectionnée n'existe pas dans la base de données.");
            return;
        }

        // Création du vol avec la catégorie existante
        Date dateDepartConverted = java.sql.Date.valueOf(dateDepart);
        Date dateRetourConverted = java.sql.Date.valueOf(dateRetour);
        Vol vol = new Vol(depart, destination, dateDepartConverted, dateRetourConverted, prix, categorie, statut);


        try {
            crudVol.ajouterVol(vol); // Appel à la méthode ajouterVol du service
            showAlert(Alert.AlertType.INFORMATION, "Vol ajouté avec succès !");
            redirigerVersListeVols();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du vol.");
            e.printStackTrace();
        }

        clearFields();
    }
    private void redirigerVersListeVols() {
        try {
            // Charger la scène de la liste des vols (assure-toi d'avoir un fichier FXML pour la liste des vols)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListVol.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir le stage courant
            Stage stage = (Stage) departField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la navigation vers la liste des vols.");
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        departField.setValue(null);         // Pour ComboBox
        destinationField.setValue(null);
        datePicker.setValue(null);
        dateRetourPicker.setValue(null);
        prixField.clear();
        categorieComboBox.setValue(null);
    }


}