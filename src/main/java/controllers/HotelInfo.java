package controllers;

import entities.Hotel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import services.CrudHotel;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HotelInfo {
    @FXML private TableView<Hotel> hotelTable;
    @FXML
    private TableColumn<Hotel, String> colNom;
    @FXML private TableColumn<Hotel, String> colDestination;
    @FXML private TableColumn<Hotel, Date> colDate;
    @FXML private TableColumn<Hotel, Integer> colNbNuite;
    @FXML private TableColumn<Hotel, Integer> colNbChambre;
    @FXML private TableColumn<Hotel, Integer> colEtoile;
    @FXML private TableColumn<Hotel, Float> colTarif;
    @FXML private TableColumn<Hotel, Void> colAction;


    CrudHotel crudHotel = new CrudHotel();
    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colNbNuite.setCellValueFactory(new PropertyValueFactory<>("nombreNuité"));
        colNbChambre.setCellValueFactory(new PropertyValueFactory<>("nombreChambre"));
        colEtoile.setCellValueFactory(new PropertyValueFactory<>("nbEtoile"));
        colTarif.setCellValueFactory(new PropertyValueFactory<>("tarif"));

        try {
            loadHotels();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addActionButtonsToTable();
    }

    public void loadHotels() throws SQLException {
        List<Hotel> hotelList = crudHotel.afficher();
        ObservableList<Hotel> observableList = FXCollections.observableArrayList(hotelList);
        hotelTable.setItems(observableList);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Hotel, Void>, TableCell<Hotel, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Hotel, Void> call(final TableColumn<Hotel, Void> param) {
                return new TableCell<>() {

                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox hBox = new HBox(10, editButton, deleteButton);

                    {
                        // Optional styling
                        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        editButton.setOnAction(event -> {
                            Hotel hotel = getTableView().getItems().get(getIndex());
                            System.out.println("✏️ Edit hotel: " + hotel.getNom());
                            // TODO: Open an edit form and pass `hotel`
                        });

                        deleteButton.setOnAction(event -> {
                            Hotel hotel = getTableView().getItems().get(getIndex());
                            try {
                                crudHotel.delete(hotel.getIdHotel() );
                                getTableView().getItems().remove(hotel);
                                System.out.println("🗑️ Hotel deleted: " + hotel.getNom());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };

        colAction.setCellFactory(cellFactory);
    }
}




