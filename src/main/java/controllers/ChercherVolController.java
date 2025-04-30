package controllers;
import entities.Enumnom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CrudVol;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class ChercherVolController implements Initializable {
    @FXML
    private javafx.scene.control.RadioButton allerSimpleRadio;

    @FXML
    private javafx.scene.control.RadioButton allerRetourRadio;

    @FXML
    private javafx.scene.control.Label labelRetour;

    @FXML
    private TextField departField;

    @FXML
    private TextField destinationField;

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
        categorieChoiceBox.getItems().addAll(Enumnom.values());

        rechercherButton.setOnAction(event -> {
            System.out.println("Bouton cliqué !");
            chercherVol();});
    }
    private void chercherVol() {
        System.out.println("Début recherche...");
        String depart = departField.getText();
        String destination = destinationField.getText();
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
        if (localDate == null) {
            afficherAlerte("Erreur", "Date manquante", "Veuillez sélectionner une date");
            return;
        }

        if (categorie == null) {
            afficherAlerte("Champ manquant", "Catégorie non sélectionnée", "Veuillez choisir une catégorie.");
            return;
        }

        try {

            String dbDateString = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate retourDate = dateRetourField.getValue();
            String dateRetourString = retourDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            System.out.println("Départ : " + depart);
            System.out.println("Destination : " + destination);
            System.out.println("Date : " + dbDateString);
            System.out.println("Catégorie : " + categorie);

            System.out.println("Avant appel à crudVol...");
            var volsTrouves = crudVol.chercherVol(depart, destination, dbDateString, dateRetourString,categorie);
            System.out.println("Après appel à crudVol...");
            System.out.println("Résultats: " + volsTrouves);

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
