package controllers;
import entities.Hotel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CrudHotel;

import javafx.event.ActionEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class AddHotel {

    @FXML private TableView<Hotel> hotelTable;
    @FXML private TextField nomHotelField;
    @FXML private TextField destinationField;
    @FXML private TextField dateField;
    @FXML private TextField nbNuiteField;
    @FXML private TextField nombreChambreField;
    @FXML private TextField nbEtoilesField;
    @FXML private TextField tarifField;
    @FXML private Button addButton;
    @FXML

    CrudHotel crudHotel = new CrudHotel();
    @FXML
    public void initialize() {
        addButton.setOnAction(e -> {
            try {
                addHotel(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
    private void addHotel(ActionEvent event) throws SQLException{
        String nom = nomHotelField.getText();
        String dateString = dateField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;

        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Date Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid date format! Use yyyy-MM-dd.");
            alert.showAndWait();
            return;
        }

        String destination = destinationField.getText();
        int nombreNuité = Integer.parseInt(nbNuiteField.getText());
        int nbEtoile = Integer.parseInt(nbEtoilesField.getText());
        int nombreChambre = Integer.parseInt(nombreChambreField.getText());
        float tarif = Float.parseFloat(tarifField.getText());

        Hotel hotel = new Hotel(nom, destination, date, nombreNuité, nbEtoile, nombreChambre, tarif);

        crudHotel.add(hotel);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Hotel added successfully!");
        alert.showAndWait();

        // 👉 Navigate to table view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hoteltable.fxml"));
            Parent root = loader.load();

            // Optional: Access the controller of the next view
            HotelInfo controller = loader.getController();
            controller.loadHotels(); // Make sure this method exists in HotelInfo

            // Switch scenes
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        switchToHotelTable(event);
        clearFields();
        }


    private void switchToHotelTable(ActionEvent event) {
        try {
            // Load the hotel table view FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hoteltable.fxml"));
            Parent root = loader.load();

            // Set the new scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
            private void clearFields () {
                nomHotelField.clear();
                destinationField.clear();
                dateField.clear();
                nbNuiteField.clear();
                nbEtoilesField.clear();
                nombreChambreField.clear();
                tarifField.clear();
            }

}
