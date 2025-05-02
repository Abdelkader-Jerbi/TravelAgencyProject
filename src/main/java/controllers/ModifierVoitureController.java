package controllers;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CrudVoiture;

public class ModifierVoitureController {

    @FXML
    private TextField marqueField;

    @FXML
    private TextField modeleField;

    @FXML
    private TextField matriculeField;

    @FXML
    private TextField prixField;

    private Voiture currentVoiture;
    private final CrudVoiture crud = new CrudVoiture();

    /**
     * Méthode appelée automatiquement lors du clic sur le bouton "Enregistrer"
     */

    @FXML
    private void enregistrerModification() {
        try {
            // Validation simple (optionnelle)
            if (marqueField.getText().isEmpty() || modeleField.getText().isEmpty()
                    || matriculeField.getText().isEmpty() || prixField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            double prix = Double.parseDouble(prixField.getText());

            currentVoiture.setMarque(marqueField.getText());
            currentVoiture.setModele(modeleField.getText());
            currentVoiture.setMatricule(matriculeField.getText());
            currentVoiture.setPrixParJour(prix);

            crud.updateVoiture(currentVoiture);

            // ✅ Message de confirmation
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La voiture a été modifiée avec succès.");

            // Fermer la fenêtre après confirmation
            Stage stage = (Stage) marqueField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le prix doit être un nombre.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }


    /**
     * Appelée par VoitureController pour remplir les champs
     */
    public void setVoiture(Voiture voiture) {
        this.currentVoiture = voiture;

        marqueField.setText(voiture.getMarque());
        modeleField.setText(voiture.getModele());
        matriculeField.setText(voiture.getMatricule());
        prixField.setText(String.valueOf(voiture.getPrixParJour()));
    }

    private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
