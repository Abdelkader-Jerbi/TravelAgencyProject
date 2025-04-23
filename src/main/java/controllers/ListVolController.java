package controllers;

import entities.Vol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.CrudVol;
import services.VoLInterface;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListVolController  implements Initializable {
    @FXML
    private TableView<Vol> volTable;

    @FXML
    private TableColumn<Vol, Integer> idCol;

    @FXML
    private TableColumn<Vol, String> departCol;

    @FXML
    private TableColumn<Vol, String> destinationCol;

    @FXML
    private TableColumn<Vol, String> dateCol;

    @FXML
    private TableColumn<Vol, Double> prixCol;

    @FXML
    private TableColumn<Vol, String> categorieCol;
    @FXML
    private TableColumn<Vol, Void> actionCol;


    private VoLInterface volService = new CrudVol();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id_vol"));
        departCol.setCellValueFactory(new PropertyValueFactory<>("depart"));
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
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                       );
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #c0392b, #e74c3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.0, 0, 2);"
                ));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                ));
                deleteButton.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());
                    // Supprimer le vol de la base de données si nécessaire
                     volService.supprimerVol(vol.getId_vol());
                    getTableView().getItems().remove(vol);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }


}
