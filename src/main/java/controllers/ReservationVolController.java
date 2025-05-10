package controllers;

import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationVolController {

    // UI Components for Search
    @FXML private ComboBox<String> departComboBox;
    @FXML private ComboBox<String> destinationComboBox;
    @FXML private DatePicker dateDepartPicker;
    @FXML private ComboBox<String> passagersComboBox;
    @FXML private Button searchButton;
    
    // UI Components for Results Table
    @FXML private TableView<Vol> volsTable;
    @FXML private TableColumn<Vol, String> numVolColumn;
    @FXML private TableColumn<Vol, String> compagnieColumn;
    @FXML private TableColumn<Vol, String> departColumn;
    @FXML private TableColumn<Vol, String> destinationColumn;
    @FXML private TableColumn<Vol, String> dateDepartColumn;
    @FXML private TableColumn<Vol, String> heureColumn;
    @FXML private TableColumn<Vol, String> prixColumn;
    @FXML private TableColumn<Vol, Void> actionsColumn;
    
    // UI Components for Selected Flight Details
    @FXML private TextField numVolField;
    @FXML private TextField compagnieField;
    @FXML private TextField departField;
    @FXML private TextField destinationField;
    @FXML private TextField dateHeureField;
    @FXML private TextField prixField;
    
    // UI Components for Travel Details
    @FXML private ComboBox<String> typeVoyageComboBox;
    @FXML private ComboBox<String> classeComboBox;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<String> nbPassagersReservationComboBox;
    @FXML private ComboBox<String> optionsVolComboBox;
    @FXML private TextField prixTotalVolField;
    
    // UI Components for Passenger Information
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    
    // UI Components for Action Buttons
    @FXML private Button retourButton;
    @FXML private Button reserverButton;
    @FXML private Button ajouterPanierButton;
    
    // Service CRUD pour les vols
    private services.CrudVol crudVol = new services.CrudVol();
    
    // Observable list for table data
    private ObservableList<Vol> vols = FXCollections.observableArrayList();
    
    // Currently selected vol
    private Vol selectedVol;
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        System.out.println("Initializing ReservationVolController...");
        
        try {
            // Vérifier si la table vol existe et la créer si nécessaire
            crudVol.ensureTableExists();
            
            // Initialize ComboBoxes
            initializeComboBoxes();
            
            // Configure table columns
            configureTableColumns();
            
            // Set current date as default
            dateDepartPicker.setValue(LocalDate.now());
            
            // Add listener to table selection
            volsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedVol = newSelection;
                    displayVolDetails(newSelection);
                }
            });
            
            // Effectuer une recherche initiale pour afficher des résultats immédiatement
            Platform.runLater(this::performInitialSearch);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du contrôleur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Effectue une recherche initiale pour afficher des résultats immédiatement
     */
    private void performInitialSearch() {
        // Simuler un clic sur le bouton de recherche
        handleSearch(new ActionEvent());
    }
    
    /**
     * Initialize ComboBoxes with sample data
     */
    private void initializeComboBoxes() {
        // Departure cities
        departComboBox.getItems().addAll(
            "Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes", 
            "Strasbourg", "Montpellier", "Bordeaux", "Lille"
        );
        departComboBox.setValue("Paris"); // Valeur par défaut
        
        // Destination cities
        destinationComboBox.getItems().addAll(
            "Londres", "New York", "Tokyo", "Rome", "Madrid", "Berlin", 
            "Amsterdam", "Barcelone", "Lisbonne", "Dubaï", "Istanbul"
        );
        destinationComboBox.setValue("Londres"); // Valeur par défaut
        
        // Number of passengers (search)
        List<String> passengersOptions = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            passengersOptions.add(String.valueOf(i));
        }
        passagersComboBox.getItems().addAll(passengersOptions);
        passagersComboBox.setValue("1");
        
        // Travel Details ComboBoxes
        
        // Type de voyage
        typeVoyageComboBox.getItems().addAll(
            "Aller simple", "Aller-retour"
        );
        typeVoyageComboBox.setValue("Aller simple");
        
        // Classe
        classeComboBox.getItems().addAll(
            "Économique", "Premium Économique", "Affaires", "Première"
        );
        classeComboBox.setValue("Économique");
        
        // Set current date + 7 days as default return date
        dateRetourPicker.setValue(LocalDate.now().plusDays(7));
        
        // Number of passengers (reservation)
        nbPassagersReservationComboBox.getItems().addAll(passengersOptions);
        nbPassagersReservationComboBox.setValue("1");
        
        // Options
        optionsVolComboBox.getItems().addAll(
            "Aucune", "Bagage supplémentaire (+30 TND)", "Repas spécial (+15 TND)", 
            "Siège préférentiel (+25 TND)", "Assurance annulation (+20 TND)"
        );
        optionsVolComboBox.setValue("Aucune");
        
        // Add listeners for travel details to calculate total price
        typeVoyageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        classeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        nbPassagersReservationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        optionsVolComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
    }
    
    /**
     * Calculate the total price based on the selected options
     */
    private void calculateTotalPrice() {
        if (selectedVol == null) {
            prixTotalVolField.setText("0.00 TND");
            return;
        }
        
        try {
            double prixBase = Double.parseDouble(selectedVol.getPrix());
            int nbPassagers = Integer.parseInt(nbPassagersReservationComboBox.getValue());
            
            // Apply multiplier based on class
            double classeMultiplier = 1.0;
            String classe = classeComboBox.getValue();
            if (classe != null) {
                switch (classe) {
                    case "Économique":
                        classeMultiplier = 1.0;
                        break;
                    case "Premium Économique":
                        classeMultiplier = 1.5;
                        break;
                    case "Affaires":
                        classeMultiplier = 2.5;
                        break;
                    case "Première":
                        classeMultiplier = 4.0;
                        break;
                }
            }
            
            // Calculate base price with class multiplier
            double totalPrice = prixBase * classeMultiplier * nbPassagers;
            
            // Apply multiplier for round trip
            String typeVoyage = typeVoyageComboBox.getValue();
            if (typeVoyage != null && typeVoyage.equals("Aller-retour")) {
                totalPrice *= 1.8; // 10% discount on return flight
            }
            
            // Add options price
            String option = optionsVolComboBox.getValue();
            if (option != null && !option.equals("Aucune")) {
                double optionPrice = 0;
                if (option.contains("Bagage supplémentaire")) {
                    optionPrice = 30;
                } else if (option.contains("Repas spécial")) {
                    optionPrice = 15;
                } else if (option.contains("Siège préférentiel")) {
                    optionPrice = 25;
                } else if (option.contains("Assurance annulation")) {
                    optionPrice = 20;
                }
                
                totalPrice += optionPrice * nbPassagers;
            }
            
            prixTotalVolField.setText(String.format("%.2f TND", totalPrice));
        } catch (NumberFormatException e) {
            prixTotalVolField.setText("Erreur de calcul");
        }
    }
    
    /**
     * Configure table columns
     */
    private void configureTableColumns() {
        numVolColumn.setCellValueFactory(new PropertyValueFactory<>("numVol"));
        compagnieColumn.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
        departColumn.setCellValueFactory(new PropertyValueFactory<>("depart"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateDepartColumn.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        heureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Add action buttons to the table
        addButtonsToTable();
    }
    
    /**
     * Add action buttons to the table
     */
    private void addButtonsToTable() {
        Callback<TableColumn<Vol, Void>, TableCell<Vol, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Vol, Void> call(final TableColumn<Vol, Void> param) {
                return new TableCell<>() {
                    private final Button selectButton = new Button("Sélectionner");
                    
                    {
                        selectButton.getStyleClass().add("primary-button");
                        selectButton.setOnAction((ActionEvent event) -> {
                            Vol vol = getTableView().getItems().get(getIndex());
                            selectedVol = vol;
                            displayVolDetails(vol);
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(selectButton);
                        }
                    }
                };
            }
        };
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    /**
     * Load sample data into the table
     */
    private void loadSampleData() {
        vols.add(new Vol("AF1234", "Air France", "Paris", "Londres", "2024-05-15", "08:30", "150.00"));
        vols.add(new Vol("BA4567", "British Airways", "Paris", "New York", "2024-05-15", "10:45", "450.00"));
        vols.add(new Vol("LH7890", "Lufthansa", "Paris", "Berlin", "2024-05-15", "12:15", "180.00"));
        vols.add(new Vol("IB2345", "Iberia", "Paris", "Madrid", "2024-05-15", "14:30", "120.00"));
        vols.add(new Vol("EK6789", "Emirates", "Paris", "Dubaï", "2024-05-15", "16:45", "380.00"));
        
        volsTable.setItems(vols);
    }
    
    /**
     * Display the details of the selected vol
     * 
     * @param vol The selected vol
     */
    private void displayVolDetails(Vol vol) {
        numVolField.setText(vol.getNumVol());
        compagnieField.setText(vol.getCompagnie());
        departField.setText(vol.getDepart());
        destinationField.setText(vol.getDestination());
        dateHeureField.setText(vol.getDateDepart() + " à " + vol.getHeure());
        prixField.setText(vol.getPrix() + " TND");
        
        // Calculate total price based on selected options
        calculateTotalPrice();
    }
    
    /**
     * Handle the search button click
     * 
     * @param event The action event
     */
    @FXML
    void handleSearch(ActionEvent event) {
        String depart = departComboBox.getValue();
        String destination = destinationComboBox.getValue();
        LocalDate dateDepart = dateDepartPicker.getValue();
        String nbPassagers = passagersComboBox.getValue();
        
        if (depart == null || destination == null || dateDepart == null || nbPassagers == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", 
                     "Veuillez remplir tous les champs de recherche.");
            return;
        }
        
        if (depart.equals(destination)) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "La ville de départ et la destination ne peuvent pas être identiques.");
            return;
        }
        
        // Format the date
        String formattedDate = dateDepart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Clear the current list
        vols.clear();
        
        try {
            // Rechercher les vols dans la base de données
            List<entities.Vol> volsTrouves = crudVol.rechercherVols(depart, destination, formattedDate);
            
            if (volsTrouves.isEmpty()) {
                // Si aucun vol n'est trouvé, générer des vols aléatoires pour la démonstration
                double prix1 = 100 + Math.random() * 300;
                double prix2 = 100 + Math.random() * 300;
                double prix3 = 100 + Math.random() * 300;
                double prix4 = 100 + Math.random() * 300;
                double prix5 = 100 + Math.random() * 300;
                
                entities.Vol vol1 = new entities.Vol("AF" + (1000 + (int)(Math.random() * 9000)), "Air France", depart, destination, formattedDate, "08:30", prix1, 200, "Disponible");
                entities.Vol vol2 = new entities.Vol("BA" + (1000 + (int)(Math.random() * 9000)), "British Airways", depart, destination, formattedDate, "10:45", prix2, 200, "Disponible");
                entities.Vol vol3 = new entities.Vol("LH" + (1000 + (int)(Math.random() * 9000)), "Lufthansa", depart, destination, formattedDate, "12:15", prix3, 200, "Disponible");
                entities.Vol vol4 = new entities.Vol("IB" + (1000 + (int)(Math.random() * 9000)), "Iberia", depart, destination, formattedDate, "14:30", prix4, 200, "Disponible");
                entities.Vol vol5 = new entities.Vol("EK" + (1000 + (int)(Math.random() * 9000)), "Emirates", depart, destination, formattedDate, "16:45", prix5, 200, "Disponible");
                
                // Ajouter les vols à la base de données
                crudVol.ajouter(vol1);
                crudVol.ajouter(vol2);
                crudVol.ajouter(vol3);
                crudVol.ajouter(vol4);
                crudVol.ajouter(vol5);
                
                // Convertir et ajouter les vols à la liste observable
                vols.add(convertToVolUI(vol1));
                vols.add(convertToVolUI(vol2));
                vols.add(convertToVolUI(vol3));
                vols.add(convertToVolUI(vol4));
                vols.add(convertToVolUI(vol5));
            } else {
                // Convertir et ajouter les vols trouvés à la liste observable
                for (entities.Vol v : volsTrouves) {
                    vols.add(convertToVolUI(v));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche des vols : " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, générer des vols aléatoires pour la démonstration
            vols.add(new Vol("AF" + (1000 + (int)(Math.random() * 9000)), "Air France", depart, destination, formattedDate, "08:30", String.format("%.2f", 100 + Math.random() * 300)));
            vols.add(new Vol("BA" + (1000 + (int)(Math.random() * 9000)), "British Airways", depart, destination, formattedDate, "10:45", String.format("%.2f", 100 + Math.random() * 300)));
            vols.add(new Vol("LH" + (1000 + (int)(Math.random() * 9000)), "Lufthansa", depart, destination, formattedDate, "12:15", String.format("%.2f", 100 + Math.random() * 300)));
            vols.add(new Vol("IB" + (1000 + (int)(Math.random() * 9000)), "Iberia", depart, destination, formattedDate, "14:30", String.format("%.2f", 100 + Math.random() * 300)));
            vols.add(new Vol("EK" + (1000 + (int)(Math.random() * 9000)), "Emirates", depart, destination, formattedDate, "16:45", String.format("%.2f", 100 + Math.random() * 300)));
        }
        
        volsTable.setItems(vols);
    }
    
    /**
     * Handle the return button click
     * 
     * @param event The action event
     */
    @FXML
    void handleRetour(ActionEvent event) {
        try {
            // Load the main reservations screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestion_Reservations_Responsive.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) retourButton.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Travel Agency - Gestion des Réservations");
            
            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de retourner à l'écran précédent: " + e.getMessage());
        }
    }
    
    /**
     * Handle the reserve button click
     * 
     * @param event The action event
     */
    @FXML
    void handleReserver(ActionEvent event) {
        if (selectedVol == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun vol sélectionné", 
                     "Veuillez sélectionner un vol avant de réserver.");
            return;
        }
        
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || 
            emailField.getText().isEmpty() || telephoneField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informations incomplètes", 
                     "Veuillez remplir toutes les informations passager.");
            return;
        }
        
        // In a real application, you would save the reservation to the database here
        
        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée", 
                 "Votre réservation pour le vol " + selectedVol.getNumVol() + " a été confirmée.");
        
        // Clear the form
        clearForm();
    }
    
    /**
     * Handle the add to cart button click
     * 
     * @param event The action event
     */
    @FXML
    void handleAjouterPanier(ActionEvent event) {
        if (selectedVol == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun vol sélectionné", 
                     "Veuillez sélectionner un vol avant de l'ajouter au panier.");
            return;
        }
        
        // In a real application, you would add the vol to the cart here
        
        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Ajout au panier", 
                 "Le vol " + selectedVol.getNumVol() + " a été ajouté à votre panier.");
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        // Clear passenger information
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        
        // Clear flight details
        numVolField.clear();
        compagnieField.clear();
        departField.clear();
        destinationField.clear();
        dateHeureField.clear();
        prixField.clear();
        
        // Reset travel details
        typeVoyageComboBox.setValue("Aller simple");
        classeComboBox.setValue("Économique");
        dateRetourPicker.setValue(LocalDate.now().plusDays(7));
        nbPassagersReservationComboBox.setValue("1");
        optionsVolComboBox.setValue("Aucune");
        prixTotalVolField.clear();
        
        // Clear selection
        selectedVol = null;
        volsTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Show an alert dialog
     * 
     * @param type The alert type
     * @param title The alert title
     * @param content The alert content
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Cette méthode a été remplacée par l'utilisation du service CrudVol
    
    /**
     * Convertit un entities.Vol en Vol (classe interne)
     * 
     * @param entityVol Le vol à convertir
     * @return Un objet Vol (classe interne)
     */
    private Vol convertToVolUI(entities.Vol entityVol) {
        return new Vol(
            entityVol.getNumVol(),
            entityVol.getCompagnie(),
            entityVol.getDepart(),
            entityVol.getDestination(),
            entityVol.getDateDepart(),
            entityVol.getHeure(),
            String.valueOf(entityVol.getPrix())
        );
    }
    
    /**
     * Convertit un Vol (classe interne) en entities.Vol
     * 
     * @param uiVol Le vol à convertir
     * @return Un objet entities.Vol
     */
    private entities.Vol convertToVolEntity(Vol uiVol) {
        return new entities.Vol(
            uiVol.getNumVol(),
            uiVol.getCompagnie(),
            uiVol.getDepart(),
            uiVol.getDestination(),
            uiVol.getDateDepart(),
            uiVol.getHeure(),
            Double.parseDouble(uiVol.getPrix()),
            200, // Capacité par défaut
            "Disponible" // Statut par défaut
        );
    }
    
    /**
     * Model class for Vol
     */
    public static class Vol {
        private final SimpleStringProperty numVol;
        private final SimpleStringProperty compagnie;
        private final SimpleStringProperty depart;
        private final SimpleStringProperty destination;
        private final SimpleStringProperty dateDepart;
        private final SimpleStringProperty heure;
        private final SimpleStringProperty prix;
        
        public Vol(String numVol, String compagnie, String depart, String destination, 
                  String dateDepart, String heure, String prix) {
            this.numVol = new SimpleStringProperty(numVol);
            this.compagnie = new SimpleStringProperty(compagnie);
            this.depart = new SimpleStringProperty(depart);
            this.destination = new SimpleStringProperty(destination);
            this.dateDepart = new SimpleStringProperty(dateDepart);
            this.heure = new SimpleStringProperty(heure);
            this.prix = new SimpleStringProperty(prix);
        }
        
        public String getNumVol() { return numVol.get(); }
        public String getCompagnie() { return compagnie.get(); }
        public String getDepart() { return depart.get(); }
        public String getDestination() { return destination.get(); }
        public String getDateDepart() { return dateDepart.get(); }
        public String getHeure() { return heure.get(); }
        public String getPrix() { return prix.get(); }
    }
}