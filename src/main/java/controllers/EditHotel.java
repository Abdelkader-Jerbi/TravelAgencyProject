package controllers;

import entities.Hotel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CrudHotel;

import java.sql.Date;  // Import java.sql.Date
import java.sql.SQLException;

public class EditHotel {
    @FXML private TextField nomField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField nbNuiteField;
    @FXML private TextField nbChambreField;
    @FXML private TextField etoileField;
    @FXML private TextField tarifField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Hotel currentHotel;
    private Runnable refreshCallback;

    private final CrudHotel crudHotel = new CrudHotel();

    // Set the hotel to be edited
    public void setHotel(Hotel hotel) {
        this.currentHotel = hotel;
        if (hotel != null) {
            nomField.setText(hotel.getNom());
            destinationField.setText(hotel.getLocalisation());
            datePicker.setValue(hotel.getDate().toLocalDate());  // Convert java.sql.Date to LocalDate
            nbNuiteField.setText(String.valueOf(hotel.getNombreNuité()));
            nbChambreField.setText(String.valueOf(hotel.getNombreChambre()));
            etoileField.setText(String.valueOf(hotel.getNbEtoile()));
            tarifField.setText(String.valueOf(hotel.getTarif()));
        }
    }

    // Set the callback for refreshing
    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    // Initialize the buttons' actions
    @FXML
    private void initialize() {
        saveButton.setOnAction(event -> {
            if (currentHotel != null) {
                currentHotel.setNom(nomField.getText());
                currentHotel.setLocalisation(destinationField.getText());

                // Convert LocalDate to java.sql.Date
                Date date = Date.valueOf(datePicker.getValue());  // Convert LocalDate to java.sql.Date
                currentHotel.setDate(date);

                currentHotel.setNombreNuité(Integer.parseInt(nbNuiteField.getText()));
                currentHotel.setNombreChambre(Integer.parseInt(nbChambreField.getText()));
                currentHotel.setNbEtoile(Integer.parseInt(etoileField.getText()));
                currentHotel.setTarif(Float.parseFloat(tarifField.getText()));

                try {
                    crudHotel.modify(currentHotel);  // Save modified hotel
                    if (refreshCallback != null) refreshCallback.run();  // Refresh callback
                    closeWindow();  // Close window after saving
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setOnAction(event -> closeWindow());  // Close window on cancel
    }

    // Close the current window
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
