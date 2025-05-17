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
    private String selectedImageFileName;  // chemin relatif ex : "images/voiture1.jpg"

    @FXML
    private void enregistrerModification() {
        try {
            if (marqueField.getText().isEmpty() || modeleField.getText().isEmpty()
                    || matriculeField.getText().isEmpty() || prixField.getText().isEmpty()) {
                System.err.println("Erreur : Veuillez remplir tous les champs.");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixField.getText());
                if (prix < 0) {
                    System.err.println("Erreur : Le prix doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Erreur : Le prix doit être un nombre valide.");
                return;
            }

            currentVoiture.setMarque(marqueField.getText());
            currentVoiture.setModele(modeleField.getText());
            currentVoiture.setMatricule(matriculeField.getText());
            currentVoiture.setPrixParJour(prix);
            currentVoiture.setDisponible(disponibleCheckBox.isSelected());

            if (selectedImageFileName != null) {
                currentVoiture.setImagePath(selectedImageFileName); // ex: "images/voiture1.jpg"
            }

            crud.updateVoiture(currentVoiture);

            System.out.println("Succès : La voiture a été modifiée avec succès.");
            Stage stage = (Stage) marqueField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
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
                // Copier l’image dans le dossier "images" à la racine du projet (ex: "./images")
                File destDir = new File("./images");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, file.getName());

                // Copier l'image
                try (InputStream is = new FileInputStream(file);
                     OutputStream os = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }

                // Stocker le chemin relatif à la base de données
                selectedImageFileName = "images/" + file.getName();

                // Afficher l'image dans ImageView à partir du fichier copié
                Image image = new Image(destFile.toURI().toString());
                imageView.setImage(image);

            } catch (IOException e) {
                System.err.println("Erreur image : Impossible d'importer l'image : " + e.getMessage());
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

        if (voiture.getImagePath() != null && !voiture.getImagePath().isEmpty()) {
            try {
                // Charger l’image à partir du chemin relatif sans concaténation incorrecte
                File imageFile = new File(voiture.getImagePath());
                if (!imageFile.exists()) {
                    // Tenter dans "./src/main/resources" si l'image est packagée dans les ressources
                    imageFile = new File("./src/main/resources/" + voiture.getImagePath());
                }

                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    System.err.println("Image non trouvée : " + voiture.getImagePath());
                }

            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }
}
