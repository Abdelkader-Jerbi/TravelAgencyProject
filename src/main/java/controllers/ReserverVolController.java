package controllers;

import entities.Vol;
import entities.ReservationVol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.CrudReservation;
import services.CrudVol;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReserverVolController implements Initializable {
    @FXML
    private VBox volListContainer;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Button retourButton;
    
    private List<Vol> vols;
    private List<Vol> filteredVols;
    private final CrudVol crudVol = new CrudVol();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration du bouton retour
        retourButton.setCursor(Cursor.HAND);
        retourButton.setOnAction(event -> handleRetourButtonClick());

        // Configuration du filtre
        filterComboBox.setOnAction(event -> {
            filterVols();
        });
    }

    public void setVols(List<Vol> vols) {
        this.vols = vols;
        this.filteredVols = vols;
        initializeFilterComboBox();
        afficherVols();
    }

    private void initializeFilterComboBox() {
        filterComboBox.getItems().clear();
        filterComboBox.getItems().add("Toutes les catégories");
        if (vols != null) {
            vols.stream()
                .map(vol -> vol.getCategorie().getNom().toString())
                .distinct()
                .forEach(categorie -> filterComboBox.getItems().add(categorie));
        }
        filterComboBox.setValue("Toutes les catégories");
    }

    private void filterVols() {
        String selectedCategory = filterComboBox.getValue();
        filteredVols = vols.stream()
            .filter(vol -> selectedCategory.equals("Toutes les catégories") ||
                           vol.getCategorie().getNom().toString().equals(selectedCategory))
            .collect(Collectors.toList());
        afficherVols();
    }

    private void afficherVols() {
        volListContainer.getChildren().clear();
        if (filteredVols.isEmpty()) {
            Label noResultsLabel = new Label("Aucun vol ne correspond à votre recherche");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
            volListContainer.getChildren().add(noResultsLabel);
        } else {
            for (Vol vol : filteredVols) {
                VBox card = createVolCard(vol);
                volListContainer.getChildren().add(card);
            }
        }
    }

    private VBox createVolCard(Vol vol) {
        VBox cardContainer = new VBox(15);
        cardContainer.setStyle("-fx-padding: 25;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-width: 1;" +
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // En-tête de la carte avec l'itinéraire
        HBox routeBox = new HBox(15);
        routeBox.setStyle("-fx-padding: 0 0 15 0;");
        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 28px;");
        Label depart = new Label(vol.getDepart());
        depart.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size: 20px; -fx-text-fill: #7f8c8d;");
        Label destination = new Label(vol.getDestination());
        destination.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        routeBox.getChildren().addAll(planeIcon, depart, arrow, destination);

        // Détails du vol
        VBox detailsBox = new VBox(12);
        detailsBox.setStyle("-fx-padding: 15 0; -fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        // Date
        HBox dateBox = createDetailRow("🗓", "Date", new SimpleDateFormat("dd MMMM yyyy").format(vol.getDate()));
        // Prix
        HBox prixBox = createDetailRow("💰", "Prix", String.format("%.2f TND", vol.getPrix()));
        // Catégorie
        HBox categorieBox = createDetailRow("📦", "Catégorie", 
            vol.getCategorie() != null ? String.valueOf(vol.getCategorie().getNom()) : "Non spécifié");

        detailsBox.getChildren().addAll(dateBox, prixBox, categorieBox);

        // Bouton Réserver
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-padding: 12 35; -fx-font-size: 14px;");
        reserverButton.setCursor(Cursor.HAND);
        reserverButton.setOnMouseEntered(e -> reserverButton.setStyle(
            "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 35; -fx-font-size: 14px;"
        ));
        reserverButton.setOnMouseExited(e -> reserverButton.setStyle(
            "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 12 35; -fx-font-size: 14px;"
        ));
        reserverButton.setOnAction(event -> {
            handleReservation(vol);
        });

        cardContainer.getChildren().addAll(routeBox, detailsBox, reserverButton);
        return cardContainer;
    }

    private HBox createDetailRow(String icon, String label, String value) {
        HBox row = new HBox(12);
        row.setStyle("-fx-padding: 8 15;");
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label labelText = new Label(label + " :");
        labelText.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        row.getChildren().addAll(iconLabel, labelText, valueLabel);
        return row;
    }

    @FXML
    private void handleRetourButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur de navigation", "Impossible de retourner à la page précédente.");
        }
    }

    private void handleReservation(Vol vol) {
        try {
            // Enregistrer la réservation dans la base
            CrudReservation crudReservation = new CrudReservation();
            ReservationVol r = new ReservationVol(
                    0, // idReservation (auto-incrémenté)
                    vol.getPrix(), // prix total = prix du vol
                    "En attente", // statut
                    new Date(), // date du jour
                    0, // idHotel
                    vol.getId_vol(), // idVol
                    0, // idUser
                    0  // idVoiture
            );
            crudReservation.ajouterReservationVolSeulement(r);
            // Charger la confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConfirmationReservation.fxml"));
            Parent root = loader.load();
            ConfirmationReservation controller = loader.getController();
            controller.initializeData(vol);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Confirmation de Réservation");
            stage.setScene(scene);
            stage.show();
            Stage currentStage = (Stage) retourButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur de réservation", "Impossible d'accéder à la page de confirmation. Veuillez réessayer.");
        }
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
