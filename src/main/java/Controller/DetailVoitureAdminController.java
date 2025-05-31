package Controller;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Controller.ThemeManager;

import java.io.File;

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

    @FXML
    public void initialize() {
        // Listener pour appliquer le thème dynamiquement
        imageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Parent root = newScene.getRoot();
                
                // Appliquer le thème actuel
                applyThemeToRoot(root);
                
                // Ajouter un listener pour les changements de thème
                ThemeManager.darkModeProperty().addListener((o, oldVal, newVal) -> {
                    applyThemeToRoot(root);
                });
            }
        });
    }

    public void setVoiture(Voiture voiture) {
        if (voiture == null) return;
        
        // Informations de base
        marqueLabel.setText(voiture.getMarque());
        modeleLabel.setText(voiture.getModele());
        matriculeLabel.setText(voiture.getMatricule());
        prixLabel.setText(String.format("%.0f TND", voiture.getPrixParJour()));
        
        // Ajouter un style pour la disponibilité
        disponibleLabel.setText(voiture.isDisponible() ? "Disponible" : "Non disponible");
        disponibleLabel.setStyle(voiture.isDisponible() ? "-fx-text-fill: #2e7d32;" : "-fx-text-fill: #c62828;");
        
        // Caractéristiques
        carburantLabel.setText(voiture.getCarburant());
        boiteVitesseLabel.setText(voiture.getBoiteVitesse());
        nbPlacesLabel.setText(String.valueOf(voiture.getNbPlaces()));
        nbPortesLabel.setText(String.valueOf(voiture.getNbPortes()));
        couleurLabel.setText(voiture.getCouleur());
        climatisationLabel.setText(voiture.isClimatisation() ? "Oui" : "Non");
        kilometrageLabel.setText(String.format("%d km", voiture.getKilometrage()));
        anneeLabel.setText(String.valueOf(voiture.getAnnee()));
        categorieLabel.setText(voiture.getCategorie());
        
        // Image avec meilleure gestion des erreurs
        if (voiture.getImagePath() != null && !voiture.getImagePath().isEmpty()) {
            try {
                File imgFile = new File(voiture.getImagePath());
                String fullPath = imgFile.getAbsolutePath();
                Image img = new Image("file:" + fullPath.replace("\\", "/"), 300, 200, true, true);
                imageView.setImage(img);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }
        
        // Appliquer le thème si la scène est déjà disponible
        if (imageView.getScene() != null) {
            applyThemeToRoot(imageView.getScene().getRoot());
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
    }

    /**
     * Applique le thème approprié (clair ou sombre) à l'élément racine
     * @param root L'élément racine auquel appliquer le thème
     */
    public static void applyThemeToRoot(Parent root) {
        if (ThemeManager.isDarkMode()) {
            if (!root.getStyleClass().contains("dark-root")) {
                root.getStyleClass().add("dark-root");
            }
        } else {
            root.getStyleClass().remove("dark-root");
        }
    }
}