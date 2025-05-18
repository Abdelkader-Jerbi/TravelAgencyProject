package controllers;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.CrudUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class AfficherUtilisateurController {


    @javafx.fxml.FXML
    private TableColumn coltel;
    @javafx.fxml.FXML
    private TableView tvpersonne;
    @javafx.fxml.FXML
    private TableColumn colnom;
    @javafx.fxml.FXML
    private TableColumn colprenom;
    @javafx.fxml.FXML
    private TableColumn colemail;
    @javafx.fxml.FXML
    private TableColumn colrole;

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


    @javafx.fxml.FXML
    void initialize() {

        CrudUtilisateur CrudUtilisateur =new CrudUtilisateur();
        try {
            ObservableList<Utilisateur> observableList= FXCollections.observableArrayList(CrudUtilisateur.afficher());
            tvpersonne.setItems(observableList);
            colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            coltel.setCellValueFactory(new PropertyValueFactory<>("tel"));
            colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colrole.setCellValueFactory(new PropertyValueFactory<>("role"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    public void supprimerUtilisateur(ActionEvent actionEvent) {
        int utilisateurSelectionne = tvpersonne.getSelectionModel().getSelectedIndex();
        tvpersonne.getItems().remove(utilisateurSelectionne);
    }

    public void RetourAjoutUtilisateur(ActionEvent Event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterUtilisateur.fxml"));
            Stage stage = (Stage)((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
