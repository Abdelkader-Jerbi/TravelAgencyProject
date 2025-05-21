package controllers;

import entities.Vol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ConfirmationReservation {
    @FXML
    private Label confirmationLabel;

    public void initializeData(Vol vol) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = dateFormat.format(vol.getDate());
        String dateRetourStr = vol.getDateRetour() != null ? dateFormat.format(vol.getDateRetour()) : "N/A";

        String message = String.format("""
            Votre réservation a été confirmée avec succès !

            Détails du vol :
            • Départ : %s
            • Destination : %s
            • Date de départ : %s
            • Date de retour : %s
            • Catégorie : %s
            • Prix : %.2f TND

            Pour finaliser votre réservation, veuillez procéder au paiement en cliquant sur le bouton "Payer".
            Vous serez redirigé vers notre plateforme de paiement sécurisée.

            Merci de votre confiance !
            """,
            vol.getDepart(),
            vol.getDestination(),
            dateStr,
            dateRetourStr,
            vol.getCategorie().getNom(),
            vol.getPrix()
        );

        confirmationLabel.setText(message);
    }

    @FXML
    private void handlePayerButtonClick() {
        try {
            // TODO: Implémenter la redirection vers la page de paiement
            afficherMessage("Paiement", "Redirection vers la page de paiement...");
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur de paiement", "Impossible d'accéder à la page de paiement. Veuillez réessayer.");
        }
    }

    @FXML
    public void handlePayerButtonHover() {
        confirmationLabel.getScene().lookup("#payerButton").setStyle(
            "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 30; -fx-font-size: 14px;"
        );
    }

    @FXML
    public void handlePayerButtonExit() {
        confirmationLabel.getScene().lookup("#payerButton").setStyle(
            "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 30; -fx-font-size: 14px;"
        );
    }

    @FXML
    private void handleRetourButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) confirmationLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur de navigation", "Impossible de retourner à la page précédente.");
        }
    }

    @FXML
    public void handleRetourButtonHover() {
        confirmationLabel.getScene().lookup("#retourButton").setStyle(
            "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 30; -fx-font-size: 14px;"
        );
    }

    @FXML
    public void handleRetourButtonExit() {
        confirmationLabel.getScene().lookup("#retourButton").setStyle(
            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 30; -fx-font-size: 14px;"
        );
    }

    private void afficherMessage(String titre, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String titre, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

