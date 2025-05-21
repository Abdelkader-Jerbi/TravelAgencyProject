package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.CrudUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierUtilisateur {

    @FXML
    private TextField nomFieldUpdate;

    @FXML
    private TextField prenomFieldUpdate;

    @FXML
    private TextField emailFieldUpdate;

    @FXML
    private TextField telephoneFieldUpdate;


    private int id;

    public void setUtilisateur(Utilisateur u) {
        this.id = u.getId();
        telephoneFieldUpdate.setText(String.valueOf(u.getTel()));
        nomFieldUpdate.setText(u.getNom());
        prenomFieldUpdate.setText(u.getPrenom());
        emailFieldUpdate.setText(u.getEmail());
    }

    public void modifierUtilisateur(ActionEvent event) throws IOException {
        CrudUtilisateur CrudUtilisateur = new CrudUtilisateur();
        Utilisateur utilisateur = new Utilisateur(this.id,Integer.parseInt(telephoneFieldUpdate.getText()),nomFieldUpdate.getText(),prenomFieldUpdate.getText(),emailFieldUpdate.getText());

        try {
            CrudUtilisateur.modifier(utilisateur);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Utilisateur a ete modifie avec succes");
            alert.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void retourAfficherUtilisateursUpdate(ActionEvent Event) {
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
