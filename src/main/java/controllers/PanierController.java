package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PanierController {

    @FXML private TextField idPanierField, idReservationField, idVolField, idHebergementField, createdAtField, totalField;
    @FXML private ComboBox<String> statutCombo;

    @FXML
    public void initialize() {
        statutCombo.getItems().addAll("validé", "pending");
    }

    @FXML
    private void ajouterPanier() {
        if (idPanierField.getText().isEmpty() || totalField.getText().isEmpty() || statutCombo.getValue() == null) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return;
        }
        System.out.println("Panier ajouté : ID " + idPanierField.getText() + " | Total : " + totalField.getText() + " €");
    }

    @FXML
    private void resetForm() {
        idPanierField.clear();
        idReservationField.clear();
        idVolField.clear();
        idHebergementField.clear();
        createdAtField.clear();
        totalField.clear();
        statutCombo.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Champ manquant");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
