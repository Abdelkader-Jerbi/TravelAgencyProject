package controllers.Categorie;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.categorie.categorieRec;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherCategorie implements Initializable {

    @FXML
    private TableView<Categorie> categorieTableView;

    @FXML
    private TableColumn<Categorie, String> descriptionColumn;

    @FXML
    private TableColumn<Categorie, String> actionColumn;

    @FXML
    private Button translateButton;

    private final categorieRec categorieService = new categorieRec();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        configureColumnStyle(descriptionColumn, "Description", false);
        configureActionColumn();
        // Charger les catégories
        loadCategories();
        translateButton.setOnAction(e -> handleTranslate());
    }

    private void configureColumnStyle(TableColumn<?, ?> column, String title, boolean center) {
        if (center) {
            column.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI';");
        } else {
            column.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-family: 'Segoe UI'; -fx-padding: 0 0 0 10;");
        }
        column.setResizable(true);
        column.setSortable(true);
        column.setText(title);
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.afficher();
            ObservableList<Categorie> observableList = FXCollections.observableArrayList(categories);
            categorieTableView.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories: " + e.getMessage());
        }
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) categorieTableView.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void configureActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<Categorie, String>() {
            private final Button btnModifier = new Button("Modifier");
            {
                btnModifier.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 2 8 2 8; -fx-min-width: 60px; -fx-max-width: 70px; -fx-background-radius: 5;");
                btnModifier.setOnAction(event -> {
                    Categorie data = getTableView().getItems().get(getIndex());
                    handleModifier(data);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnModifier);
                }
            }
        });
    }

    private void handleModifier(Categorie categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Categorie/ModifierCategorie.fxml"));
            Parent root = loader.load();
            ModifierCategorie controller = loader.getController();
            controller.prefillCategorie(categorie);
            controller.setOnCategorieModified(() -> loadCategories());
            Stage stage = new Stage();
            stage.setTitle("Modifier Catégorie");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        }
    }

    private void handleTranslate() {
        ObservableList<Categorie> categories = categorieTableView.getItems();
        for (Categorie cat : categories) {
            String translated = translateText(cat.getDescription(), "en"); // ou demander la langue
            cat.setDescription(translated);
        }
        categorieTableView.refresh();
    }

    private String translateText(String text, String targetLang) {
        try {
            String urlStr = String.format(
                "https://api.mymemory.translated.net/get?q=%s&langpair=%s|%s",
                java.net.URLEncoder.encode(text, "UTF-8"),
                "fr", // adapter selon la langue source
                targetLang
            );
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de traduction", "Impossible de traduire le texte: " + e.getMessage());
        }
        return text;
    }

    // Pour la suppression, à placer dans la méthode de suppression :
    private boolean canDeleteCategorie(int idCategorie) {
        try {
            // Vérifier s'il existe des réclamations associées à cette catégorie
            return categorieService.countReclamationsByCategorie(idCategorie) == 0;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de vérifier les réclamations associées: " + e.getMessage());
            return false;
        }
    }
}


