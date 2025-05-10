package controllers;

import services.CrudReservation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ReservationController {

    @FXML private TextField idReservationField;
    @FXML private DatePicker dateReservationPicker;
    @FXML private TextField idHotelField;
    @FXML private TextField idVolsField;
    @FXML private TextField idVoitureField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField prixField;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> idReservationColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, String> idHotelColumn;
    @FXML private TableColumn<Reservation, String> idVolsColumn;
    @FXML private TableColumn<Reservation, String> idVoitureColumn;
    @FXML private TableColumn<Reservation, String> statutColumn;
    @FXML private TableColumn<Reservation, String> prixColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    
    // Navigation buttons
    @FXML private Button reserverVolButton;
    @FXML private Button reserverHebergementButton;

    // CRUD service for reservations
    private CrudReservation crudReservation = new CrudReservation();
    
    // Observable list for table data
    private ObservableList<Reservation> reservations;

    @FXML
    public void initialize() {
        // Initialize ComboBox
        statutComboBox.getItems().addAll("Confirmé", "En attente", "Annulé");
        
        // Set current date as default
        dateReservationPicker.setValue(LocalDate.now());
        
        // Configure table columns
        idReservationColumn.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        idHotelColumn.setCellValueFactory(new PropertyValueFactory<>("idHotel"));
        idVolsColumn.setCellValueFactory(new PropertyValueFactory<>("idVols"));
        idVoitureColumn.setCellValueFactory(new PropertyValueFactory<>("idVoiture"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Configure actions column with buttons
        addButtonsToTable();
        
        // Load data from database
        loadReservationsFromDatabase();
        
        // Add listener to table selection
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadReservationDetails(newSelection);
            }
        });
    }
    
    // Load reservations from database
    private void loadReservationsFromDatabase() {
        System.out.println("=== LOADING RESERVATIONS FROM DATABASE ===");
        try {
            reservations = crudReservation.getAllReservations();
            System.out.println("Loaded " + reservations.size() + " reservations from database");
            
            // Debug: print all loaded reservations
            for (Reservation r : reservations) {
                System.out.println("ID: " + r.getIdReservation() + 
                                  ", Date: " + r.getDate() + 
                                  ", Hotel: " + r.getIdHotel() + 
                                  ", Vols: " + r.getIdVols() + 
                                  ", Voiture: " + r.getIdVoiture() + 
                                  ", Statut: " + r.getStatut() + 
                                  ", Prix: " + r.getPrix());
            }
            
            reservationsTable.setItems(reservations);
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", 
                     "Impossible de charger les réservations: " + e.getMessage());
            
            // If database loading fails, use sample data for demonstration
            System.out.println("Using sample data instead");
            reservations = FXCollections.observableArrayList(
                new Reservation("1001", "2024-05-01", "101", "201", "301", "Confirmé", "1200"),
                new Reservation("1002", "2024-05-02", "102", "202", "302", "En attente", "950"),
                new Reservation("1003", "2024-05-03", "103", "203", "303", "Confirmé", "1500")
            );
            reservationsTable.setItems(reservations);
        }
    }

    private void addButtonsToTable() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("Voir");
                    private final Button editBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");
                    private final HBox pane = new HBox(5, viewBtn, editBtn, deleteBtn);

                    {
                        viewBtn.getStyleClass().add("secondary-button");
                        viewBtn.setStyle("-fx-padding: 2px 5px; -fx-font-size: 11px;");
                        editBtn.getStyleClass().add("secondary-button");
                        editBtn.setStyle("-fx-padding: 2px 5px; -fx-font-size: 11px;");
                        deleteBtn.getStyleClass().add("secondary-button");
                        deleteBtn.setStyle("-fx-padding: 2px 5px; -fx-font-size: 11px; -fx-border-color: #f44336; -fx-text-fill: #f44336;");
                        
                        viewBtn.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            viewReservation(reservation);
                        });
                        
                        editBtn.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            editReservation(reservation);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            deleteReservation(reservation);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        
        actionsColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void ajouterReservation() {
        if (!validateInputs()) {
            return;
        }

        try {
            System.out.println("=== ADDING RESERVATION ===");
            System.out.println("ID: " + idReservationField.getText());
            System.out.println("Date: " + dateReservationPicker.getValue().format(DateTimeFormatter.ISO_DATE));
            System.out.println("Hotel ID: " + idHotelField.getText());
            System.out.println("Vols ID: " + idVolsField.getText());
            System.out.println("Voiture ID: " + idVoitureField.getText());
            System.out.println("Statut: " + statutComboBox.getValue());
            System.out.println("Prix: " + prixField.getText());
            
            // Create new reservation object
            Reservation newReservation = new Reservation(
                idReservationField.getText(),
                dateReservationPicker.getValue().format(DateTimeFormatter.ISO_DATE),
                idHotelField.getText(),
                idVolsField.getText(),
                idVoitureField.getText(),
                statutComboBox.getValue(),
                prixField.getText()
            );
            
            boolean success = false;
            boolean isUpdate = false;
            
            // Check if we're updating an existing reservation
            Optional<Reservation> existingReservation = reservations.stream()
                    .filter(r -> r.getIdReservation().equals(idReservationField.getText()))
                    .findFirst();
            
            if (existingReservation.isPresent()) {
                System.out.println("Updating existing reservation");
                // Update existing reservation in database
                success = crudReservation.updateReservation(newReservation);
                isUpdate = true;
            } else {
                System.out.println("Adding new reservation");
                // Add new reservation to database
                success = crudReservation.addReservation(newReservation);
            }
            
            System.out.println("Operation " + (success ? "successful" : "failed"));
            
            if (success) {
                // Reload data from database
                loadReservationsFromDatabase();
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation " + 
                          (isUpdate ? "mise à jour" : "ajoutée") + " avec succès.");
                
                // Reset form
                resetForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de " + 
                         (isUpdate ? "mettre à jour" : "ajouter") + " la réservation.");
            }
        } catch (Exception e) {
            System.err.println("Exception in ajouterReservation: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void resetForm() {
        idReservationField.clear();
        dateReservationPicker.setValue(LocalDate.now());
        idHotelField.clear();
        idVolsField.clear();
        idVoitureField.clear();
        statutComboBox.setValue(null);
        prixField.clear();
    }
    
    @FXML
    private void handleTestDatabase() {
        try {
            // Test database connection
            boolean connectionOk = crudReservation.testConnection();
            
            if (connectionOk) {
                showAlert(Alert.AlertType.INFORMATION, "Test de connexion", 
                         "Connexion à la base de données réussie!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Test de connexion", 
                         "Échec de la connexion à la base de données.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Test de connexion", 
                     "Erreur lors du test de connexion: " + e.getMessage());
        }
    }
    
    private void viewReservation(Reservation reservation) {
        loadReservationDetails(reservation);
        
        // Make fields read-only
        idReservationField.setEditable(false);
        dateReservationPicker.setDisable(true);
        idHotelField.setEditable(false);
        idVolsField.setEditable(false);
        idVoitureField.setEditable(false);
        statutComboBox.setDisable(true);
        prixField.setEditable(false);
        
        // Show info message
        showAlert(Alert.AlertType.INFORMATION, "Information", 
                 "Consultation de la réservation ID: " + reservation.getIdReservation());
    }
    
    private void editReservation(Reservation reservation) {
        loadReservationDetails(reservation);
        
        // Make fields editable
        idReservationField.setEditable(true);
        dateReservationPicker.setDisable(false);
        idHotelField.setEditable(true);
        idVolsField.setEditable(true);
        idVoitureField.setEditable(true);
        statutComboBox.setDisable(false);
        prixField.setEditable(true);
    }
    
    private void loadReservationDetails(Reservation reservation) {
        idReservationField.setText(reservation.getIdReservation());
        dateReservationPicker.setValue(LocalDate.parse(reservation.getDate()));
        idHotelField.setText(reservation.getIdHotel());
        idVolsField.setText(reservation.getIdVols());
        idVoitureField.setText(reservation.getIdVoiture());
        statutComboBox.setValue(reservation.getStatut());
        prixField.setText(reservation.getPrix());
    }
    
    private void deleteReservation(Reservation reservation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer la réservation");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = crudReservation.deleteReservation(reservation.getIdReservation());
                
                if (success) {
                    // Reload data from database
                    loadReservationsFromDatabase();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès.");
                    resetForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la réservation.");
                }
            }
        });
    }
    
    private boolean validateInputs() {
        if (idReservationField.getText().isEmpty() || dateReservationPicker.getValue() == null || 
            idHotelField.getText().isEmpty() || idVolsField.getText().isEmpty() || 
            idVoitureField.getText().isEmpty() || statutComboBox.getValue() == null ||
            prixField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", "Veuillez remplir tous les champs.");
            return false;
        }
        
        // Validate ID format (assuming numeric IDs)
        try {
            Integer.parseInt(idReservationField.getText());
            Integer.parseInt(idHotelField.getText());
            Integer.parseInt(idVolsField.getText());
            Integer.parseInt(idVoitureField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "Les identifiants doivent être des nombres entiers.");
            return false;
        }
        
        // Validate price format
        try {
            Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "Le prix doit être un nombre valide.");
            return false;
        }
        
        // Validate date (not in the past)
        if (dateReservationPicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "La date de réservation ne peut pas être dans le passé.");
            return false;
        }
        
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Handle the "Réserver un Vol" button click
     * 
     * @param event The action event
     */
    @FXML
    void handleReserverVol(ActionEvent event) {
        try {
            // Load the vol reservation screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation_Vol.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) reserverVolButton.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Travel Agency - Réservation de Vol");
            
            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'ouvrir l'écran de réservation de vol: " + e.getMessage());
        }
    }
    
    /**
     * Handle the "Réserver un Hébergement" button click
     * 
     * @param event The action event
     */
    @FXML
    void handleReserverHebergement(ActionEvent event) {
        try {
            // Load the hebergement reservation screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation_Hebergement.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) reserverHebergementButton.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Travel Agency - Réservation d'Hébergement");
            
            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'ouvrir l'écran de réservation d'hébergement: " + e.getMessage());
        }
    }
    
    /**
     * Handle the "Retour au Panier" menu item click
     * 
     * @param event The action event
     */
    @FXML
    void handleRetourPanier(ActionEvent event) {
        try {
            // Load the panier screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestion_Panier_Responsive.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) reserverVolButton.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Travel Agency - Gestion du Panier");
            
            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'ouvrir l'écran de gestion du panier: " + e.getMessage());
        }
    }
    
    /**
     * Handle the "Rafraîchir" menu item click
     * 
     * @param event The action event
     */
    @FXML
    void handleRefresh(ActionEvent event) {
        // Reload data from database
        loadReservationsFromDatabase();
        
        // Reset form
        resetForm();
        
        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Rafraîchissement", 
                 "Les données ont été rafraîchies avec succès.");
    }
    
    /**
     * Handle the "À propos" menu item click
     * 
     * @param event The action event
     */
    @FXML
    void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Travel Agency - Système de Gestion");
        alert.setContentText("Version: 1.0\n" +
                            "Développé par: Équipe Travel Agency\n\n" +
                            "Ce système permet de gérer les réservations de vols, " +
                            "d'hébergements et autres services touristiques.");
        
        alert.showAndWait();
    }
    
    // Model class for Reservation
    public static class Reservation {
        private final SimpleStringProperty idReservation;
        private final SimpleStringProperty date;
        private final SimpleStringProperty idHotel;
        private final SimpleStringProperty idVols;
        private final SimpleStringProperty idVoiture;
        private final SimpleStringProperty statut;
        private final SimpleStringProperty prix;
        
        public Reservation(String idReservation, String date, String idHotel, String idVols, 
                          String idVoiture, String statut, String prix) {
            this.idReservation = new SimpleStringProperty(idReservation);
            this.date = new SimpleStringProperty(date);
            this.idHotel = new SimpleStringProperty(idHotel);
            this.idVols = new SimpleStringProperty(idVols);
            this.idVoiture = new SimpleStringProperty(idVoiture);
            this.statut = new SimpleStringProperty(statut);
            this.prix = new SimpleStringProperty(prix);
        }

        public String getIdReservation() { return idReservation.get(); }
        public String getDate() { return date.get(); }
        public String getIdHotel() { return idHotel.get(); }
        public String getIdVols() { return idVols.get(); }
        public String getIdVoiture() { return idVoiture.get(); }
        public String getStatut() { return statut.get(); }
        public String getPrix() { return prix.get(); }
    }
}
