package controllers;

import entities.Voiture;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import services.CrudVoiture;

import java.util.List;

public class ReservationVoiture {

    @FXML
    private TableView<Voiture> voitureTable;

    @FXML
    private TableColumn<Voiture, String> modelColumn;

    @FXML
    private TableColumn<Voiture, Double> priceColumn;

    @FXML
    private TableColumn<Voiture, String> availabilityColumn;

    @FXML
    private TableColumn<Voiture, ImageView> imageColumn;

    @FXML
    private AnchorPane mainPane;

    private final CrudVoiture crudVoiture = new CrudVoiture();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        modelColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModele()));

        priceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPrixParJour()).asObject());

        availabilityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isDisponible() ? "Disponible" : "Indisponible"));

        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView(new Image("file:" + cellData.getValue().getImagePath()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(75);
            return new SimpleObjectProperty<>(imageView);
        });

        imageColumn.setCellFactory(column -> new TableCell<Voiture, ImageView>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : item);
            }
        });

        imageColumn.setCellFactory(col -> new VoitureImageTableCell());

        loadCarsFromDatabase();
    }

    private void loadCarsFromDatabase() {
        try {
            List<Voiture> voitures = crudVoiture.getAllVoitures();
            voitureTable.getItems().setAll(voitures);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des voitures : " + e.getMessage());
        }
    }

    private static class VoitureImageTableCell extends TableCell<Voiture, ImageView> {
        @Override
        protected void updateItem(ImageView imageView, boolean empty) {
            super.updateItem(imageView, empty);
            setGraphic(empty || imageView == null ? null : imageView);
        }
    }
}
