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
import java.sql.Date;  // Import java.sql.Date
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class AddHotel {

    @FXML private TextField nomHotelField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker; // Changed from TextField to DatePicker
    @FXML private TextField nbNuiteField;
    @FXML private TextField nombreChambreField;
    @FXML private TextField nbEtoilesField;
    @FXML private TextField tarifField;
    @FXML private Button addButton;

    CrudHotel crudHotel = new CrudHotel();

    @FXML
    public void initialize() {
        // Initialize the date picker with today's date
        datePicker.setValue(LocalDate.now());

        addButton.setOnAction(e -> {
            try {
                addHotel(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void addHotel(ActionEvent event) throws SQLException {
        String nom = nomHotelField.getText();
        LocalDate localDate = datePicker.getValue();

        // Convert LocalDate to java.sql.Date
        Date date = null;
        if (localDate != null) {
            // Convert LocalDate to java.util.Date first, then to java.sql.Date
            Calendar calendar = Calendar.getInstance();
            calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            date = new Date(calendar.getTimeInMillis());
        }

        try {
            String destination = destinationField.getText();
            int nombreNuité = Integer.parseInt(nbNuiteField.getText());
            int nbEtoile = Integer.parseInt(nbEtoilesField.getText());
            int nombreChambre = Integer.parseInt(nombreChambreField.getText());
            float tarif = Float.parseFloat(tarifField.getText());

            Hotel hotel = new Hotel(nom, destination, date, nombreNuité, nbEtoile, nombreChambre, tarif);
            crudHotel.add(hotel);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Hotel added successfully!");
            successAlert.showAndWait();
        } catch (Exception e) {
            // Show warning, but DO NOT return — allow navigation
            showAlert("Warning", "Some fields were invalid or empty. Continuing anyway.", Alert.AlertType.WARNING);
        }

        // Always switch to hotel table, regardless of errors
        switchToHotelTable(event);
        clearFields();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchToHotelTable(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/hoteltable.fxml"));
            Parent root = loader.load();

            // Access the controller of the next view
            HotelInfo controller = loader.getController();
            controller.loadHotels();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        nomHotelField.clear();
        destinationField.clear();
        datePicker.setValue(LocalDate.now()); // Reset to today's date
        nbNuiteField.clear();
        nbEtoilesField.clear();
        nombreChambreField.clear();
        tarifField.clear();
    }
}
