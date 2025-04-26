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
import javafx.scene.control.Alert;
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

    public void afficherUtilisateur(ActionEvent actionEvent) {
        Utilisateur utilisateurSelectionne = (Utilisateur) tvpersonne.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UtilisateurInfo.fxml"));
                Parent root = loader.load();

                // Get the controller of the new scene
                UtilisateurInfo controller = loader.getController();

                // Send the selected user to the controller
                controller.setUtilisateur(utilisateurSelectionne);

                // Switch scenes
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Optional: Alert if nothing is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to view.");
            alert.showAndWait();
        }
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

    public void ModifierCompte(ActionEvent Event) {
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
