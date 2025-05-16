package Controller;

import entities.Voiture;
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
    @FXML private TextField prixField;
    @FXML private CheckBox disponibleCheckBox;
    @FXML private ImageView imageView;
    @FXML private Button importImageButton;

    private Voiture currentVoiture;
    private final CrudVoiture crud = new CrudVoiture();
    private String selectedImageFileName;

    @FXML
    private void enregistrerModification() {
        try {
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
            currentVoiture.setDisponible(disponibleCheckBox.isSelected());

            if (selectedImageFileName != null) {
                currentVoiture.setImagePath(selectedImageFileName);
            }

            crud.updateVoiture(currentVoiture);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "La voiture a été modifiée avec succès.");
            Stage stage = (Stage) marqueField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le prix doit être un nombre.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    @FXML
    private void importerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                File destDir = new File("src/main/resources/images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, file.getName());
                try (FileInputStream fis = new FileInputStream(file);
                     FileOutputStream fos = new FileOutputStream(destFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }

                Image image = new Image(destFile.toURI().toString());
                imageView.setImage(image);

                selectedImageFileName = "images/" + file.getName();

            } catch (IOException e) {
                System.err.println("Erreur lors de l'importation de l'image : " + e.getMessage());
            }
        }
    }

    public void setVoiture(Voiture voiture) {
        this.currentVoiture = voiture;

        marqueField.setText(voiture.getMarque());
        modeleField.setText(voiture.getModele());
        matriculeField.setText(voiture.getMatricule());
        prixField.setText(String.valueOf(voiture.getPrixParJour()));
        disponibleCheckBox.setSelected(voiture.isDisponible());

        if (voiture.getImagePath() != null) {
            try {
                Image image = new Image(new File("src/main/resources/" + voiture.getImagePath()).toURI().toString());
                imageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur chargement image : " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
