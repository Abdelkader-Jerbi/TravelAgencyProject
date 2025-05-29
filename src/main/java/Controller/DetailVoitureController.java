package Controller;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.CrudVoiture;
import Controller.ThemeManager;

import java.io.File;
import java.sql.SQLException;
import javafx.event.ActionEvent;

public class DetailVoitureController {

    @FXML private ImageView imageView;
    @FXML private Label marqueLabel;
    @FXML private Label modeleLabel;
    @FXML private Label prixLabel;
    @FXML private Label disponibleLabel;
    @FXML private Label placesLabel;
    @FXML private Label boiteVitesseLabel;
    @FXML private Label carburantLabel;
    @FXML private Label kilometrageLabel;
    @FXML private Label couleurLabel;
    @FXML private Label climatisationLabel;
    @FXML private Text descriptionText;
    @FXML private Button reserverButton;
    @FXML private Button annulerButton;

    // 🔔 Ajout du panier
    @FXML private ImageView panierIcon;
    @FXML private Label notificationLabel;

    private int compteurPanier = 0;

    private int voitureId;
    private final CrudVoiture crudVoiture = new CrudVoiture();

    public void setVoitureId(int id) {
        this.voitureId = id;
        loadVoitureDetails();
    }

    @FXML
    public void initialize() {
        if (reserverButton != null) {
            reserverButton.setOnAction(this::handleReserver);
        }
        if (annulerButton != null) {
            annulerButton.setOnAction(e -> closeWindow());
        }

        // 🔔 Initialiser le compteur panier
        if (notificationLabel != null) {
            notificationLabel.setText(String.valueOf(compteurPanier));
        }

        // 🔔 Gestion du clic sur l'icône panier (optionnel)
        if (panierIcon != null) {
            panierIcon.setOnMouseClicked(e -> showPanierWindow());
        }

        // Listener pour appliquer le thème dynamiquement
        annulerButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Parent root = newScene.getRoot();
                Controller.ThemeManager.darkModeProperty().addListener((o, oldVal, newVal) -> {
                    if (newVal) {
                        if (!root.getStyleClass().contains("dark-root")) {
                            root.getStyleClass().add("dark-root");
                        }
                    } else {
                        root.getStyleClass().remove("dark-root");
                    }
                });
            }
        });
    }

    private void loadVoitureDetails() {
        try {
            Voiture voiture = crudVoiture.getVoitureById(voitureId);
            if (voiture != null) {
                // Informations de base
                marqueLabel.setText(voiture.getMarque());
                modeleLabel.setText(voiture.getModele());
                prixLabel.setText(String.format("%.0f TND", voiture.getPrixParJour()));
                disponibleLabel.setText(voiture.isDisponible() ? "Disponible" : "Non disponible");
                disponibleLabel.setStyle(voiture.isDisponible() ? "-fx-text-fill: #2e7d32;" : "-fx-text-fill: #c62828;");

                // Caractéristiques
                placesLabel.setText(String.valueOf(voiture.getNbPlaces()));
                boiteVitesseLabel.setText(voiture.getBoiteVitesse());
                carburantLabel.setText(voiture.getCarburant());
                kilometrageLabel.setText(String.format("%d km", voiture.getKilometrage()));
                couleurLabel.setText(voiture.getCouleur());
                climatisationLabel.setText(voiture.isClimatisation() ? "Oui" : "Non");

                // Description
                descriptionText.setText(String.format(
                    "Cette %s %s est une voiture %s avec %d places. Elle est équipée d'une boîte de vitesse %s " +
                    "et fonctionne au %s. Avec %d km au compteur, elle est en excellent état et prête à vous " +
                    "accompagner dans tous vos déplacements.",
                    voiture.getMarque(),
                    voiture.getModele(),
                    voiture.getCategorie().toLowerCase(),
                    voiture.getNbPlaces(),
                    voiture.getBoiteVitesse().toLowerCase(),
                    voiture.getCarburant().toLowerCase(),
                    voiture.getKilometrage()
                ));

                // Image
                loadImage(voiture.getImagePath());

                // État du bouton de réservation
                reserverButton.setDisable(!voiture.isDisponible());
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des détails", e.getMessage());
        }
    }

    private void loadImage(String imagePath) {
        try {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                File imgFile = new File(imagePath);
                String fullPath = imgFile.getAbsolutePath();
                Image img = new Image("file:" + fullPath.replace("\\", "/"), 400, 300, true, true);
                imageView.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }
    }

    @FXML
    private void handleReserver(ActionEvent event) {
        if (voitureId == 0) {
            showError("Erreur", "Aucune voiture sélectionnée.");
            return;
        }

        try {
            Voiture voiture = crudVoiture.getVoitureById(voitureId);
            if (voiture == null) {
                showError("Erreur", "Voiture non trouvée.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationVoiture.fxml"));
            Parent root = loader.load();

            ReservationVoitureController controller = loader.getController();
            controller.setVoiture(voiture);

            Stage stage = new Stage();
            stage.setTitle("Réservation de la voiture");
            stage.setScene(new Scene(root));
            stage.show();

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir la page de réservation : " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        closeWindow();
    }

    private void showPanierWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mon panier");
            stage.setScene(new Scene(root));
            // Appliquer le thème courant
            if (ThemeManager.isDarkMode()) {
                root.getStyleClass().add("dark-root");
            } else {
                root.getStyleClass().remove("dark-root");
            }
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le panier.");
        }
    }

    private void closeWindow() {
        if (annulerButton != null && annulerButton.getScene() != null) {
            Stage stage = (Stage) annulerButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
