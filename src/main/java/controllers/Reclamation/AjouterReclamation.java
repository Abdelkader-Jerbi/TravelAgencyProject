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
import javafx.stage.Stage;
import services.categorie.categorieRec;
import services.CrudReclamation;
import utils.MyDatabase;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterReclamation implements Initializable {

    @FXML private ComboBox<Categorie> categorieComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextArea commentaireArea;
    @FXML private TextField statutField;
    @FXML private Button ajouterButton;
    @FXML private Button annulerButton;
    @FXML private Button afficherButton;
    @FXML private Label messageLabel;

    private final categorieRec categorieService = new categorieRec();
    private final CrudReclamation reclamationService = new CrudReclamation();
    private int currentUserId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser la date à aujourd'hui
        datePicker.setValue(LocalDate.now());
        
        // Charger les catégories
        loadCategories();
        
        // Récupérer le premier ID utilisateur disponible
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idUser FROM user LIMIT 1");
            if (rs.next()) {
                currentUserId = rs.getInt("idUser");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur trouvé dans la base de données");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de récupérer l'utilisateur: " + e.getMessage());
        }
        
        // Configurer les boutons
        ajouterButton.setOnAction(e -> handleAjouter());
        annulerButton.setOnAction(e -> handleAnnuler());
        afficherButton.setOnAction(e -> handleAfficher());
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.afficher();
            ObservableList<Categorie> categorieList = FXCollections.observableArrayList(categories);
            categorieComboBox.setItems(categorieList);
            
            // Définir comment afficher les catégories dans la ComboBox
            categorieComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Categorie item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getDescription());
                    }
                }
            });
            
            // Définir comment afficher la catégorie sélectionnée
            categorieComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Categorie item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getDescription());
                    }
                }
            });
            
            // Sélectionner la première catégorie par défaut si disponible
            if (!categorieList.isEmpty()) {
                categorieComboBox.setValue(categorieList.get(0));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    @FXML
    public void handleAjouter() {
        try {
            // Vérifier si un utilisateur a été trouvé
            if (currentUserId == 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur disponible pour créer la réclamation");
                return;
            }
            
            // Validation des champs
            if (categorieComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une catégorie");
                return;
            }
            if (datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une date");
                return;
            }
            if (commentaireArea.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Le commentaire ne peut pas être vide");
                return;
            }

            // Créer la réclamation
            Reclamation reclamation = new Reclamation();
            reclamation.setIdUser(currentUserId);
            reclamation.setIdCategorie(categorieComboBox.getValue().getIdCategorie());
            reclamation.setDate(datePicker.getValue().format(DateTimeFormatter.ISO_DATE));
            reclamation.setCommentaire(commentaireArea.getText().trim());
            reclamation.setStatut("en attente"); // Toujours en attente

            // Ajouter la réclamation
            reclamationService.ajouter(reclamation);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès");
            handleAnnuler(); // Réinitialiser le formulaire

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la réclamation: " + e.getMessage());
            e.printStackTrace(); // Pour le débogage
        }
    }

    @FXML
    public void handleAnnuler() {
        // Réinitialiser le formulaire
        if (!categorieComboBox.getItems().isEmpty()) {
            categorieComboBox.setValue(categorieComboBox.getItems().get(0));
        }
        datePicker.setValue(LocalDate.now());
        commentaireArea.clear();
        messageLabel.setText("");
    }

    @FXML
    public void handleAfficher() {
        try {
            // Charger la vue FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/AfficherReclamation.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Liste des Réclamations");
            stage.setScene(scene);

            // Afficher la fenêtre
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'affichage: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
}
