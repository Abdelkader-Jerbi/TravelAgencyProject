package controllers.Categorie;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.categorie.categorieRec;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class SupprimerCategorie implements Initializable {

    @FXML private TableView<Categorie> categorieTable;
    @FXML private TableColumn<Categorie, Integer> idColumn;
    @FXML private TableColumn<Categorie, String> descriptionColumn;
    @FXML private Button supprimerButton;
    @FXML private Button annulerButton;

    private final categorieRec service = new categorieRec();
    private ObservableList<Categorie> categorieList;
    private Categorie categorieSelectionnee;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idCategorie"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Chargement des données
        loadCategories();

        // Configuration de la sélection dans la table
        categorieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categorieSelectionnee = newSelection;
                supprimerButton.setDisable(false);
            } else {
                supprimerButton.setDisable(true);
            }
        });

        // Configuration des boutons
        supprimerButton.setOnAction(e -> handleSupprimer());
        annulerButton.setOnAction(e -> handleAnnuler());

        // Désactiver le bouton supprimer initialement
        supprimerButton.setDisable(true);
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = service.afficher();
            categorieList = FXCollections.observableArrayList(categories);
            categorieTable.setItems(categorieList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    private void handleSupprimer() {
        if (categorieSelectionnee == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la catégorie");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie : " + 
                                  categorieSelectionnee.getDescription() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimer(categorieSelectionnee.getIdCategorie());
                    loadCategories(); // Rafraîchir la liste
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie supprimée avec succès");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la catégorie: " + e.getMessage());
                }
            }
        });
    }

    private void handleAnnuler() {
        // Fermer la fenêtre
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
