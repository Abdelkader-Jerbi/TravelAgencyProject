package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TravelAgencyController implements Initializable {

    @FXML
    private BorderPane mainPane;
    
    @FXML
    private Button usersButton;
    
    @FXML
    private Button flightsButton;
    
    @FXML
    private Button hotelsButton;
    
    @FXML
    private Button carsButton;
    
    @FXML
    private Button reclamationsButton;
    
    @FXML
    private Button reservationsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize dashboard - could load a default view
        System.out.println("Travel Agency Dashboard initialized");
    }

    @FXML
    public void navigateToUsers(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/AfficherUtilisateur.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Users Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Users interface: " + e.getMessage());
            showNotImplementedMessage("Users Management");
        }
    }

    @FXML
    public void navigateToFlights(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/ListVol.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Flights Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Flights interface: " + e.getMessage());
            showNotImplementedMessage("Flights Management");
        }
    }

    @FXML
    public void navigateToHotels(ActionEvent event) {
        try {
            // Assuming there will be a Hotels interface
            URL resource = getClass().getResource("/View/hoteltable.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Hotels Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Hotels interface: " + e.getMessage());
            showNotImplementedMessage("Hotels Management");
        }
    }

    @FXML
    public void navigateToCars(ActionEvent event) {
        try {
            // Assuming there will be a Cars interface
            URL resource = getClass().getResource("/ListCar.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Cars Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Cars interface: " + e.getMessage());
            showNotImplementedMessage("Cars Management");
        }
    }

    @FXML
    public void navigateToReclamations(ActionEvent event) {
        try {
            // Assuming there will be a Reclamations interface
            URL resource = getClass().getResource("/Reclamation/ListReclamation.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Reclamations Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Reclamations interface: " + e.getMessage());
            showNotImplementedMessage("Reclamations Management");
        }
    }

    @FXML
    public void navigateToReservations(ActionEvent event) {
        try {
            // Assuming there will be a Reservations interface
            URL resource = getClass().getResource("/ListReservation.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Reservations Management");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Reservations interface: " + e.getMessage());
            showNotImplementedMessage("Reservations Management");
        }
    }
    
    private void showNotImplementedMessage(String feature) {
        VBox messageBox = new VBox();
        messageBox.setAlignment(javafx.geometry.Pos.CENTER);
        messageBox.setSpacing(20);
        
        Label titleLabel = new Label(feature + " - Coming Soon");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label messageLabel = new Label("This feature is currently under development.");
        messageLabel.setStyle("-fx-font-size: 16px;");
        
        messageBox.getChildren().addAll(titleLabel, messageLabel);
        mainPane.setCenter(messageBox);
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            // Close current window
            Stage currentStage = (Stage) mainPane.getScene().getWindow();
            currentStage.close();
            
            // Load login screen
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error logging out: " + e.getMessage());
        }
    }
}