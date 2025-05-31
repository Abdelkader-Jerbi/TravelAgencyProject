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
    private Button categoriesButton;
    
    @FXML
    private VBox reclamationsSubMenu;
    
    @FXML
    private VBox categoriesSubMenu;
    
    @FXML
    private Button reservationsButton;

    @FXML
    private Label reclamationsChevron;
    
    @FXML
    private Label categoriesChevron;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize dashboard - could load a default view
        System.out.println("Travel Agency Dashboard initialized");
        
        // Add click handlers for menu buttons
        reclamationsButton.setOnAction(e -> toggleSubMenu(reclamationsSubMenu, reclamationsChevron));
        categoriesButton.setOnAction(e -> toggleSubMenu(categoriesSubMenu, categoriesChevron));
    }
    
    private void toggleSubMenu(VBox subMenu, Label chevron) {
        boolean isVisible = !subMenu.isVisible();
        subMenu.setVisible(isVisible);
        subMenu.setManaged(isVisible);
        chevron.setText(isVisible ? "▲" : "▼");
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
        toggleSubMenu(reclamationsSubMenu, reclamationsChevron);
    }

    @FXML
    public void navigateToCategories(ActionEvent event) {
        toggleSubMenu(categoriesSubMenu, categoriesChevron);
    }

    @FXML
    public void navigateToAfficherReclamations(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Reclamation/AfficherReclamation.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Afficher Réclamations");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Afficher Réclamations interface: " + e.getMessage());
            showNotImplementedMessage("Afficher Réclamations");
        }
    }

    @FXML
    public void navigateToModifierReclamations(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Reclamation/ModifierReclamation.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Modifier Réclamations");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Modifier Réclamations interface: " + e.getMessage());
            showNotImplementedMessage("Modifier Réclamations");
        }
    }

    @FXML
    public void navigateToAfficherCategories(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Categorie/AfficherCategorie.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Afficher Catégories");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Afficher Catégories interface: " + e.getMessage());
            showNotImplementedMessage("Afficher Catégories");
        }
    }

    @FXML
    public void navigateToAjouterCategorie(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Categorie/AjouterCategorie.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Ajouter Catégorie");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Ajouter Catégorie interface: " + e.getMessage());
            showNotImplementedMessage("Ajouter Catégorie");
        }
    }

    @FXML
    public void navigateToModifierCategorie(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Categorie/ModifierCategorie.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Modifier Catégorie");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Modifier Catégorie interface: " + e.getMessage());
            showNotImplementedMessage("Modifier Catégorie");
        }
    }

    @FXML
    public void navigateToSupprimerCategorie(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Categorie/SupprimerCategorie.fxml");
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                mainPane.setCenter(root);
            } else {
                showNotImplementedMessage("Supprimer Catégorie");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Supprimer Catégorie interface: " + e.getMessage());
            showNotImplementedMessage("Supprimer Catégorie");
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