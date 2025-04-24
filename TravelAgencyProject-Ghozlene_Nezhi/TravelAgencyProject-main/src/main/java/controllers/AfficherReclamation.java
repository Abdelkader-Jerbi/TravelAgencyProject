package controllers;

import entities.Reclamation;
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
import services.CrudReclamation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class AfficherReclamation {

    @FXML
    private TableView<Reclamation> tableReclamation;

    @FXML
    private TableColumn<Reclamation, Integer> colid;

    @FXML
    private TableColumn<Reclamation, Integer> colidUser;

    @FXML
    private TableColumn<Reclamation, String> colcateg;

    @FXML
    private TableColumn<Reclamation, String> colDate;

    @FXML
    private TableColumn<Reclamation, String> colComment;

    // Cette méthode est appelée automatiquement après le chargement de la vue FXML
    @FXML
    void initialize() {
        CrudReclamation crudReclamation = new CrudReclamation();
        try {
            // On récupère la liste des réclamations depuis la base
            ObservableList<Reclamation> observableList = FXCollections.observableArrayList(crudReclamation.afficher());

            // On associe cette liste au TableView
            tableReclamation.setItems(observableList);

            // On lie les colonnes aux attributs de l'entité Reclamation
            colid.setCellValueFactory(new PropertyValueFactory<>("idAvis")); // ou "idReclamation"
            colidUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            colcateg.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colComment.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des réclamations : " + e.getMessage());
        }
    }

    // Supprime la réclamation sélectionnée dans le tableau (visuellement uniquement ici)
    @FXML
    public void supprimerUtilisateur(ActionEvent actionEvent) {
        int selectedIndex = tableReclamation.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            tableReclamation.getItems().remove(selectedIndex);
        }
    }

    // Redirige vers l'écran "AjouterReclamation.fxml"
    public void RetourAjoutReclamation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AjouteReclamation.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à l'ajout utilisateur : " + e.getMessage());
        }
    }
}
