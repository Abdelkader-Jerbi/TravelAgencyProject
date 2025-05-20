package controllers.Categorie;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.categorie.categorieRec;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierCategorie implements Initializable {

    @FXML private TableView<Categorie> categorieTable;
    @FXML private TableColumn<Categorie, String> descriptionColumn;
    @FXML private TextField descriptionField;
    @FXML private Button sauvegarderButton;
    @FXML private Button annulerButton;

    private final categorieRec service = new categorieRec();
    private ObservableList<Categorie> categorieList;
    private Categorie categorieSelectionnee;
    private Runnable onCategorieModified;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Chargement des données
        loadCategories();

        // Configuration de la sélection dans la table
        categorieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categorieSelectionnee = newSelection;
                descriptionField.setText(newSelection.getDescription());
            }
        });

        // Configuration des boutons
        sauvegarderButton.setOnAction(e -> handleSauvegarder());
        annulerButton.setOnAction(e -> handleAnnuler());
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

    public void setOnCategorieModified(Runnable callback) {
        this.onCategorieModified = callback;
    }

    public void prefillCategorie(Categorie categorie) {
        this.categorieSelectionnee = categorie;
        descriptionField.setText(categorie.getDescription());
        categorieTable.getSelectionModel().clearSelection();
    }

    private void notifyCategorieModified() {
        if (onCategorieModified != null) {
            onCategorieModified.run();
        }
    }

    private void handleSauvegarder() {
        if (categorieSelectionnee == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie à modifier");
            return;
        }

        String nouvelleDescription = descriptionField.getText().trim();
        if (nouvelleDescription.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "La description ne peut pas être vide");
            return;
        }

        try {
            // Mise à jour de l'objet
            categorieSelectionnee.setDescription(nouvelleDescription);
            
            // Sauvegarde dans la base de données
            service.modifier(categorieSelectionnee);
            
            // Rafraîchir la liste
            loadCategories();
            
            // Notifier le parent
            notifyCategorieModified();
            
            // Réinitialiser le formulaire
            descriptionField.clear();
            categorieSelectionnee = null;
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie modifiée avec succès");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la catégorie: " + e.getMessage());
        }
    }

    private void handleAnnuler() {
        descriptionField.clear();
        categorieSelectionnee = null;
        categorieTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
