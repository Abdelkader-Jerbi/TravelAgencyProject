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
import javafx.stage.Stage;

import java.io.IOException;

public class UtilisateurInfo {

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
        telephoneFieldLabel.setText(String.valueOf(u.getTel()));
        nomFieldLabel.setText(u.getNom());
        prenomFieldLabel.setText(u.getPrenom());
        emailFieldLabel.setText(u.getEmail());
    }

    public void retourAfficherUtilisateurss(ActionEvent Event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherUtilisateur.fxml"));
            Stage stage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
