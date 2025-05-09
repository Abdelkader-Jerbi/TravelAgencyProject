package controllers.Categorie;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.categorie.CrudCategorie;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterCategorie implements Initializable {

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<String> categorieComboBox;

    @FXML
    private Label messageLabel;

    private final CrudCategorie categorieService = new CrudCategorie();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.afficher();
            ObservableList<String> descriptions = FXCollections.observableArrayList();
            for (Categorie categorie : categories) {
                descriptions.add(categorie.getDescription());
            }
            categorieComboBox.setItems(descriptions);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        String description = descriptionField.getText().trim();
        
        if (description.isEmpty()) {
            messageLabel.setText("Veuillez entrer une description");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            Categorie categorie = new Categorie();
            categorie.setDescription(description);
            categorieService.ajouter(categorie);
            
            // Afficher le message de succès
            messageLabel.setText("Catégorie ajoutée avec succès");
            messageLabel.setStyle("-fx-text-fill: green;");
            
            // Vider le champ de description
            descriptionField.clear();
            
            // Recharger la liste des catégories
            loadCategories();
            
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de l'ajout de la catégorie");
            messageLabel.setStyle("-fx-text-fill: red;");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la catégorie: " + e.getMessage());
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


