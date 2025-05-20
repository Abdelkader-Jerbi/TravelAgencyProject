package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Hotel;
import services.CrudHotel;

import java.io.File;
import java.sql.Date;

public class EditHotel {

    @FXML private TextField nomField;
    @FXML private TextField localisationField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> chambreComboBox;
    @FXML private ComboBox<String> categorieTypeComboBox;
    @FXML private TextField nbEtoileField;
    @FXML private TextField tarifField;
    @FXML private TextField imageField;
    @FXML private TextArea descriptionField;

    private CrudHotel crudHotel;
    private Hotel currentHotel;
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    public void initialize() {
        crudHotel = new CrudHotel();

        chambreComboBox.getItems().addAll("Single", "Double", "Suite", "Deluxe");
        categorieTypeComboBox.getItems().addAll("Luxe", "Famille", "Economique", "Affaires");

        nbEtoileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nbEtoileField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        tarifField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tarifField.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

    public void setHotel(Hotel hotel) {
        this.currentHotel = hotel;
        populateFields();
    }

    private void populateFields() {
        if (currentHotel != null) {
            nomField.setText(currentHotel.getNom());
            localisationField.setText(currentHotel.getLocalisation());
            datePicker.setValue(currentHotel.getDate().toLocalDate());
            chambreComboBox.setValue(currentHotel.getChambre());
            categorieTypeComboBox.setValue(currentHotel.getCategorieType());
            nbEtoileField.setText(String.valueOf(currentHotel.getNbEtoile()));

            // ✅ Correctly format the price to string
            tarifField.setText(String.valueOf(currentHotel.getTarif()));

            imageField.setText(currentHotel.getImage());
            descriptionField.setText(currentHotel.getDescription());
        }
    }

    @FXML
    private void handleImageBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Hotel Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imageField.getScene().getWindow());
        if (selectedFile != null) {
            imageField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!validateInputs()) {
            return;
        }

        try {
            currentHotel.setNom(nomField.getText());
            currentHotel.setLocalisation(localisationField.getText());
            currentHotel.setDate(Date.valueOf(datePicker.getValue()));
            currentHotel.setChambre(chambreComboBox.getValue());
            currentHotel.setCategorieType(categorieTypeComboBox.getValue());
            currentHotel.setNbEtoile(Integer.parseInt(nbEtoileField.getText()));

            // ✅ Convert and set price
            double price = Double.parseDouble(tarifField.getText());
            currentHotel.setTarif((float) price);

            currentHotel.setImage(imageField.getText());
            currentHotel.setDescription(descriptionField.getText());

            crudHotel.modify(currentHotel);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Hotel updated successfully!");

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update hotel: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("Hotel name is required\n");
        }
        if (localisationField.getText().trim().isEmpty()) {
            errors.append("Location is required\n");
        }
        if (datePicker.getValue() == null) {
            errors.append("Date is required\n");
        }
        if (chambreComboBox.getValue() == null) {
            errors.append("Room type is required\n");
        }
        if (categorieTypeComboBox.getValue() == null) {
            errors.append("Category type is required\n");
        }
        if (nbEtoileField.getText().trim().isEmpty()) {
            errors.append("Number of stars is required\n");
        } else {
            try {
                int stars = Integer.parseInt(nbEtoileField.getText());
                if (stars < 1 || stars > 5) {
                    errors.append("Stars must be between 1 and 5\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid number of stars\n");
            }
        }
        if (tarifField.getText().trim().isEmpty()) {
            errors.append("Price is required\n");
        } else {
            try {
                double price = Double.parseDouble(tarifField.getText());
                if (price <= 0) {
                    errors.append("Price must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Invalid price format\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
