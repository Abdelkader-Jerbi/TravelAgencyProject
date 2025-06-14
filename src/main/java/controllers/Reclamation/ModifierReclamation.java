package controllers.Reclamation;

import entities.Categorie;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CrudReclamation;
import services.categorie.categorieRec;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import utils.EmailRec;

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
    private ComboBox<String> statutComboBox;

    @FXML
    private TextArea reponseTextArea;

    @FXML
    private VBox reponseContainer;

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
        // Configurer les colonnes avec des cellules personnalisées
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
            return new SimpleStringProperty(reponse != null && !reponse.isEmpty() ? reponse : "Pas encore répondu");
        });

        // Configurer le style des colonnes
        configureColumnStyle(categorieColumn, "Catégorie");
        configureColumnStyle(dateColumn, "Date");
        configureColumnStyle(commentaireColumn, "Commentaire");
        configureColumnStyle(statutColumn, "Statut");
        configureColumnStyle(reponseColumn, "Réponse");

        // Configurer le ComboBox des statuts
        statutComboBox.getItems().addAll("À traiter", "En attente", "En cours", "Résolu");
        
        // Ajouter un listener pour le changement de statut
        statutComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            reponseContainer.setVisible("Résolu".equals(newVal));
            reponseContainer.setManaged("Résolu".equals(newVal));
            if (!"Résolu".equals(newVal)) {
                reponseTextArea.clear();
            }
        });

        // Ajouter un listener pour la sélection d'une ligne
        tableReclamation.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReclamation = newSelection;
                statutComboBox.setValue(newSelection.getStatut());
                if ("Résolu".equals(newSelection.getStatut())) {
                    reponseTextArea.setText(newSelection.getReponse());
                }
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
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une réclamation à modifier.");
            return;
        }

        String nouveauStatut = statutComboBox.getValue();
        String reponse = reponseTextArea.getText();

        // Vérifier si une réponse est fournie pour le statut "Résolu"
        if ("Résolu".equals(nouveauStatut) && (reponse == null || reponse.trim().isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez fournir une réponse pour marquer la réclamation comme résolue.");
            return;
        }

        try {
            // Mettre à jour la réclamation
            selectedReclamation.setStatut(nouveauStatut);
            selectedReclamation.setReponse(reponse);
            reclamationService.modifier(selectedReclamation);

            // Si le statut est "Résolu", envoyer un email
            if ("Résolu".equals(nouveauStatut)) {
                String emailContent = String.format(
                    "Cher(e) client(e),\n\n" +
                    "Nous avons le plaisir de vous informer que votre réclamation a été traitée et résolue.\n\n" +
                    "Détails de votre réclamation :\n" +
                    "----------------------------------------\n" +
                    "Catégorie : %s\n" +
                    "Date de la réclamation : %s\n" +
                    "Votre message : %s\n\n" +
                    "Notre réponse :\n" +
                    "----------------------------------------\n" +
                    "%s\n\n" +
                    "Nous vous remercions de votre confiance et restons à votre disposition pour toute question supplémentaire.\n\n" +
                    "Cordialement,\n" +
                    "L'équipe de l'Agence de Voyage",
                    getCategorieDescription(selectedReclamation.getIdCategorie()),
                    selectedReclamation.getDate(),
                    selectedReclamation.getCommentaire(),
                    selectedReclamation.getReponse()
                );

                // Utiliser l'email stocké dans la réclamation
                String userEmail = selectedReclamation.getEmail();
                if (userEmail != null && !userEmail.isEmpty()) {
                    System.out.println("Envoi de l'email à: " + userEmail);
                    EmailRec.sendEmail(
                        userEmail,
                        "Votre réclamation a été résolue - Agence de Voyage",
                        emailContent
                    );
                    System.out.println("Email envoyé avec succès");
                } else {
                    System.err.println("Impossible de trouver l'email de l'utilisateur");
                }
            }

            // Rafraîchir la table
            loadReclamations();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
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

    private String getCategorieDescription(int idCategorie) {
        return categoriesMap.getOrDefault(idCategorie, "Inconnue");
    }

    private void clearFields() {
        selectedReclamation = null;
        statutComboBox.setValue(null);
        reponseTextArea.clear();
    }
}