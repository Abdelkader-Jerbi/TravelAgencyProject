package controllers.Reclamation;

import entities.Categorie;
import entities.Reclamation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.categorie.categorieRec;
import services.CrudReclamation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterReclamation implements Initializable {
//for push
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private TextArea commentaireTextArea;
    @FXML private Button soumettreButton;
    @FXML private Button retourButton;

    private final CrudReclamation reclamationService = new CrudReclamation();
    private final categorieRec categorieService = new categorieRec();
    private int currentUserId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadCategories();
        
        // Configurer les boutons
        retourButton.setOnAction(e -> handleRetour());
        soumettreButton.setOnAction(e -> handleSoumettre());
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.afficher();
            for (Categorie categorie : categories) {
                categorieComboBox.getItems().add(categorie.getDescription());
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    @FXML
    private void handleSoumettre() {
        try {
            String categorie = categorieComboBox.getValue();
            String commentaire = commentaireTextArea.getText();
            
            if (categorie == null || commentaire.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs");
                return;
            }

            // Créer la réclamation
            Reclamation reclamation = new Reclamation();
            reclamation.setId(currentUserId);
            reclamation.setIdCategorie(getCategorieId(categorie));
            reclamation.setCommentaire(commentaire);
            reclamation.setDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            reclamation.setStatut("En attente");

            // Ajouter la réclamation
            reclamationService.ajouter(reclamation);
            
            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès");
            
            // Réinitialiser les champs
            categorieComboBox.setValue(null);
            commentaireTextArea.clear();
            
            // Retourner à la page d'accueil
            handleRetour();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réclamation: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la page d'accueil: " + e.getMessage());
        }
    }

    private int getCategorieId(String description) throws SQLException {
        List<Categorie> categories = categorieService.afficher();
        for (Categorie categorie : categories) {
            if (categorie.getDescription().equals(description)) {
                return categorie.getIdCategorie();
            }
        }
        return -1;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
