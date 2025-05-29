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

import java.io.*;

public class ModifierVoitureController {

    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private TextField matriculeField;
    @FXML private TextField prixParJourField;
    @FXML private CheckBox disponibleCheckBox;

    @FXML private ComboBox<String> carburantComboBox;
    @FXML private ComboBox<String> boiteVitesseComboBox;
    @FXML private Spinner<Integer> nbPlacesSpinner;
    @FXML private Spinner<Integer> nbPortesSpinner;
    @FXML private TextField couleurField;
    @FXML private CheckBox climatisationCheckBox;
    @FXML private TextField kilometrageField;
    @FXML private TextField anneeField;
    @FXML private ComboBox<String> categorieComboBox;

    @FXML private ImageView imageView;
    @FXML private Button importImageButton;

    private Voiture currentVoiture;
    private final CrudVoiture crud = new CrudVoiture();
    private String selectedImageFileName;  // chemin relatif vers l'image

    @FXML
    private void initialize() {
        // Initialiser les spinners avec des valeurs par défaut et plages
        nbPlacesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 4));
        nbPortesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 4));

        // Initialisation des ComboBox si besoin (déjà fait dans FXML mais on peut redéfinir au besoin)
        if (carburantComboBox.getItems().isEmpty()) {
            carburantComboBox.setItems(FXCollections.observableArrayList("Essence", "Diesel", "Électrique", "Hybride"));
        }
        if (boiteVitesseComboBox.getItems().isEmpty()) {
            boiteVitesseComboBox.setItems(FXCollections.observableArrayList("Manuelle", "Automatique"));
        }
        if (categorieComboBox.getItems().isEmpty()) {
            categorieComboBox.setItems(FXCollections.observableArrayList("Citadine", "Berline", "SUV", "Sport", "Utilitaire"));
        }
    }

    public void setVoiture(Voiture voiture) {
        this.currentVoiture = voiture;

        // Remplir les champs avec les données de la voiture
        marqueField.setText(voiture.getMarque());
        modeleField.setText(voiture.getModele());
        matriculeField.setText(voiture.getMatricule());
        prixParJourField.setText(String.valueOf(voiture.getPrixParJour()));
        disponibleCheckBox.setSelected(voiture.isDisponible());

        carburantComboBox.setValue(voiture.getCarburant());
        boiteVitesseComboBox.setValue(voiture.getBoiteVitesse());
        nbPlacesSpinner.getValueFactory().setValue(voiture.getNbPlaces());
        nbPortesSpinner.getValueFactory().setValue(voiture.getNbPortes());
        couleurField.setText(voiture.getCouleur());
        climatisationCheckBox.setSelected(voiture.isClimatisation());
        kilometrageField.setText(String.valueOf(voiture.getKilometrage()));
        anneeField.setText(String.valueOf(voiture.getAnnee()));
        categorieComboBox.setValue(voiture.getCategorie());

        // Chargement de l'image
        if (voiture.getImagePath() != null && !voiture.getImagePath().isEmpty()) {
            try {
                File imgFile = new File(voiture.getImagePath());
                Image img;
                if (imgFile.exists()) {
                    img = new Image(imgFile.toURI().toString());
                } else {
                    InputStream is = getClass().getResourceAsStream("/" + voiture.getImagePath());
                    img = (is != null) ? new Image(is) : null;
                }
                if (img != null) {
                    imageView.setImage(img);
                } else {
                    System.err.println("Image introuvable: " + voiture.getImagePath());
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void importerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(importImageButton.getScene().getWindow());
        if (file != null) {
            try {
                File destDir = new File("./images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, file.getName());

                // Copier fichier image dans le dossier ./images
                try (InputStream is = new FileInputStream(file);
                     OutputStream os = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }

                selectedImageFileName = "images/" + file.getName();

                Image image = new Image(destFile.toURI().toString());
                imageView.setImage(image);

            } catch (IOException e) {
                System.err.println("Erreur lors de l'importation de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void enregistrerModification() {
        try {
            // Vérifier si currentVoiture est null
            if (currentVoiture == null) {
                showAlert("Erreur", "Aucune voiture sélectionnée pour la modification.", Alert.AlertType.ERROR);
                return;
            }

            // Vérification des champs obligatoires (exemple simple)
            if (marqueField.getText().isEmpty() || modeleField.getText().isEmpty()
                    || matriculeField.getText().isEmpty() || prixParJourField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
                return;
            }

            // Conversion prix
            double prix;
            try {
                prix = Double.parseDouble(prixParJourField.getText());
                if (prix < 0) {
                    showAlert("Erreur", "Le prix doit être un nombre positif.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
                return;
            }

            // Conversion kilométrage
            int kilometrage = 0;
            if (!kilometrageField.getText().isEmpty()) {
                try {
                    kilometrage = Integer.parseInt(kilometrageField.getText());
                    if (kilometrage < 0) {
                        showAlert("Erreur", "Le kilométrage doit être positif.", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le kilométrage doit être un nombre valide.", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Conversion année
            int annee = 0;
            if (!anneeField.getText().isEmpty()) {
                try {
                    annee = Integer.parseInt(anneeField.getText());
                    if (annee < 1900 || annee > 2100) {
                        showAlert("Erreur", "L'année doit être comprise entre 1900 et 2100.", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'année doit être un nombre valide.", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Mettre à jour la voiture
            currentVoiture.setMarque(marqueField.getText());
            currentVoiture.setModele(modeleField.getText());
            currentVoiture.setMatricule(matriculeField.getText());
            currentVoiture.setPrixParJour(prix);
            currentVoiture.setDisponible(disponibleCheckBox.isSelected());

            currentVoiture.setCarburant(carburantComboBox.getValue());
            currentVoiture.setBoiteVitesse(boiteVitesseComboBox.getValue());
            currentVoiture.setNbPlaces(nbPlacesSpinner.getValue());
            currentVoiture.setNbPortes(nbPortesSpinner.getValue());
            currentVoiture.setCouleur(couleurField.getText());
            currentVoiture.setClimatisation(climatisationCheckBox.isSelected());
            currentVoiture.setKilometrage(kilometrage);
            currentVoiture.setAnnee(annee);
            currentVoiture.setCategorie(categorieComboBox.getValue());

            if (selectedImageFileName != null) {
                currentVoiture.setImagePath(selectedImageFileName);
            }

            crud.updateVoiture(currentVoiture);

            showAlert("Succès", "Voiture modifiée avec succès !", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre après enregistrement
            Stage stage = (Stage) marqueField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler(javafx.event.ActionEvent event) {
        // Fermer la fenêtre de modification
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
