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

public class ModifierReclamation implements Initializable {

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

    @FXML
    private ComboBox<String> statutComboBox;

    @FXML
    private Button modifierButton;

    @FXML
    private Button fermerButton;

    private final CrudReclamation reclamationService = new CrudReclamation();
    private final categorieRec categorieService = new categorieRec();
    private Map<Integer, String> categoriesMap = new HashMap<>();
    private Reclamation selectedReclamation;
    private Integer toSelectId = null;
    private Runnable onReclamationModified;

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

        // Configurer le ComboBox des statuts
        statutComboBox.getItems().addAll("À traiter", "En attente", "En cours", "Résolu");

        // Ajouter un listener pour la sélection d'une ligne
        tableReclamation.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                statutComboBox.setValue(newSelection.getStatut());
            }
        });

        // Charger les catégories
        loadCategories();
        // Charger les réclamations
        loadReclamations();

        // Configurer les boutons
        modifierButton.setOnAction(e -> handleModifier());
        fermerButton.setOnAction(e -> handleFermer());
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

            // Charger le style CSS
            String cssPath = getClass().getResource("/styles/table.css").toExternalForm();
            tableReclamation.getStylesheets().add(cssPath);

            // Sélectionner la réclamation si demandé
            if (toSelectId != null) {
                for (Reclamation r : observableList) {
                    if (r.getIdReclamation() == toSelectId) {
                        tableReclamation.getSelectionModel().select(r);
                        tableReclamation.scrollTo(r);
                        break;
                    }
                }
                toSelectId = null;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations: " + e.getMessage());
        }
    }

    public void selectReclamationById(int id) {
        this.toSelectId = id;
        loadReclamations();
    }

    public void setOnReclamationModified(Runnable callback) {
        this.onReclamationModified = callback;
    }

    private void notifyReclamationModified() {
        if (onReclamationModified != null) {
            onReclamationModified.run();
        }
    }

    @FXML
    private void handleModifier() {
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une réclamation");
            return;
        }

        String nouveauStatut = statutComboBox.getValue();
        if (nouveauStatut == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un statut");
            return;
        }

        try {
            selectedReclamation.setStatut(nouveauStatut);
            reclamationService.modifier(selectedReclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès");
            notifyReclamationModified();
            handleFermer();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la réclamation: " + e.getMessage());
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
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