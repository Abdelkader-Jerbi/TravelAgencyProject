package controllers.Categorie;

import entities.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableColumn<Categorie, Integer> idColumn;

    @FXML
    private TableColumn<Categorie, String> descriptionColumn;

    private final categorieRec categorieService = new categorieRec();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idCategorie"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Configurer le style des colonnes
        configureColumnStyle(idColumn, "ID", true);
        configureColumnStyle(descriptionColumn, "Description", false);

        // Charger les catégories
        loadCategories();
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
}


