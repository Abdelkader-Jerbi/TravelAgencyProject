package Controller;

import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.CrudVoiture;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;



public class VoitureController {

    @FXML
    private TableView<Voiture> voitureTable;

    @FXML
    private TableColumn<Voiture, String> marqueCol;

    @FXML
    private TableColumn<Voiture, String> modelCol;

    @FXML
    private TableColumn<Voiture, Double> prixCol;

    @FXML
    private TableColumn<Voiture, String> matriculeCol;

    @FXML
    private TableColumn<Voiture, String> disponible;

    @FXML
    private TableColumn<Voiture, String> imagePath;

    @FXML
    private TableColumn<Voiture, Void> actionCol;

    @FXML
    private Button addOrUpdateButton;

    private final CrudVoiture crud = new CrudVoiture();

    @FXML
    public void initialize() {
        initColumns();
        setupImageColumn();
     /////   addActionButtonsToTable();
        refreshTable();

        addOrUpdateButton.setOnAction(event -> openAjouterWindow());
    }

    private void initColumns() {
        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        disponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
    }

    private void setupImageColumn() {
        imagePath.setCellFactory(column -> {
            return new TableCell<Voiture, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);

                    if (empty || imagePath == null || imagePath.isEmpty()) {
                        setGraphic(null);
                    } else {
                        try {
                            // Créer une Image à partir d'une URL si le chemin est une URL valide
                            Image image = new Image(imagePath); // Utiliser directement l'URL
                            imageView.setImage(image);
                            imageView.setFitWidth(100);  // Définir la largeur de l'image
                            imageView.setFitHeight(75);  // Définir la hauteur de l'image
                            imageView.setPreserveRatio(true);  // Maintenir les proportions de l'image
                            setGraphic(imageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setGraphic(null); // Si l'URL n'est pas valide, on ne met pas d'image
                        }
                    }
                }
            };
        });
    }




    /*private void addActionButtonsToTable() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(10, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Voiture voiture = getTableView().getItems().get(getIndex());
                    openModifierWindow(voiture);
                });

                deleteButton.setOnAction(event -> {
                    Voiture voiture = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText("Supprimer cette voiture ?");
                    confirm.setContentText("Action irréversible.");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                // Obtenez la connexion depuis votre classe de gestion de base de données (par exemple, MyDatabase.getConnection())
                                Connection conn = MyDatabase.getConnection();

                                // Passer la connexion et l'ID de la voiture
                                crud.deleteVoiture(conn, voiture.getId());

                                // Rafraîchir la table après la suppression
                                refreshTable();
                            } catch (SQLException e) {
                                afficherErreur("Erreur suppression : " + e.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

   private void openModifierWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVoiture.fxml"));
            Parent root = loader.load();

            ModifierVoitureController controller = loader.getController();
            controller.setVoiture(voiture);

            Stage stage = new Stage();
            stage.setTitle("Modifier Voiture");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> refreshTable());
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture modification : " + e.getMessage());
        }
    }
*/
    private void openAjouterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Voiture");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> refreshTable());
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture ajout : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            // Récupérer la connexion depuis la classe MyDatabase
            Connection conn = MyDatabase.getConnection();

            // Passer la connexion à la méthode getAllVoitures
            ObservableList<Voiture> observableList = FXCollections.observableArrayList(crud.getAllVoitures(conn));
            voitureTable.setItems(observableList);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la récupération des voitures : " + e.getMessage());
        }
    }


    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
