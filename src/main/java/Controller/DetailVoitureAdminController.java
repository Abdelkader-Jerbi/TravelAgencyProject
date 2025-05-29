package Controller;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Controller.ThemeManager;

public class DetailVoitureAdminController {
    @FXML private ImageView imageView;
    @FXML private Label marqueLabel;
    @FXML private Label modeleLabel;
    @FXML private Label matriculeLabel;
    @FXML private Label prixLabel;
    @FXML private Label disponibleLabel;
    @FXML private Label carburantLabel;
    @FXML private Label boiteVitesseLabel;
    @FXML private Label nbPlacesLabel;
    @FXML private Label nbPortesLabel;
    @FXML private Label couleurLabel;
    @FXML private Label climatisationLabel;
    @FXML private Label kilometrageLabel;
    @FXML private Label anneeLabel;
    @FXML private Label categorieLabel;

    public void setVoiture(Voiture voiture) {
        if (voiture == null) return;
        marqueLabel.setText(voiture.getMarque());
        modeleLabel.setText(voiture.getModele());
        matriculeLabel.setText(voiture.getMatricule());
        prixLabel.setText(String.valueOf(voiture.getPrixParJour()));
        disponibleLabel.setText(voiture.isDisponible() ? "Oui" : "Non");
        carburantLabel.setText(voiture.getCarburant());
        boiteVitesseLabel.setText(voiture.getBoiteVitesse());
        nbPlacesLabel.setText(String.valueOf(voiture.getNbPlaces()));
        nbPortesLabel.setText(String.valueOf(voiture.getNbPortes()));
        couleurLabel.setText(voiture.getCouleur());
        climatisationLabel.setText(voiture.isClimatisation() ? "Oui" : "Non");
        kilometrageLabel.setText(String.valueOf(voiture.getKilometrage()));
        anneeLabel.setText(String.valueOf(voiture.getAnnee()));
        categorieLabel.setText(voiture.getCategorie());
        // Image
        if (voiture.getImagePath() != null && !voiture.getImagePath().isEmpty()) {
            try {
                Image img = new Image("file:" + voiture.getImagePath());
                imageView.setImage(img);
            } catch (Exception e) {
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
    }

    public static void applyThemeToRoot(javafx.scene.Parent root) {
        if (ThemeManager.isDarkMode()) {
            if (!root.getStyleClass().contains("dark-root")) {
                root.getStyleClass().add("dark-root");
            }
        } else {
            root.getStyleClass().remove("dark-root");
        }
    }
} 