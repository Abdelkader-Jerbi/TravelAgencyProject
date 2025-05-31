package controllers;


import entities.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.AbstractDocument;
import java.io.IOException;

import static entities.Session.getLoggedInUser;


public class HompePageController {


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
            Parent root = FXMLLoader.load(getClass().getResource("/Admin/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
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