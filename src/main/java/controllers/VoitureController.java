/*package controllers;

import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.CrudVoiture;

import java.io.IOException;
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
    private TableColumn<Voiture, Void> actionCol;

    @FXML
    private Button addOrUpdateButton;

    private final CrudVoiture crud = new CrudVoiture();

    @FXML
    public void initialize() {
        try {
            // Récupérer toutes les voitures et les ajouter à la TableView
            refreshTable();

            marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
            modelCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
            prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
            matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));

            // Ajouter la colonne d'action avec les boutons Modifier/Supprimer
            actionCol.setCellFactory(new Callback<TableColumn<Voiture, Void>, TableCell<Voiture, Void>>() {
                @Override
                public TableCell<Voiture, Void> call(TableColumn<Voiture, Void> param) {
                    return new TableCell<Voiture, Void>() {
                        private final Button editButton = new Button("Modifier");
                        private final Button deleteButton = new Button("Supprimer");
                        private final HBox buttons = new HBox(10, editButton, deleteButton);

                        {
                            editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white;");
                            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

                            editButton.setOnAction(event -> {
                                Voiture selectedVoiture = getTableView().getItems().get(getIndex());
                                openModifierWindow(selectedVoiture);
                            });

                            deleteButton.setOnAction(event -> {
                                Voiture selectedVoiture = getTableView().getItems().get(getIndex());
                                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                confirmation.setTitle("Confirmation de Suppression");
                                confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette voiture ?");
                                confirmation.setContentText("Cette action est irréversible.");
                                confirmation.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) {
                                        try {
                                            crud.deleteVoiture(selectedVoiture.getId());
                                            refreshTable(); // Rafraîchir la table après suppression
                                        } catch (SQLException e) {
                                            afficherErreur("Erreur lors de la suppression : " + e.getMessage());
                                        }
                                    }
                                });
                            });
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : buttons);
                        }
                    };
                }
            });

            // Action du bouton Ajouter
            addOrUpdateButton.setOnAction(event -> openAjouterWindow());
        } catch (Exception e) {
            afficherErreur("Erreur lors de la récupération des voitures : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            ObservableList<Voiture> observableList = FXCollections.observableArrayList(crud.getAllVoitures());
            voitureTable.setItems(observableList);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la récupération des voitures : " + e.getMessage());
        }
    }


    private void openModifierWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVoiture.fxml")); // Assurez-vous que le chemin est correct
            Parent root = loader.load();

            ModifierVoitureController controller = loader.getController();


            Stage stage = new Stage();
            stage.setTitle("Modifier Voiture");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Rafraîchir la table lorsque la fenêtre de modification est fermée
            stage.setOnHidden(event -> refreshTable());
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }
    private void openAjouterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVoiture.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Voiture");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();

            // Rafraîchir la table lorsque la fenêtre d'ajout est fermée
            stage.setOnHidden(event -> refreshTable());
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/
package controllers;

import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.CrudVoiture;

import java.io.IOException;
import java.sql.SQLException;

public class VoitureController {



    @FXML
    private TableColumn<Voiture, String> marqueCol;

    @FXML
    private TableColumn<Voiture, String> modelCol;

    @FXML
    private TableColumn<Voiture, Double> prixCol;

    @FXML
    private TableColumn<Voiture, String> matriculeCol;

    @FXML
    private TableColumn<Voiture, Void> actionCol;

    @FXML
    private Button addOrUpdateButton;

    private final CrudVoiture crud = new CrudVoiture();

    @FXML
    public void initialize() {
        try {
            marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
            modelCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
            prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
            matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));

            addActionButtonsToTable();
            refreshTable();

            addOrUpdateButton.setOnAction(event -> openAjouterWindow());
        } catch (Exception e) {
            afficherErreur("Erreur d'initialisation : " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            ObservableList<Voiture> observableList = FXCollections.observableArrayList(crud.getAllVoitures());
            voitureTable.setItems(observableList);
        } catch (SQLException e) {
            afficherErreur("Erreur lors de la récupération des voitures : " + e.getMessage());
        }
    }

    private void addActionButtonsToTable() {
        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Voiture, Void> call(final TableColumn<Voiture, Void> param) {
                return new TableCell<>() {
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
                                        crud.deleteVoiture(voiture.getId());
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
                };
            }
        });
    }

    private void openModifierWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVoiture.fxml"));
            Parent root = loader.load();

            ModifierVoitureController controller = loader.getController();
            controller.setVoiture(voiture);  // Passer l’objet à modifier

            Stage stage = new Stage();
            stage.setTitle("Modifier Voiture");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> refreshTable());
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture modification : " + e.getMessage());
        }
    }

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

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private TableView<Voiture> voitureTable; // Table principale

    @FXML
    private void handleOpenAjouterVoiture() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjouterVoiture.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            AjouterVoitureController controller = loader.getController();

            // Passer la table au contrôleur
            controller.setVoitureTable(voitureTable);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une voiture");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
