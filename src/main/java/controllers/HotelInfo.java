package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Hotel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.CrudHotel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HotelInfo {
    @FXML private TableView<Hotel> hotelTable;
    @FXML private TableColumn<Hotel, String> colNom;
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
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox hBox = new HBox(10, editButton, deleteButton);

                    {
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

                        editButton.setGraphic(editIcon);
                        deleteButton.setGraphic(deleteIcon);

                        editButton.getStyleClass().add("edit-button");
                        deleteButton.getStyleClass().add("delete-button");

                        editButton.setOnAction(event -> {
                            int index = getIndex();
                            if (index >= 0 && index < getTableView().getItems().size()) {
                                Hotel selectedHotel = getTableView().getItems().get(index);
                                openEditHotelView(selectedHotel);
                            }
                        });

                        deleteButton.setOnAction(event -> {
                            int index = getIndex();
                            if (index >= 0 && index < getTableView().getItems().size()) {
                                Hotel hotel = getTableView().getItems().get(index);
                                try {
                                    crudHotel.delete(hotel.getIdHotel());
                                    getTableView().getItems().remove(hotel);
                                    System.out.println("🗑️ Hotel deleted: " + hotel.getNom());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
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
        hotelTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void openEditHotelView(Hotel hotel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/edithotel.fxml"));
            Parent root = loader.load();

            EditHotel controller = loader.getController();
            controller.setHotel(hotel);
            controller.setRefreshCallback(() -> {
                try {
                    loadHotels();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Edit Hotel");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
