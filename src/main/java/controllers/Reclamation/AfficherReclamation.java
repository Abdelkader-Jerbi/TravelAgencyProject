package controllers.Reclamation;

import entities.Categorie;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.CrudReclamation;
import services.categorie.categorieRec;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AfficherReclamation implements Initializable {

    @FXML
    private TableView<Reclamation> tableReclamation;

    @FXML
    private TableColumn<Reclamation, Integer> idColumn;

    @FXML
    private TableColumn<Reclamation, Integer> idUserColumn;

    @FXML
    private TableColumn<Reclamation, String> categorieColumn;

    @FXML
    private TableColumn<Reclamation, String> dateColumn;

    @FXML
    private TableColumn<Reclamation, String> commentaireColumn;

    @FXML
    private TableColumn<Reclamation, String> statutColumn;

    private final CrudReclamation reclamationService = new CrudReclamation();
    private final categorieRec categorieService = new categorieRec();
    private Map<Integer, String> categoriesMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idReclamation"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        categorieColumn.setCellValueFactory(cellData -> {
            int idCategorie = cellData.getValue().getIdCategorie();
            return new SimpleStringProperty(categoriesMap.getOrDefault(idCategorie, "Inconnue"));
        });
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configurer le style des colonnes
        configureColumnStyle(idColumn, "ID");
        configureColumnStyle(idUserColumn, "ID Utilisateur");
        configureColumnStyle(categorieColumn, "Catégorie");
        configureColumnStyle(dateColumn, "Date");
        configureColumnStyle(commentaireColumn, "Commentaire");
        configureColumnStyle(statutColumn, "Statut");

        // Charger les catégories
        loadCategories();
        
        // Charger les réclamations
        loadReclamations();
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
            List<Reclamation> reclamations = reclamationService.afficher();
            ObservableList<Reclamation> observableList = FXCollections.observableArrayList(reclamations);
            tableReclamation.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) tableReclamation.getScene().getWindow();
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
