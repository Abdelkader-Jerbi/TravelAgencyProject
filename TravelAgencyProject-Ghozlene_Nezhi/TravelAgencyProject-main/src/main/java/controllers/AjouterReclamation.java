package controllers;

import entities.Reclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.CrudReclamation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class AjouterReclamation{

    @FXML
    private TextField idReclamationTF;
    @FXML
    private TextField idUserTF;
    @FXML
    private TextField categorieTF;
    @FXML
    private TextField dateTF;
    @FXML
    private TextArea commentaireTF;


    @FXML
    void ajouterReclamation(ActionEvent event) {
        try {
            int idReclamation = Integer.parseInt(idReclamationTF.getText());
            int idUser = Integer.parseInt(idUserTF.getText());
            String categorie = categorieTF.getText();

            // Conversion de la chaîne en java.sql.Date
            String dateStr = dateTF.getText();
            java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr); // Format requis : YYYY-MM-DD

            String commentaire = commentaireTF.getText();

            // Adaptation du constructeur de Reclamation si nécessaire
            Reclamation reclamation = new Reclamation(idReclamation, idUser, categorie, sqlDate.toString(), commentaire);

            CrudReclamation crud = new CrudReclamation();
            crud.ajouter(reclamation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réclamation ajoutée avec succès !");
            alert.showAndWait();

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherReclamation.fxml")));
            idReclamationTF.getScene().setRoot(root);

        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Veuillez entrer des valeurs numériques pour les IDs.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur de format de date", "Veuillez entrer une date au format YYYY-MM-DD.");
        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        } catch (IOException e) {
            showAlert("Erreur de chargement", "Impossible de charger l'interface suivante.");
        }
    }


    @FXML
    void annulerReclamation(ActionEvent event) {
        // Réinitialise les champs du formulaire
        idReclamationTF.clear();
        idUserTF.clear();
        categorieTF.clear();
        dateTF.clear();
        commentaireTF.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
