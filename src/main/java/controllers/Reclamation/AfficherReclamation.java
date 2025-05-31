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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.util.ArrayList;

public class AfficherReclamation implements Initializable {

    @FXML
    private TableView<Reclamation> tableReclamation;

    @FXML
    private TableColumn<Reclamation, String> emailColumn;

    @FXML
    private TableColumn<Reclamation, String> categorieColumn;

    @FXML
    private TableColumn<Reclamation, String> dateColumn;

    @FXML
    private TableColumn<Reclamation, String> commentaireColumn;

    @FXML
    private TableColumn<Reclamation, String> statutColumn;

    @FXML
    private TableColumn<Reclamation, String> actionColumn;

    @FXML
    private DatePicker dateFilterPicker;

    @FXML
    private ComboBox<String> categorieFilterComboBox;

    @FXML
    private TextField emailFilterField;

    @FXML
    private ComboBox<String> statutFilterComboBox;

    @FXML
    private Button translateButton;

    private final CrudReclamation reclamationService = new CrudReclamation();
    private final categorieRec categorieService = new categorieRec();
    private Map<Integer, String> categoriesMap = new HashMap<>();
    private List<Reclamation> allReclamations = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        categorieColumn.setCellValueFactory(cellData -> {
            int idCategorie = cellData.getValue().getIdCategorie();
            return new SimpleStringProperty(categoriesMap.getOrDefault(idCategorie, "Inconnue"));
        });
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configurer le style des colonnes
        configureColumnStyle(emailColumn, "Email Utilisateur");
        configureColumnStyle(categorieColumn, "Catégorie");
        configureColumnStyle(dateColumn, "Date");
        configureColumnStyle(commentaireColumn, "Commentaire");
        configureColumnStyle(statutColumn, "Statut");
        configureColumnStyle(actionColumn, "Action");

        // Charger les catégories
        loadCategories();
        
        // Ajouter l'option "Toutes les catégories" au début de la liste
        categorieFilterComboBox.getItems().add(0, "Toutes les catégories");
        categorieFilterComboBox.setValue("Toutes les catégories");
        
        // Configurer les filtres
        setupFilters();
        
        // Configurer le filtre par email en temps réel
        emailFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        loadReclamations();
        configureActionColumn();
        setupStatutFilter();
        translateButton.setOnAction(e -> handleTranslate());
    }

    private void configureColumnStyle(TableColumn<?, ?> column, String title) {
        column.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI';");
        column.setResizable(true);
        column.setSortable(true);
        column.setText(title);
    }

    private void loadCategories() {
        try {
            List<entities.Categorie> categories = categorieService.afficher();
            for (entities.Categorie categorie : categories) {
                categoriesMap.put(categorie.getIdCategorie(), categorie.getDescription());
                categorieFilterComboBox.getItems().add(categorie.getDescription());
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    private void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationService.afficher();
            allReclamations = new ArrayList<>(reclamations);
            tableReclamation.setItems(FXCollections.observableArrayList(allReclamations));
            // Remplir le filtre email
            ObservableList<String> emails = FXCollections.observableArrayList();
            for (Reclamation r : allReclamations) {
                if (!emails.contains(r.getEmail())) {
                    emails.add(r.getEmail());
                }
            }
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

    private void setupFilters() {
        // Filtre par catégorie
        categorieFilterComboBox.setOnAction(e -> applyFilters());
        
        // Filtre par date
        dateFilterPicker.setOnAction(e -> applyFilters());
        
        // Filtre par statut
        statutFilterComboBox.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        ObservableList<Reclamation> filtered = FXCollections.observableArrayList(allReclamations);
        String emailText = emailFilterField.getText().toLowerCase();
        LocalDate selectedDate = dateFilterPicker.getValue();
        String selectedCategory = categorieFilterComboBox.getValue();
        String selectedStatut = statutFilterComboBox.getValue();

        // Filtre par email (en temps réel)
        if (!emailText.isEmpty()) {
            filtered.removeIf(r -> r.getEmail() == null || !r.getEmail().toLowerCase().contains(emailText));
        }

        // Filtre par date
        if (selectedDate != null) {
            filtered.removeIf(r -> !selectedDate.toString().equals(r.getDate()));
        }

        // Filtre par catégorie
        if (selectedCategory != null && !selectedCategory.equals("Toutes les catégories")) {
            final int selectedCategoryId = categoriesMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(selectedCategory))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
                
            if (selectedCategoryId != -1) {
                filtered.removeIf(r -> r.getIdCategorie() != selectedCategoryId);
            }
        }

        // Filtre par statut
        if (selectedStatut != null && !selectedStatut.equals("Toutes les réclamations")) {
            filtered.removeIf(r -> !selectedStatut.equalsIgnoreCase(r.getStatut()));
        }

        tableReclamation.setItems(filtered);
    }

    private void configureActionColumn() {
        actionColumn.setCellFactory(new Callback<TableColumn<Reclamation, String>, TableCell<Reclamation, String>>() {
            @Override
            public TableCell<Reclamation, String> call(final TableColumn<Reclamation, String> param) {
                return new TableCell<Reclamation, String>() {
                    private final Button btn = new Button("Modifier");
                    {
                        btn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 2 8 2 8; -fx-min-width: 60px; -fx-max-width: 70px; -fx-background-radius: 5;");
                        btn.setOnAction((ActionEvent event) -> {
                            Reclamation data = getTableView().getItems().get(getIndex());
                            handleModifier(data);
                        });
                    }
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    private void handleModifier(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/ModifierReclamation.fxml"));
            Parent root = loader.load();
            ModifierReclamation controller = loader.getController();
            controller.selectReclamationById(reclamation.getIdReclamation());
            
            // Ajouter un listener pour rafraîchir la liste après modification
            controller.setOnReclamationModified(() -> {
                loadReclamations();
                applyFilters();
            });
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        }
    }

    private void setupStatutFilter() {
        statutFilterComboBox.getItems().addAll("Toutes les réclamations", "en attente", "en cours", "résolu", "à traiter");
        statutFilterComboBox.getSelectionModel().selectFirst();
    }

    private void handleTranslate() {
        String targetLang = askLanguage(); // Demander la langue à l'utilisateur (fr, en, es)
        if (targetLang == null) return;
        ObservableList<Reclamation> reclamations = tableReclamation.getItems();
        for (Reclamation rec : reclamations) {
            String translated = translateText(rec.getCommentaire(), targetLang);
            rec.setCommentaire(translated);
        }
        tableReclamation.refresh();
    }

    private String askLanguage() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("fr", "fr", "en", "es");
        dialog.setTitle("Choisir la langue");
        dialog.setHeaderText("Traduire les commentaires vers :");
        dialog.setContentText("Langue :");
        return dialog.showAndWait().orElse(null);
    }

    private String translateText(String text, String targetLang) {
        try {
            // URL de l'API LibreTranslate (vous pouvez utiliser un serveur public ou héberger le vôtre)
            String urlStr = "https://libretranslate.de/translate";
            
            // Préparer les données pour la requête
            String postData = String.format(
                "{\"q\":\"%s\",\"source\":\"auto\",\"target\":\"%s\"}",
                text.replace("\"", "\\\""),
                targetLang
            );
            
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Envoyer les données
            try (java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse la réponse JSON

            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de traduction", 
                "Impossible de traduire le texte: " + e.getMessage());
        }
        return text; // Retourner le texte original en cas d'erreur
    }
}
