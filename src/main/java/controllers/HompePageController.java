package controllers;

import entities.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import controllers.Reclamation.AjouterReclamation;
import controllers.Reclamation.HistoriqueReclamations;

import javax.swing.text.AbstractDocument;
import java.io.IOException;

import static entities.Session.getLoggedInUser;

public class HompePageController {

    @FXML
    private Button reclamationsButton;
    
    @FXML
    private VBox reclamationsSubMenu;
    
    @FXML
    private Label reclamationsChevron;

    @FXML
    private void toggleReclamationsMenu() {
        boolean isVisible = !reclamationsSubMenu.isVisible();
        reclamationsSubMenu.setVisible(isVisible);
        reclamationsSubMenu.setManaged(isVisible);
        reclamationsChevron.setText(isVisible ? "▲" : "▼");
    }

    public void allerPageProfile(ActionEvent Event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UtilisateurInfoUser.fxml"));
            Parent root = loader.load();
            UtilisateurInfoUser controller = loader.getController();
            controller.setUtilisateur(getLoggedInUser());
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void allerPageReclamation(ActionEvent Event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/AjouterReclamation.fxml"));
            Parent root = loader.load();
            AjouterReclamation controller = loader.getController();
            controller.setCurrentUserId(getLoggedInUser().getId());
            controller.setCurrentUserEmail(getLoggedInUser().getEmail());
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void allerPageHistoriqueReclamations(ActionEvent Event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/HistoriqueReclamations.fxml"));
            Parent root = loader.load();
            HistoriqueReclamations controller = loader.getController();
            controller.setUserId(getLoggedInUser().getId());
            
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.setTitle("Historique des Réclamations");
            newStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void allerPageVol(ActionEvent Event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ChercherVol.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void allerPageHotel(ActionEvent Event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/AllHotels.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void allerPageVoiture(ActionEvent Event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherVoitureVisiteur.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    

    public void logout(ActionEvent event) {
        try {
            // Clear the session
            Session.clearSession();
            
            // Load the login screen
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            System.out.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}