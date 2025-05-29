package Controller;

import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CrudVoiture;
import Controller.ReservationVoitureController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static com.sun.javafx.css.FontFaceImpl.FontFaceSrcType.URL;


public class AjouterVoitureController {

    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private TextField matriculeField;
    @FXML private TextField prixParJourField;
    @FXML private ComboBox<String> disponibleComboBox;
    @FXML private ComboBox<String> carburantComboBox;
    @FXML private ComboBox<String> boiteVitesseComboBox;
    @FXML private TextField nbPlacesField;
    @FXML private TextField nbPortesField;
    @FXML private TextField couleurField;
    @FXML private CheckBox climatisationCheckBox;
    @FXML private TextField kilometrageField;
    @FXML private TextField anneeField;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ImageView imagePreview;
    @FXML private Label imageStatusLabel;


    private TableView<Voiture> voitureTable;
    private final CrudVoiture crud = new CrudVoiture();
    private String imagePath = "";

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        disponibleComboBox.setItems(FXCollections.observableArrayList("Oui", "Non"));
        carburantComboBox.setItems(FXCollections.observableArrayList("Essence", "Diesel", "Électrique", "Hybride"));
        boiteVitesseComboBox.setItems(FXCollections.observableArrayList("Manuelle", "Automatique"));
        categorieComboBox.setItems(FXCollections.observableArrayList("Citadine", "Berline", "SUV", "Sport", "Utilitaire"));

        // Sélection par défaut
        disponibleComboBox.getSelectionModel().select("Oui");
        carburantComboBox.getSelectionModel().selectFirst();
        boiteVitesseComboBox.getSelectionModel().selectFirst();
        categorieComboBox.getSelectionModel().selectFirst();

        // Label d'image par défaut
        if (imageStatusLabel != null) {
            imageStatusLabel.setText("Aucune image sélectionnée");
        }
    }

    public void setVoitureTable(TableView<Voiture> voitureTable) {
        this.voitureTable = voitureTable;
    }

    @FXML
    public void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(marqueField.getScene().getWindow());

        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
                afficherErreur("Format d'image non supporté. Veuillez choisir une image .png, .jpg ou .jpeg.");
                if (imageStatusLabel != null) imageStatusLabel.setText("Aucune image sélectionnée");
                return;
            }

            try {
                File destDir = new File("C:/images_voitures/");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagePath = destFile.getAbsolutePath().replace("\\", "/");
                imagePreview.setImage(new Image(destFile.toURI().toString()));
                if (imageStatusLabel != null) imageStatusLabel.setText("Image : " + selectedFile.getName());
            } catch (IOException e) {
                afficherErreur("Erreur lors de la copie de l'image : " + e.getMessage());
                if (imageStatusLabel != null) imageStatusLabel.setText("Aucune image sélectionnée");
            }
        } else {
            if (imageStatusLabel != null) imageStatusLabel.setText("Aucune image sélectionnée");
        }
    }

    @FXML
    public void handleAjouterVoiture() {
        if (!validerChamps()) return;

        try {
            Voiture voiture = new Voiture(
                    0,
                    marqueField.getText().trim(),
                    modeleField.getText().trim(),
                    matriculeField.getText().trim(),
                    Double.parseDouble(prixParJourField.getText().trim()),
                    imagePath,
                    "Oui".equals(disponibleComboBox.getValue()),
                    carburantComboBox.getValue(),
                    boiteVitesseComboBox.getValue(),
                    Integer.parseInt(nbPlacesField.getText().trim()),
                    Integer.parseInt(nbPortesField.getText().trim()),
                    couleurField.getText().trim(),
                    climatisationCheckBox.isSelected(),
                    Integer.parseInt(kilometrageField.getText().trim()),
                    Integer.parseInt(anneeField.getText().trim()),
                    categorieComboBox.getValue()
            );

            crud.ajouterVoiture(voiture);

            if (voitureTable != null) {
                voitureTable.getItems().add(voiture);
            }

            afficherConfirmation("Voiture ajoutée avec succès !");
            closeWindow();

        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'enregistrement dans la base : " + e.getMessage());
        } catch (Exception e) {
            afficherErreur("Une erreur est survenue : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        // Vérifie les champs obligatoires
        if (champVide(marqueField) || champVide(modeleField) || champVide(matriculeField)
                || champVide(prixParJourField) || champVide(kilometrageField)
                || champVide(anneeField) || champVide(couleurField) || champVide(nbPlacesField)
                || champVide(nbPortesField) || imagePath.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs obligatoires et choisir une image.");
            return false;
        }

        try {
            double prix = Double.parseDouble(prixParJourField.getText().trim());
            int km = Integer.parseInt(kilometrageField.getText().trim());
            int annee = Integer.parseInt(anneeField.getText().trim());
            int nbPlaces = Integer.parseInt(nbPlacesField.getText().trim());
            int nbPortes = Integer.parseInt(nbPortesField.getText().trim());

            if (prix < 0 || km < 0 || annee < 1900 || annee > 2100 
                || nbPlaces < 1 || nbPlaces > 9 || nbPortes < 2 || nbPortes > 6) {
                afficherErreur("Veuillez entrer des valeurs valides (prix, kilométrage, année, nombre de places et portes).");
                return false;
            }
        } catch (NumberFormatException e) {
            afficherErreur("Les champs numériques doivent contenir des chiffres valides.");
            return false;
        }

        return true;
    }

    private boolean champVide(TextField champ) {
        return champ.getText() == null || champ.getText().trim().isEmpty();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur de validation");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) marqueField.getScene().getWindow();
        stage.close();
    }
}
