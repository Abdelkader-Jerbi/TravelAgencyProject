package DetailVoitureController;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CrudVoiture;

import java.io.File;
import java.sql.SQLException;

public class AjouterVoitureController {

    @FXML
    private TextField marqueField;
    @FXML
    private TextField modeleField;
    @FXML
    private TextField matriculeField;
    @FXML
    private TextField prixField;
    @FXML
    private ImageView imagePreview;

    private TableView<Voiture> voitureTable;

    private final CrudVoiture crud = new CrudVoiture();

    private String imagePath = ""; // Chemin de l'image sélectionnée

    public void setVoitureTable(TableView<Voiture> voitureTable) {
        this.voitureTable = voitureTable;
    }

    @FXML
    public void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(marqueField.getScene().getWindow());
        if (file != null) {
            imagePath = file.toURI().toString(); // Chemin utilisable par ImageView
            Image image = new Image(imagePath);
            imagePreview.setImage(image);
        }
    }

    @FXML
    public void handleAjouterVoiture() {
        String marque = marqueField.getText();
        String modele = modeleField.getText();
        String matricule = matriculeField.getText();
        Double prix;
      

        // Vérification du prix
        try {
            prix = Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            afficherErreur("Le prix doit être un nombre valide.");
            return;
        }

        // Vérification image
        if (imagePath == null || imagePath.isEmpty()) {
            afficherErreur("Veuillez sélectionner une image.");
            return;
        }

        // Création de la voiture
        boolean disponible = false;
        Voiture voiture = new Voiture(marque, modele, matricule, prix, imagePath, disponible);

        try {
            crud.ajouterVoiture(voiture);
            if (voitureTable != null) {
                voitureTable.getItems().add(voiture);
            }
            Stage stage = (Stage) marqueField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'ajout de la voiture: " + e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void handleAnnuler() {
        Stage stage = (Stage) marqueField.getScene().getWindow();
        stage.close();
    }



}
