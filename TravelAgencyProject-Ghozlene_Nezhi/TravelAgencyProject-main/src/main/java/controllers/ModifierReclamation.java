package controllers;

import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.CrudReclamation;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierReclamation implements Initializable {

    @FXML
    private TableView<Reclamation> tableReclamation;

    @FXML
    private TableColumn<Reclamation, Integer> idAvisColumn;

    @FXML
    private TableColumn<Reclamation, Integer> idUserColumn;

    @FXML
    private TableColumn<Reclamation, String> categorieColumn;

    @FXML
    private TableColumn<Reclamation, String> dateColumn;

    @FXML
    private TableColumn<Reclamation, String> commentaireColumn;

    @FXML
    private TextField idAvisInput;

    @FXML
    private TextField idUserInput;

    @FXML
    private TextField categorieInput;

    @FXML
    private TextField dateInput;

    @FXML
    private TextField commentaireInput;

    private final CrudReclamation crudReclamation = new CrudReclamation();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Reclamation> observableList = FXCollections.observableArrayList(crudReclamation.afficher());

            idAvisColumn.setCellValueFactory(new PropertyValueFactory<>("idAvis"));
            idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

            tableReclamation.setItems(observableList);

            // Listener pour remplissage automatique
            tableReclamation.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    idAvisInput.setText(String.valueOf(newSelection.getIdAvis()));
                    idUserInput.setText(String.valueOf(newSelection.getIdUser()));
                    categorieInput.setText(newSelection.getCategorie());
                    dateInput.setText(newSelection.getDate());
                    commentaireInput.setText(newSelection.getCommentaire());
                    idAvisInput.setDisable(true); // empêche modification ID
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des réclamations : " + e.getMessage());
        }
    }

    @FXML
    void submit(ActionEvent event) {
        try {
            int idAvis = Integer.parseInt(idAvisInput.getText());
            int idUser = Integer.parseInt(idUserInput.getText());
            String categorie = categorieInput.getText();
            String date = dateInput.getText();
            String commentaire = commentaireInput.getText();

            Reclamation reclamation = new Reclamation(idAvis, idUser, categorie, date, commentaire);
            Reclamation selected = tableReclamation.getSelectionModel().getSelectedItem();

            if (selected != null && selected.getIdAvis() == idAvis) {
                crudReclamation.modifier(reclamation);
                int index = tableReclamation.getSelectionModel().getSelectedIndex();
                tableReclamation.getItems().set(index, reclamation);
            } else {
                crudReclamation.ajouter(reclamation);
                tableReclamation.getItems().add(reclamation);
            }

            clearInputs();
            tableReclamation.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            System.out.println("Erreur de saisie : ID Avis et ID User doivent être des nombres.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    void removeReclamation(ActionEvent event) {
        Reclamation selected = tableReclamation.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                crudReclamation.supprimer(selected.getIdAvis());
                tableReclamation.getItems().remove(selected);
                clearInputs();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void clearInputs() {
        idAvisInput.clear();
        idUserInput.clear();
        categorieInput.clear();
        dateInput.clear();
        commentaireInput.clear();
        idAvisInput.setDisable(false);
    }
}
