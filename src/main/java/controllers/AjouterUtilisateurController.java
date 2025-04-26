package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.CrudUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterUtilisateurController {

    @FXML
    private TextField telTF;

    @FXML
    private TextField nomTF;

    @FXML
    private TextField prenomTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField roleTF;

    @FXML
    void AjouterUtilisateur(ActionEvent event) {
        CrudUtilisateur CrudUtilisateur = new CrudUtilisateur();
        Utilisateur utilisateur = new Utilisateur(Integer.parseInt(telTF.getText()),nomTF.getText(),prenomTF.getText(),emailTF.getText(),roleTF.getText());
        try {
            CrudUtilisateur.ajouter(utilisateur);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Utilisateur a ete ajouté avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UtilisateurInfo.fxml"));
            try {
                Parent parent = loader.load();
                UtilisateurInfo utilInfo = loader.getController();
                utilInfo.setTel(telTF.getText());
                utilInfo.setNom(nomTF.getText());
                utilInfo.setPrenom(prenomTF.getText());
                utilInfo.setEmail(emailTF.getText());
                utilInfo.setRole(roleTF.getText());
                nomTF.getScene().setRoot(parent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information");
            alert.setContentText(e.getMessage());
            alert.show();
        }

    }

    @FXML
    public void AfficherUtilisateur(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherUtilisateur.fxml"));
            nomTF.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
