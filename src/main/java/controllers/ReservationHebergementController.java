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

public class ReservationHebergementController {

    // UI Components for Search
    @FXML private ComboBox<String> destinationComboBox;
    @FXML private ComboBox<String> typeHebergementComboBox;
    @FXML private DatePicker dateArriveePicker;
    @FXML private ComboBox<String> nbNuitsComboBox;
    @FXML private Button searchButton;
    
    // UI Components for Results Table
    @FXML private TableView<Hebergement> hebergementsTable;
    @FXML private TableColumn<Hebergement, String> idHebergementColumn;
    @FXML private TableColumn<Hebergement, String> nomColumn;
    @FXML private TableColumn<Hebergement, String> typeColumn;
    @FXML private TableColumn<Hebergement, String> adresseColumn;
    @FXML private TableColumn<Hebergement, String> villeColumn;
    @FXML private TableColumn<Hebergement, String> etoilesColumn;
    @FXML private TableColumn<Hebergement, String> prixNuitColumn;
    @FXML private TableColumn<Hebergement, Void> actionsColumn;
    
    // UI Components for Selected Accommodation Details
    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField adresseField;
    @FXML private TextField villeField;
    @FXML private TextField etoilesField;
    @FXML private TextField prixNuitField;
    
    // UI Components for Reservation Details
    @FXML private DatePicker dateArriveeReservationPicker;
    @FXML private ComboBox<String> nbNuitsReservationComboBox;
    @FXML private ComboBox<String> nbAdultesComboBox;
    @FXML private ComboBox<String> nbEnfantsComboBox;
    @FXML private ComboBox<String> typeChambreComboBox;
    @FXML private TextField prixTotalField;
    @FXML private ComboBox<String> optionsComboBox;
    
    // UI Components for Client Information
    @FXML private TextField nomClientField;
    @FXML private TextField prenomClientField;
    @FXML private TextField emailClientField;
    @FXML private TextField telephoneClientField;
    
    // UI Components for Action Buttons
    @FXML private Button retourButton;
    @FXML private Button reserverButton;
    @FXML private Button ajouterPanierButton;
    
    // Service CRUD pour les hébergements
    private services.CrudHebergement crudHebergement = new services.CrudHebergement();
    
    // Observable list for table data
    private ObservableList<Hebergement> hebergements = FXCollections.observableArrayList();
    
    // Currently selected hebergement
    private Hebergement selectedHebergement;
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        System.out.println("Initializing ReservationHebergementController...");
        
        try {
            // Vérifier si la table hebergement existe et la créer si nécessaire
            crudHebergement.ensureTableExists();
            
            // Initialize ComboBoxes
            initializeComboBoxes();
            
            // Configure table columns
            configureTableColumns();
            
            // Set current date as default
            dateArriveePicker.setValue(LocalDate.now());
            dateArriveeReservationPicker.setValue(LocalDate.now());
            
            // Add listener to table selection
            hebergementsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedHebergement = newSelection;
                    displayHebergementDetails(newSelection);
                }
            });
            
            // Effectuer une recherche initiale pour afficher des résultats immédiatement
            Platform.runLater(this::performInitialSearch);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du contrôleur : " + e.getMessage());
            e.printStackTrace();
        }
        
        // Type de chambre
        typeChambreComboBox.getItems().addAll(
            "Standard", "Supérieure", "Deluxe", "Suite", "Suite Junior", "Suite Présidentielle"
        );
        typeChambreComboBox.setValue("Standard");
        
        // Add listeners for reservation details to calculate total price
        nbNuitsReservationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        nbAdultesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        nbEnfantsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        typeChambreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        optionsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        
        // Load sample data
        loadSampleData();
    }
    
    /**
     * Initialize ComboBoxes with sample data
     */
    private void initializeComboBoxes() {
        // Destination cities
        destinationComboBox.getItems().addAll(
            "Paris", "Londres", "New York", "Rome", "Madrid", "Berlin", 
            "Amsterdam", "Barcelone", "Lisbonne", "Dubaï", "Istanbul"
        );
        destinationComboBox.setValue("Paris"); // Valeur par défaut
        
        // Accommodation types
        typeHebergementComboBox.getItems().addAll(
            "Hôtel", "Appartement", "Maison", "Villa", "Résidence", "Gîte"
        );
        typeHebergementComboBox.setValue("Hôtel"); // Valeur par défaut
        
        // Number of nights (search)
        List<String> nightsOptions = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            nightsOptions.add(String.valueOf(i));
        }
        nbNuitsComboBox.getItems().addAll(nightsOptions);
        nbNuitsComboBox.setValue("1");
        
        // Number of nights (reservation)
        nbNuitsReservationComboBox.getItems().addAll(nightsOptions);
        nbNuitsReservationComboBox.setValue("1");
        
        // Number of adults
        List<String> adultsOptions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            adultsOptions.add(String.valueOf(i));
        }
        nbAdultesComboBox.getItems().addAll(adultsOptions);
        nbAdultesComboBox.setValue("1");
        
        // Number of children
        List<String> childrenOptions = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            childrenOptions.add(String.valueOf(i));
        }
        nbEnfantsComboBox.getItems().addAll(childrenOptions);
        nbEnfantsComboBox.setValue("0");
        
        // Additional options
        optionsComboBox.getItems().addAll(
            "Aucune", "Petit-déjeuner (+10 TND/pers/jour)", "Demi-pension (+25 TND/pers/jour)", 
            "Pension complète (+40 TND/pers/jour)"
        );
        optionsComboBox.setValue("Aucune");
    }
    
    /**
     * Configure table columns
     */
    private void configureTableColumns() {
        idHebergementColumn.setCellValueFactory(new PropertyValueFactory<>("idHebergement"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
        etoilesColumn.setCellValueFactory(new PropertyValueFactory<>("etoiles"));
        prixNuitColumn.setCellValueFactory(new PropertyValueFactory<>("prixNuit"));
        
        // Add action buttons to the table
        addButtonsToTable();
    }
    
    /**
     * Add action buttons to the table
     */
    private void addButtonsToTable() {
        Callback<TableColumn<Hebergement, Void>, TableCell<Hebergement, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Hebergement, Void> call(final TableColumn<Hebergement, Void> param) {
                return new TableCell<>() {
                    private final Button selectButton = new Button("Sélectionner");
                    
                    {
                        selectButton.getStyleClass().add("primary-button");
                        selectButton.setOnAction((ActionEvent event) -> {
                            Hebergement hebergement = getTableView().getItems().get(getIndex());
                            selectedHebergement = hebergement;
                            displayHebergementDetails(hebergement);
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
        hebergements.add(new Hebergement("1", "Hôtel Luxe Paris", "Hôtel", "123 Avenue des Champs-Élysées", "Paris", "5", "200.00"));
        hebergements.add(new Hebergement("2", "Résidence Montmartre", "Résidence", "45 Rue de Montmartre", "Paris", "4", "150.00"));
        hebergements.add(new Hebergement("3", "Appartement Tour Eiffel", "Appartement", "78 Avenue de la Bourdonnais", "Paris", "3", "120.00"));
        hebergements.add(new Hebergement("4", "Maison Parisienne", "Maison", "12 Rue de Rivoli", "Paris", "4", "180.00"));
        hebergements.add(new Hebergement("5", "Villa Luxueuse", "Villa", "34 Boulevard Saint-Germain", "Paris", "5", "250.00"));
        
        hebergementsTable.setItems(hebergements);
    }
    
    /**
     * Display the details of the selected hebergement
     * 
     * @param hebergement The selected hebergement
     */
    private void displayHebergementDetails(Hebergement hebergement) {
        nomField.setText(hebergement.getNom());
        typeField.setText(hebergement.getType());
        adresseField.setText(hebergement.getAdresse());
        villeField.setText(hebergement.getVille());
        etoilesField.setText(hebergement.getEtoiles());
        prixNuitField.setText(hebergement.getPrixNuit() + " TND");
        
        // Calculate total price
        calculateTotalPrice();
    }
    
    /**
     * Effectue une recherche initiale pour afficher des résultats immédiatement
     */
    private void performInitialSearch() {
        // Simuler un clic sur le bouton de recherche
        handleSearch(new ActionEvent());
    }
    
    /**
     * Calculate the total price based on the selected options
     */
    private void calculateTotalPrice() {
        if (selectedHebergement == null) {
            prixTotalField.setText("0.00 TND");
            return;
        }
        
        try {
            double prixNuit = Double.parseDouble(selectedHebergement.getPrixNuit());
            int nbNuits = Integer.parseInt(nbNuitsReservationComboBox.getValue());
            int nbAdultes = Integer.parseInt(nbAdultesComboBox.getValue());
            int nbEnfants = Integer.parseInt(nbEnfantsComboBox.getValue());
            
            // Apply multiplier based on room type
            double chambreMultiplier = 1.0;
            String typeChambre = typeChambreComboBox.getValue();
            if (typeChambre != null) {
                switch (typeChambre) {
                    case "Standard":
                        chambreMultiplier = 1.0;
                        break;
                    case "Supérieure":
                        chambreMultiplier = 1.3;
                        break;
                    case "Deluxe":
                        chambreMultiplier = 1.6;
                        break;
                    case "Suite":
                        chambreMultiplier = 2.0;
                        break;
                    case "Suite Junior":
                        chambreMultiplier = 2.5;
                        break;
                    case "Suite Présidentielle":
                        chambreMultiplier = 4.0;
                        break;
                }
            }
            
            // Calculate base price with room type multiplier
            double totalPrice = prixNuit * chambreMultiplier * nbNuits;
            
            // Add price for additional persons (children count as 0.5 adults)
            if (nbAdultes > 2) {
                totalPrice += (nbAdultes - 2) * prixNuit * 0.3 * nbNuits; // 30% of base price per additional adult
            }
            if (nbEnfants > 0) {
                totalPrice += nbEnfants * prixNuit * 0.15 * nbNuits; // 15% of base price per child
            }
            
            // Add options price
            String option = optionsComboBox.getValue();
            if (option != null && !option.equals("Aucune")) {
                double optionPrice = 0;
                if (option.contains("Petit-déjeuner")) {
                    optionPrice = 10;
                } else if (option.contains("Demi-pension")) {
                    optionPrice = 25;
                } else if (option.contains("Pension complète")) {
                    optionPrice = 40;
                }
                
                totalPrice += optionPrice * (nbAdultes + (nbEnfants * 0.5)) * nbNuits;
            }
            
            prixTotalField.setText(String.format("%.2f TND", totalPrice));
        } catch (NumberFormatException e) {
            prixTotalField.setText("Erreur de calcul");
        }
    }
    
    /**
     * Handle the search button click
     * 
     * @param event The action event
     */
    @FXML
    void handleSearch(ActionEvent event) {
        String destination = destinationComboBox.getValue();
        String typeHebergement = typeHebergementComboBox.getValue();
        LocalDate dateArrivee = dateArriveePicker.getValue();
        String nbNuits = nbNuitsComboBox.getValue();
        
        if (destination == null || typeHebergement == null || dateArrivee == null || nbNuits == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", 
                     "Veuillez remplir tous les champs de recherche.");
            return;
        }
        
        // Format the date
        String formattedDate = dateArrivee.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Clear the current list
        hebergements.clear();
        
        try {
            // Rechercher les hébergements dans la base de données
            List<entities.Hebergement> hebergementsTrouves = crudHebergement.rechercherHebergements(destination, typeHebergement);
            
            if (hebergementsTrouves.isEmpty()) {
                // Si aucun hébergement n'est trouvé, générer des hébergements aléatoires pour la démonstration
                double prix1 = 150 + Math.random() * 150;
                double prix2 = 100 + Math.random() * 100;
                double prix3 = 80 + Math.random() * 80;
                double prix4 = 120 + Math.random() * 100;
                double prix5 = 200 + Math.random() * 150;
                
                entities.Hebergement h1 = new entities.Hebergement(typeHebergement + " Luxe " + destination, typeHebergement, "123 Avenue Principale", destination, 5, prix1, 4, "Un hébergement luxueux au cœur de " + destination, "Disponible");
                entities.Hebergement h2 = new entities.Hebergement("Résidence " + destination + " Centre", "Résidence", "45 Rue du Centre", destination, 4, prix2, 4, "Une résidence confortable en plein centre de " + destination, "Disponible");
                entities.Hebergement h3 = new entities.Hebergement("Appartement " + destination + " Vue", "Appartement", "78 Avenue Panoramique", destination, 3, prix3, 3, "Un appartement avec vue panoramique sur " + destination, "Disponible");
                entities.Hebergement h4 = new entities.Hebergement("Maison " + destination, "Maison", "12 Rue Résidentielle", destination, 4, prix4, 5, "Une maison spacieuse à " + destination, "Disponible");
                entities.Hebergement h5 = new entities.Hebergement("Villa " + destination + " Luxueuse", "Villa", "34 Boulevard Principal", destination, 5, prix5, 6, "Une villa luxueuse avec piscine à " + destination, "Disponible");
                
                // Ajouter les hébergements à la base de données
                crudHebergement.ajouter(h1);
                crudHebergement.ajouter(h2);
                crudHebergement.ajouter(h3);
                crudHebergement.ajouter(h4);
                crudHebergement.ajouter(h5);
                
                // Convertir et ajouter les hébergements à la liste observable
                hebergements.add(convertToHebergementUI(h1));
                hebergements.add(convertToHebergementUI(h2));
                hebergements.add(convertToHebergementUI(h3));
                hebergements.add(convertToHebergementUI(h4));
                hebergements.add(convertToHebergementUI(h5));
            } else {
                // Convertir et ajouter les hébergements trouvés à la liste observable
                for (entities.Hebergement h : hebergementsTrouves) {
                    hebergements.add(convertToHebergementUI(h));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche des hébergements : " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, générer des hébergements aléatoires pour la démonstration
            hebergements.add(new Hebergement("1", typeHebergement + " Luxe " + destination, typeHebergement, "123 Avenue Principale", destination, "5", String.format("%.2f", 150 + Math.random() * 150)));
            hebergements.add(new Hebergement("2", "Résidence " + destination + " Centre", "Résidence", "45 Rue du Centre", destination, "4", String.format("%.2f", 100 + Math.random() * 100)));
            hebergements.add(new Hebergement("3", "Appartement " + destination + " Vue", "Appartement", "78 Avenue Panoramique", destination, "3", String.format("%.2f", 80 + Math.random() * 80)));
            hebergements.add(new Hebergement("4", "Maison " + destination, "Maison", "12 Rue Résidentielle", destination, "4", String.format("%.2f", 120 + Math.random() * 100)));
            hebergements.add(new Hebergement("5", "Villa " + destination + " Luxueuse", "Villa", "34 Boulevard Principal", destination, "5", String.format("%.2f", 200 + Math.random() * 150)));
        }
        
        hebergementsTable.setItems(hebergements);
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
        if (selectedHebergement == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun hébergement sélectionné", 
                     "Veuillez sélectionner un hébergement avant de réserver.");
            return;
        }
        
        if (nomClientField.getText().isEmpty() || prenomClientField.getText().isEmpty() || 
            emailClientField.getText().isEmpty() || telephoneClientField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informations incomplètes", 
                     "Veuillez remplir toutes les informations client.");
            return;
        }
        
        // In a real application, you would save the reservation to the database here
        
        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée", 
                 "Votre réservation pour " + selectedHebergement.getNom() + " a été confirmée.");
        
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
        if (selectedHebergement == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun hébergement sélectionné", 
                     "Veuillez sélectionner un hébergement avant de l'ajouter au panier.");
            return;
        }
        
        // In a real application, you would add the hebergement to the cart here
        
        // Show confirmation
        showAlert(Alert.AlertType.INFORMATION, "Ajout au panier", 
                 "L'hébergement " + selectedHebergement.getNom() + " a été ajouté à votre panier.");
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        // Clear client information
        nomClientField.clear();
        prenomClientField.clear();
        emailClientField.clear();
        telephoneClientField.clear();
        
        // Clear accommodation details
        nomField.clear();
        typeField.clear();
        adresseField.clear();
        villeField.clear();
        etoilesField.clear();
        prixNuitField.clear();
        
        // Reset travel details
        dateArriveeReservationPicker.setValue(LocalDate.now());
        nbNuitsReservationComboBox.setValue("1");
        nbAdultesComboBox.setValue("1");
        nbEnfantsComboBox.setValue("0");
        typeChambreComboBox.setValue("Standard");
        optionsComboBox.setValue("Aucune");
        prixTotalField.clear();
        
        // Clear selection
        selectedHebergement = null;
        hebergementsTable.getSelectionModel().clearSelection();
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
    
    // Cette méthode a été remplacée par l'utilisation du service CrudHebergement
    
    /**
     * Convertit un entities.Hebergement en Hebergement (classe interne)
     * 
     * @param entityHebergement L'hébergement à convertir
     * @return Un objet Hebergement (classe interne)
     */
    private Hebergement convertToHebergementUI(entities.Hebergement entityHebergement) {
        return new Hebergement(
            String.valueOf(entityHebergement.getIdHebergement()),
            entityHebergement.getNom(),
            entityHebergement.getType(),
            entityHebergement.getAdresse(),
            entityHebergement.getVille(),
            String.valueOf(entityHebergement.getEtoiles()),
            String.valueOf(entityHebergement.getPrixNuit())
        );
    }
    
    /**
     * Convertit un Hebergement (classe interne) en entities.Hebergement
     * 
     * @param uiHebergement L'hébergement à convertir
     * @return Un objet entities.Hebergement
     */
    private entities.Hebergement convertToHebergementEntity(Hebergement uiHebergement) {
        return new entities.Hebergement(
            uiHebergement.getNom(),
            uiHebergement.getType(),
            uiHebergement.getAdresse(),
            uiHebergement.getVille(),
            Integer.parseInt(uiHebergement.getEtoiles()),
            Double.parseDouble(uiHebergement.getPrixNuit().replace(" TND", "")),
            4, // Capacité par défaut
            "Description non disponible", // Description par défaut
            "Disponible" // Disponibilité par défaut
        );
    }
    
    /**
     * Model class for Hebergement
     */
    public static class Hebergement {
        private final SimpleStringProperty idHebergement;
        private final SimpleStringProperty nom;
        private final SimpleStringProperty type;
        private final SimpleStringProperty adresse;
        private final SimpleStringProperty ville;
        private final SimpleStringProperty etoiles;
        private final SimpleStringProperty prixNuit;
        
        public Hebergement(String idHebergement, String nom, String type, String adresse, 
                          String ville, String etoiles, String prixNuit) {
            this.idHebergement = new SimpleStringProperty(idHebergement);
            this.nom = new SimpleStringProperty(nom);
            this.type = new SimpleStringProperty(type);
            this.adresse = new SimpleStringProperty(adresse);
            this.ville = new SimpleStringProperty(ville);
            this.etoiles = new SimpleStringProperty(etoiles);
            this.prixNuit = new SimpleStringProperty(prixNuit);
        }
        
        public String getIdHebergement() { return idHebergement.get(); }
        public String getNom() { return nom.get(); }
        public String getType() { return type.get(); }
        public String getAdresse() { return adresse.get(); }
        public String getVille() { return ville.get(); }
        public String getEtoiles() { return etoiles.get(); }
        public String getPrixNuit() { return prixNuit.get(); }
    }
}