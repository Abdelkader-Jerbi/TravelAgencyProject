package controllers.Reclamation;

import entities.Categorie;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.CrudReclamation;
import services.categorie.categorieRec;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HistoriqueReclamations implements Initializable {

    @FXML
    private TableView<Reclamation> tableReclamation;

    @FXML
    private TableColumn<Reclamation, String> categorieColumn;

    @FXML
    private TableColumn<Reclamation, String> dateColumn;

    @FXML
    private TableColumn<Reclamation, String> commentaireColumn;

    @FXML
    private TableColumn<Reclamation, String> statutColumn;

    @FXML
    private TableColumn<Reclamation, String> reponseColumn;

    @FXML
    private Button fermerButton;

    private final CrudReclamation reclamationService = new CrudReclamation();
    private final categorieRec categorieService = new categorieRec();
    private Map<Integer, String> categoriesMap = new HashMap<>();
    private int userId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes
        categorieColumn.setCellValueFactory(cellData -> {
            int idCategorie = cellData.getValue().getIdCategorie();
            return new SimpleStringProperty(categoriesMap.getOrDefault(idCategorie, "Inconnue"));
        });
        
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate()));
        
        commentaireColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCommentaire()));
        
        statutColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut()));
            
        reponseColumn.setCellValueFactory(cellData -> {
            String reponse = cellData.getValue().getReponse();
            String statut = cellData.getValue().getStatut();
            if ("Résolu".equals(statut) && reponse != null && !reponse.isEmpty()) {
                return new SimpleStringProperty(reponse);
            }
            return new SimpleStringProperty("Pas encore répondu");
        });

        // Configurer le style des colonnes
        configureColumnStyle(categorieColumn, "Catégorie");
        configureColumnStyle(dateColumn, "Date");
        configureColumnStyle(commentaireColumn, "Commentaire");
        configureColumnStyle(statutColumn, "Statut");
        configureColumnStyle(reponseColumn, "Réponse");

        // Charger les catégories
        loadCategories();
        // Charger les réclamations
        loadReclamations();

        // Configurer le bouton fermer
        fermerButton.setOnAction(e -> handleRetour());
    }

    private void configureColumnStyle(TableColumn<?, ?> column, String title) {
        column.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI';");
        column.setResizable(true);
        column.setSortable(true);
        column.setText(title);
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.afficher();
            for (Categorie categorie : categories) {
                categoriesMap.put(categorie.getIdCategorie(), categorie.getDescription());
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    private void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationService.getReclamationsByUserId(userId);
            ObservableList<Reclamation> observableList = FXCollections.observableArrayList(reclamations);
            tableReclamation.setItems(observableList);

            // Configurer les colonnes
            categorieColumn.setCellValueFactory(cellData -> {
                int idCategorie = cellData.getValue().getIdCategorie();
                return new SimpleStringProperty(categoriesMap.getOrDefault(idCategorie, "Inconnue"));
            });
            
            dateColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDate()));
            
            commentaireColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getCommentaire()));
            
            statutColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStatut()));
                
            reponseColumn.setCellValueFactory(cellData -> {
                String reponse = cellData.getValue().getReponse();
                String statut = cellData.getValue().getStatut();
                if ("Résolu".equals(statut) && reponse != null && !reponse.isEmpty()) {
                    return new SimpleStringProperty(reponse);
                }
                return new SimpleStringProperty("Pas encore répondu");
            });

            // Charger le style CSS
            String cssPath = getClass().getResource("/styles/table.css").toExternalForm();
            tableReclamation.getStylesheets().add(cssPath);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadReclamations(); // Recharger les réclamations avec le nouvel ID
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) fermerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la page d'accueil: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 