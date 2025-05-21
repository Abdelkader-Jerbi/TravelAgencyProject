package controllers;

import entities.Role;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UtilisateurInfoUser {

    @FXML
    private Label nomFieldLabel;

    @FXML
    private Label prenomFieldLabel;

    @FXML
    private Label emailFieldLabel;

    @FXML
    private Label telephoneFieldLabel;

    @FXML
    private Label roleFieldLabel;

    private int userId;


    public void setNom(String nom) {
        this.nomFieldLabel.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.prenomFieldLabel.setText(prenom);
    }

    public void setTel(String telephone) {
        this.telephoneFieldLabel.setText(telephone);
    }

    public void setEmail(String email) {
        this.emailFieldLabel.setText(email);
    }


    public void setRole(String role) {
        this.roleFieldLabel.setText(role);
    }

    public void setUtilisateur(Utilisateur u) {
        this.userId = u.getId();
        telephoneFieldLabel.setText(String.valueOf(u.getTel()));
        nomFieldLabel.setText(u.getNom());
        prenomFieldLabel.setText(u.getPrenom());
        emailFieldLabel.setText(u.getEmail());
    }

    public void retourAfficherUtilisateurss(ActionEvent Event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void navigateToModifierUtilisateur(ActionEvent event) {
        try {
            // Get the current user information
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(this.userId);
            utilisateur.setNom(nomFieldLabel.getText());
            utilisateur.setPrenom(prenomFieldLabel.getText());
            utilisateur.setEmail(emailFieldLabel.getText());
            utilisateur.setTel(Integer.parseInt(telephoneFieldLabel.getText()));

            // Load the ModifierUtilisateur.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUtilisateur.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the user data
            ModifierUtilisateur modifierUtilisateurController = loader.getController();
            modifierUtilisateurController.setUtilisateur(utilisateur);

            // Set the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error navigating to ModifierUtilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void navigateToChangePassword(ActionEvent event) {
        try {
            // Create a new stage for the password change dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChangePassword.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the user email
            ChangePasswordController changePasswordController = loader.getController();
            changePasswordController.setUserEmail(emailFieldLabel.getText());
            
            // Set the scene
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.out.println("Error navigating to Change Password: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
