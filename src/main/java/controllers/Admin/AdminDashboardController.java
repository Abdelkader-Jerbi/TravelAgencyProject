package controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
// for push
public class AdminDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnAfficherReclamations;

    @FXML
    private Button btnModifierReclamations;

    @FXML
    private Button btnAjouterCategorie;

    @FXML
    private Button btnModifierCategorie;

    @FXML
    private Button btnSupprimerCategorie;

    @FXML
    private Button btnAfficherCategories;

    @FXML
    public void handleAfficherReclamations() {
        loadFXML("/Reclamation/AfficherReclamation.fxml");
    }

    @FXML
    public void handleModifierReclamations() {
        loadFXML("/Reclamation/ModifierReclamation.fxml");
    }

    @FXML
    public void handleAjouterCategorie() {
        loadFXML("/Categorie/AjouterCategorie.fxml");
    }

    @FXML
    public void handleModifierCategorie() {
        loadFXML("/Categorie/ModifierCategorie.fxml");
    }

    @FXML
    public void handleSupprimerCategorie() {
        loadFXML("/Categorie/SupprimerCategorie.fxml");
    }

    @FXML
    public void handleAfficherCategories() {
        loadFXML("/Categorie/AfficherCategorie.fxml");
    }

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Vous pourriez ajouter ici une gestion d'erreur plus élaborée
        }
    }
} 