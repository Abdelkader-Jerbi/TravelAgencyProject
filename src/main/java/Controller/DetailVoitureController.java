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
import javafx.stage.Stage;
import services.CrudVoiture;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;

public class DetailVoitureController {

    @FXML private Label marqueLabel;
    @FXML private Label modeleLabel;
    @FXML private Label prixLabel;
    @FXML private Label matriculeLabel;
    @FXML private Label disponibleLabel;
    @FXML private ImageView imageView;
    @FXML private Button reserverButton;
    @FXML private Button annulerButton;

    // 🔔 Ajout du panier
    @FXML private ImageView panierIcon;
    @FXML private Label notificationLabel;

    private int compteurPanier = 0;

    private final CrudVoiture crudVoiture = new CrudVoiture();
    private Voiture currentVoiture;

    public void setVoitureId(int voitureId) {
        try {
            Voiture voiture = crudVoiture.getVoitureById(voitureId);
            if (voiture != null) {
                this.currentVoiture = voiture;
                updateDetails(voiture);
            } else {
                showAlert("Erreur", "Voiture non trouvée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les détails de la voiture : " + e.getMessage());
        }
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

        // 🔔 Gestion du clic sur l’icône panier (optionnel)
        if (panierIcon != null) {
            panierIcon.setOnMouseClicked(e -> showPanierWindow());
        }
    }

    private void updateDetails(Voiture voiture) {
        marqueLabel.setText("Marque : " + voiture.getMarque());
        modeleLabel.setText("Modèle : " + voiture.getModele());
        matriculeLabel.setText("Matricule : " + voiture.getMatricule());
        prixLabel.setText("Prix: " + voiture.getPrixParJour());
        disponibleLabel.setText("Disponibilité : " + voiture.isDisponible());
        loadImage(voiture.getImagePath());
    }

    private void loadImage(String imagePath) {
        try {
            Image img;
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("http") || imagePath.startsWith("file:")) {
                    img = new Image(imagePath);
                } else {
                    img = new Image(getClass().getResourceAsStream("/images/" + imagePath));
                }
            } else {
                img = new Image(getClass().getResourceAsStream("/images/default.png"));
            }
            imageView.setImage(img);
        } catch (Exception e) {
            System.err.println("Erreur chargement image : " + e.getMessage());
        }
    }

    @FXML
    private void handleReserver(ActionEvent event) {
        if (currentVoiture == null) {
            showAlert("Erreur", "Aucune voiture sélectionnée.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationVoiture.fxml"));
            Parent root = loader.load();

            ReservationVoitureController controller = loader.getController();
            controller.setVoiture(currentVoiture);

            Stage stage = new Stage();
            stage.setTitle("Réservation de la voiture");
            stage.setScene(new Scene(root));
            stage.show();

            // 🔔 Incrémenter le panier
            compteurPanier++;
            notificationLabel.setText(String.valueOf(compteurPanier));

            closeWindow();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d’ouvrir la page de réservation : " + e.getMessage());
        }
    }

    private void showPanierWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mon panier");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d’ouvrir le panier.");
        }
    }

    private void closeWindow() {
        if (annulerButton != null && annulerButton.getScene() != null) {
            Stage stage = (Stage) annulerButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
