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
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

public class AddHotel {

    @FXML private TextField nomHotelField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField nbNuiteField;
    @FXML private TextField nombreChambreField;
    @FXML private TextField nbEtoilesField;
    @FXML private TextField tarifField;
    @FXML private Button addButton;

    private final CrudHotel crudHotel = new CrudHotel();
    private Runnable refreshCallback; // ✅ Callback for refreshing hotel table

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    public void initialize() {
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
        Date date = null;

        if (localDate != null) {
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

            // ✅ Refresh the table
            if (refreshCallback != null) {
                refreshCallback.run();
            }

            showAlert("Success", "Hotel added successfully!", Alert.AlertType.INFORMATION);

            // ✅ Close the window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            // The alert is removed, and no message will be shown in case of exception.
        }

        clearFields();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void clearFields() {
        nomHotelField.clear();
        destinationField.clear();
        datePicker.setValue(LocalDate.now());
        nbNuiteField.clear();
        nbEtoilesField.clear();
        nombreChambreField.clear();
        tarifField.clear();
    }
}
