package controllers;

import entities.Vol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.CrudVol;
import services.VoLInterface;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ListVolController  implements Initializable {
    @FXML
    private TableView<Vol> volTable;



    @FXML
    private TableColumn<Vol, String> departCol;

    @FXML
    private TableColumn<Vol, String> destinationCol;

    @FXML
    private TableColumn<Vol, String> dateCol;

    @FXML
    private TableColumn<Vol, String> dateRetourCol;

    @FXML
    private TableColumn<Vol, String> prixFinalCol;

    @FXML
    private TableColumn<Vol, Double> prixCol;

    @FXML
    private TableColumn<Vol, String> categorieCol;
    @FXML
    private TableColumn<Vol, Void> actionCol;


    private VoLInterface volService = new CrudVol();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        departCol.setCellValueFactory(new PropertyValueFactory<>("depart"));
        dateRetourCol.setCellValueFactory(cellData -> {
            Date retourDate = cellData.getValue().getDateRetour();
            String formattedRetour = retourDate != null
                    ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(retourDate)
                    : "—"; // ou "" si tu préfères
            return new javafx.beans.property.SimpleStringProperty(formattedRetour);
        });

        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateCol.setCellValueFactory(cellData -> {
            // On formate la date en String
            String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(cellData.getValue().getDate());
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        categorieCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCategorie().getNom().toString()
                )
        );
        prixFinalCol.setCellValueFactory(cellData ->
        {
            double prixFinal = volService.calculerPrixFinal(cellData.getValue());
            String prixFormate = String.format("%.2f DT", prixFinal);
            return new javafx.beans.property.SimpleStringProperty(prixFormate);
        });

        afficherVols();
        ajouterBoutonSuppression();
    }
    private void afficherVols() {
        List<Vol> vols = volService.getAllVols();
        ObservableList<Vol> observableVols = FXCollections.observableArrayList(vols);
        volTable.setItems(observableVols);
    }
    private void ajouterBoutonSuppression() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton= new Button();  ;
            private final Button editButton = new Button(" Modifier");
            private final HBox buttonBox = new HBox(10);
            {
        ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream("/icones/delete.png"))
        );
        icon.setFitHeight(16);
        icon.setFitWidth(16);
                deleteButton.setText(" Supprimer");
                deleteButton.setGraphic(icon);
                deleteButton.setContentDisplay(ContentDisplay.RIGHT);

                deleteButton.setStyle("-fx-background-color: #ff5c5c; -fx-text-fill: white; -fx-background-radius: 15;");


                deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right,  #ff7f7f, #e74c3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                       );
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #e74c3c,  #ff7f7f);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.0, 0, 2);"
                ));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #ff7f7f,#e74c3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                ));

                deleteButton.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());

                     volService.supprimerVol(vol.getId_vol());
                    getTableView().getItems().remove(vol);
                });
                ImageView editIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("/icones/icons8-edit-30.png")) // à créer/ajouter dans resources
                );
                editIcon.setFitHeight(16);
                editIcon.setFitWidth(16);
                editButton.setGraphic(editIcon);
                editButton.setContentDisplay(ContentDisplay.RIGHT);
                editButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
                                "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;" +
                                "-fx-cursor: hand; -fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                );

                editButton.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());
                    System.out.println("ID du vol sélectionné : " + vol.getId_vol());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVol.fxml"));
                        Parent root = loader.load();

                        ModifierVolController controller = loader.getController();
                        controller.setVol(vol);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Vol");
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                        stage.setScene(scene);
                        stage.showAndWait();

                        // Rafraîchir la table après modification
                        afficherVols();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                buttonBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox); // ← Affiche les deux boutons
                }
            }
        });
    }


    public void ajouterVol(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVol.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Vol");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
