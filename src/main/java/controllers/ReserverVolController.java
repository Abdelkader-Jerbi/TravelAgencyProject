package controllers;

import entities.Vol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CrudVol;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ReserverVolController implements Initializable {
    @FXML
    private Button retourButton;
    @FXML
    private Label departLabel;
    @FXML
    private Label destinationLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private Label categorieLabel;
    @FXML
    private VBox  volListContainer;
    private CrudVol crudVol = new CrudVol();
    private  List<Vol> vols ;
    public void setVols(List<Vol> vols) {
        this.vols = vols;
        afficherVols();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        retourButton.setCursor(Cursor.HAND);
        retourButton.setOnAction(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChercherVol.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) retourButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
    private void afficherVols() {
        volListContainer.getChildren().clear(); // On vide d'abord
        for (Vol vol : vols) {
            VBox card = createVolCard(vol);
            volListContainer.getChildren().add(card);
        }
    }
    private VBox createVolCard(Vol vol) {
        VBox cardContainer = new VBox(10);
        cardContainer.setStyle("-fx-padding: 20;" +
                "-fx-border-color: #e0e0e0;" +
                "-fx-border-width: 1;" +
                "-fx-background-color: #ffffff;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);");


        HBox routeBox = new HBox(10);
        Label planeIcon = new Label("✈️");
        planeIcon.setStyle("-fx-font-size: 24px;");
        Label depart = new Label(vol.getDepart());
        depart.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label arrow = new Label("→");
        Label destination = new Label(vol.getDestination());
        destination.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        routeBox.getChildren().addAll(planeIcon, depart, arrow, destination);

        // Date
        HBox dateBox = new HBox(10);
        Label dateIcon = new Label("🗓 Date :");
        dateIcon.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Label date = new Label(sdf.format(vol.getDate()));
        date.setStyle("-fx-text-fill: #333;");
        dateBox.getChildren().addAll(dateIcon, date);

        // Prix
        HBox prixBox = new HBox(10);
        Label prixIcon = new Label("💰 Prix :");
        prixIcon.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");
        Label prix = new Label(vol.getPrix() + " TND");
        prix.setStyle("-fx-text-fill: #333;");
        prixBox.getChildren().addAll(prixIcon, prix);

        // Catégorie
        HBox categorieBox = new HBox(10);
        Label catIcon = new Label("📦 Catégorie :");
        catIcon.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");
        Label categorie = new Label(vol.getCategorie() != null ? String.valueOf(vol.getCategorie().getNom()) : "Non spécifié");
        categorie.setStyle("-fx-text-fill: #333;");
        categorieBox.getChildren().addAll(catIcon, categorie);

        // Bouton réserver
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #2ecc71; " +
                "-fx-text-fill: white;" +
                " -fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 16;");
        reserverButton.setCursor(Cursor.HAND);
        reserverButton.setOnMouseEntered(event -> {
            reserverButton.setScaleX(1.05);
            reserverButton.setScaleY(1.05);
            reserverButton.setStyle(
                    "-fx-background-color: #27ae60; " +  // Un vert légèrement plus foncé
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 8 16;"
            );
        });
        reserverButton.setOnMouseExited(event -> {
            reserverButton.setScaleX(1.0);
            reserverButton.setScaleY(1.0);
            reserverButton.setStyle(
                    "-fx-background-color: #2ecc71; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 8 16;"
            );
        });
        cardContainer.getChildren().addAll(routeBox, dateBox, prixBox, categorieBox, reserverButton);

        return cardContainer;
}}
