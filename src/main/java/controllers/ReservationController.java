package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ReservationController {

    @FXML private TextField idPanierField;
    @FXML private TextField idReservationField;

    @FXML
    private void ajouterReservation() {
        if (idPanierField.getText().isEmpty() || idReservationField.getText().isEmpty()) {
            showAlert("Veuillez remplir les deux champs.");
            return;
        }

        System.out.println("Réservation ajoutée : ID Panier " + idPanierField.getText() + ", ID Réservation " + idReservationField.getText());
    }

    @FXML
    private void resetForm() {
        idPanierField.clear();
        idReservationField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur de saisie");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
