package controllers;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PanierController {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/pi";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML private DatePicker createdAtPicker;
    @FXML private GridPane gridPane;
    @FXML private TextField idHebergementField;
    @FXML private TextField idPanierField;
    @FXML private TextField idReservationField;
    @FXML private TextField idVolField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField totalField;
    
    // UI elements for table and details
    @FXML private TableView<Panier> paniersTable;
    @FXML private TableColumn<Panier, String> idPanierColumn;
    @FXML private TableColumn<Panier, String> idReservationColumn;
    @FXML private TableColumn<Panier, String> dateColumn;
    @FXML private TableColumn<Panier, String> totalColumn;
    @FXML private TableColumn<Panier, String> statutColumn;
    @FXML private TableColumn<Panier, Void> actionsColumn;
    
    @FXML private ListView<String> panierItemsListView;
    @FXML private Label subtotalLabel;
    @FXML private Label taxesLabel;
    @FXML private Label totalLabel;
    @FXML private Button paymentButton;
    @FXML private Button openReservationsButton;
    
    // Observable list for table data
    private ObservableList<Panier> paniers;
    
    // Sample items for different paniers
    private final ObservableList<String> panier1Items = FXCollections.observableArrayList(
            "Vol Paris-Rome: 150.00 TND",
            "Hôtel Roma Luxe (3 nuits): 80.00 TND",
            "Assurance voyage: 20.00 €"
    );
    
    private final ObservableList<String> panier2Items = FXCollections.observableArrayList(
            "Vol Madrid-Berlin: 120.50 TND",
            "Hôtel Berlin Central (2 nuits): 55.00 TND"
    );
    
    private final ObservableList<String> panier3Items = FXCollections.observableArrayList(
            "Vol Lyon-Londres: 180.75 TND",
            "Hôtel London Bridge (4 nuits): 120.00 TND",
            "Location voiture: 20.00 TND"
    );
    
    private ObservableList<String> panierItems = FXCollections.observableArrayList();

    /**
     * Check if the panier table exists and create it if it doesn't
     */
    private void ensurePanierTableExists() {
        try (Connection conn = getConnection()) {
            // First, check for and drop any foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                System.out.println("Checking for foreign key constraints...");
                
                // Try to disable foreign key checks
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
                System.out.println("Disabled foreign key checks");
                
                // Get information about foreign keys
                try {
                    // Check for constraints in information_schema
                    String constraintQuery = 
                        "SELECT TABLE_NAME, CONSTRAINT_NAME FROM information_schema.TABLE_CONSTRAINTS " +
                        "WHERE CONSTRAINT_TYPE = 'FOREIGN KEY' AND TABLE_SCHEMA = 'pi'";
                    
                    try (ResultSet rs = stmt.executeQuery(constraintQuery)) {
                        while (rs.next()) {
                            String tableName = rs.getString("TABLE_NAME");
                            String constraintName = rs.getString("CONSTRAINT_NAME");
                            
                            System.out.println("Found constraint: " + constraintName + " on table " + tableName);
                            
                            // Try to drop the constraint
                            try {
                                String dropSQL = "ALTER TABLE " + tableName + 
                                               " DROP FOREIGN KEY " + constraintName;
                                stmt.executeUpdate(dropSQL);
                                System.out.println("Dropped foreign key constraint: " + constraintName);
                            } catch (SQLException e) {
                                System.out.println("Could not drop constraint: " + e.getMessage());
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error checking constraints: " + e.getMessage());
                }
                
                // Drop tables if they exist
                stmt.executeUpdate("DROP TABLE IF EXISTS panier_item");
                stmt.executeUpdate("DROP TABLE IF EXISTS panier");
                System.out.println("Dropped existing panier tables");
                
                // Create the panier table
                String createTableSQL = "CREATE TABLE panier (" +
                    "idPanier INT PRIMARY KEY AUTO_INCREMENT, " +
                    "idReservation INT, " +
                    "date DATE, " +
                    "total FLOAT" +
                    ")";
                
                stmt.executeUpdate(createTableSQL);
                System.out.println("Panier table created successfully.");
                
                // Create the panier_item table
                String createItemTableSQL = "CREATE TABLE panier_item (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "idPanier INT, " +
                    "description VARCHAR(255), " +
                    "prix FLOAT" +
                    ")";
                
                stmt.executeUpdate(createItemTableSQL);
                System.out.println("Panier_item table created successfully.");
                
                // Check if reservation table exists
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "reservation", null);
                
                if (!tables.next()) {
                    System.out.println("Reservation table does not exist. Creating it...");
                    
                    String createReservationSQL = "CREATE TABLE reservation (" +
                        "idReservation INT PRIMARY KEY AUTO_INCREMENT, " +
                        "dateReservation DATE, " +
                        "montantTotal FLOAT, " +
                        "statut VARCHAR(50), " +
                        "idVol INT, " +
                        "idHebergement INT" +
                        ")";
                    
                    stmt.executeUpdate(createReservationSQL);
                    System.out.println("Reservation table created successfully.");
                    
                    // Add some sample reservations
                    String insertSQL1 = "INSERT INTO reservation (idReservation, dateReservation, montantTotal, statut, idVol, idHebergement) " +
                                       "VALUES (1, '2024-05-01', 250.00, 'Confirmé', 101, 201)";
                    String insertSQL2 = "INSERT INTO reservation (idReservation, dateReservation, montantTotal, statut, idVol, idHebergement) " +
                                       "VALUES (2, '2024-05-02', 175.50, 'En attente', 102, 202)";
                    String insertSQL3 = "INSERT INTO reservation (idReservation, dateReservation, montantTotal, statut, idVol, idHebergement) " +
                                       "VALUES (3, '2024-05-03', 320.75, 'Confirmé', 103, 203)";
                    
                    stmt.executeUpdate(insertSQL1);
                    stmt.executeUpdate(insertSQL2);
                    stmt.executeUpdate(insertSQL3);
                    System.out.println("Sample reservations added successfully.");
                } else {
                    System.out.println("Reservation table already exists.");
                }
                
                // Re-enable foreign key checks
                stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
                System.out.println("Re-enabled foreign key checks");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        System.out.println("=== INITIALIZING PANIER CONTROLLER ===");
        
        // Initialize ComboBox with standardized status values
        statutComboBox.getItems().addAll("Confirmé", "En attente", "Annulé");
        
        // Set current date as default
        createdAtPicker.setValue(LocalDate.now());
        
        // Ensure database tables exist
        ensurePanierTableExists();
        
        // Configure table columns
        if (paniersTable != null) {
            idPanierColumn.setCellValueFactory(new PropertyValueFactory<>("idPanier"));
            idReservationColumn.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
            statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
            
            // Configure actions column with buttons
            addButtonsToTable();
            
            // Load data from database
            loadPaniersFromDatabase();
            
            // Add listener to table selection
            paniersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadPanierDetails(newSelection);
                }
            });
        }
        
        // Create a new panier with a unique ID to start with
        createNewPanier();
        
        // Initialize ListView with default items
        if (panierItemsListView != null) {
            panierItems.addAll(panier1Items);
            panierItemsListView.setItems(panierItems);
        }
        
        // Initialize summary labels
        if (subtotalLabel != null && taxesLabel != null && totalLabel != null) {
            updateSummary();
        }
        
        // Initialize payment button
        if (paymentButton != null) {
            paymentButton.setOnAction(event -> handlePayment());
        }
        
        // Initialize open reservations button with a direct approach
        if (openReservationsButton != null) {
            System.out.println("Initializing openReservationsButton");
            openReservationsButton.setOnAction(event -> {
                System.out.println("openReservationsButton clicked");
                openReservationsDirectly();
            });
        }
    }
    

    
    private void openReservationsDirectly() {
        System.out.println("=== DIRECTLY OPENING RESERVATION WINDOW ===");
        try {
            // Load the FXML file directly
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestion_Reservations_Responsive.fxml"));
            Parent root = loader.load();
            
            // Create a new stage
            Stage stage = new Stage();
            stage.setTitle("Travel Agency - Gestion des Réservations");
            stage.setScene(new Scene(root));
            
            // Make the window responsive
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            
            // Show the stage
            stage.show();
            
            System.out.println("Successfully opened reservation window directly");
        } catch (Exception e) {
            System.err.println("Error directly opening reservation window: " + e.getMessage());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'ouvrir la fenêtre de gestion des réservations: " + e.getMessage());
        }
    }
    
    /**
     * Get connection to database
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        try {
            // Make sure the MySQL JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
            
            // Get and return the connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw e;
        }
    }
    
    /**
     * Test the database connection
     * 
     * @return true if connection is successful, false otherwise
     */
    private boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all paniers from the database
     * 
     * @return ObservableList of all paniers
     */
    private ObservableList<Panier> getAllPaniers() {
        ObservableList<Panier> paniersList = FXCollections.observableArrayList();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM panier")) {
            
            while (rs.next()) {
                // Get reservation status if available
                String statut = "En attente";
                try {
                    int idReservation = rs.getInt("idReservation");
                    String reservationStatut = getReservationStatus(idReservation);
                    if (reservationStatut != null) {
                        statut = reservationStatut;
                    }
                } catch (Exception e) {
                    System.out.println("Could not get reservation status: " + e.getMessage());
                }
                
                Panier panier = new Panier(
                    String.valueOf(rs.getInt("idPanier")),
                    String.valueOf(rs.getInt("idReservation")),
                    rs.getDate("date").toString(),
                    String.valueOf(rs.getFloat("total")),
                    statut
                );
                paniersList.add(panier);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting paniers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paniersList;
    }
    
    /**
     * Get the status of a reservation
     * 
     * @param idReservation The ID of the reservation
     * @return The status of the reservation, or null if not found
     */
    private String getReservationStatus(int idReservation) {
        String sql = "SELECT statut FROM reservation WHERE idReservation = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idReservation);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("statut");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reservation status: " + e.getMessage());
        }
        
        return null;
    }
    
    private Panier getPanierById(String id) {
        String sql = "SELECT * FROM panier WHERE idPanier = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Get reservation status if available
                    String statut = "En attente";
                    try {
                        int idReservation = rs.getInt("idReservation");
                        String reservationStatut = getReservationStatus(idReservation);
                        if (reservationStatut != null) {
                            statut = reservationStatut;
                        }
                    } catch (Exception e) {
                        System.out.println("Could not get reservation status: " + e.getMessage());
                    }
                    
                    return new Panier(
                        String.valueOf(rs.getInt("idPanier")),
                        String.valueOf(rs.getInt("idReservation")),
                        rs.getDate("date").toString(),
                        String.valueOf(rs.getFloat("total")),
                        statut
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting panier by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get the next available panier ID
     * 
     * @return The next available ID
     */
    private int getNextPanierId() {
        int nextId = 1; // Default starting ID
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(idPanier) AS maxId FROM panier")) {
            
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                nextId = maxId + 1;
            }
            
            System.out.println("Next available panier ID: " + nextId);
            
        } catch (SQLException e) {
            System.err.println("Error getting next panier ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nextId;
    }
    
    /**
     * Check if a panier ID already exists
     * 
     * @param id The ID to check
     * @return true if the ID exists, false otherwise
     */
    private boolean panierIdExists(int id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM panier WHERE idPanier = ?")) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If there's a result, the ID exists
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if panier ID exists: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false; // Assume it doesn't exist if there was an error
    }
    
    private boolean addPanier(Panier panier) {
        // First, test the database connection
        if (!testConnection()) {
            System.err.println("Database connection test failed. Cannot add panier.");
            return false;
        }
        
        // Check if the ID already exists
        int panierId;
        try {
            panierId = Integer.parseInt(panier.getIdPanier());
            if (panierId <= 0) {
                // Use auto-increment
                panierId = 0;
            } else if (panierIdExists(panierId)) {
                System.err.println("Panier ID " + panierId + " already exists.");
                
                // Ask user if they want to use the next available ID
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("ID déjà utilisé");
                confirmDialog.setHeaderText("L'ID de panier " + panierId + " est déjà utilisé");
                confirmDialog.setContentText("Voulez-vous utiliser l'ID suivant disponible (" + getNextPanierId() + ") ?");
                
                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    panierId = getNextPanierId();
                    System.out.println("Using next available ID: " + panierId);
                } else {
                    System.out.println("User cancelled operation");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid panier ID format: " + panier.getIdPanier());
            panierId = getNextPanierId();
            System.out.println("Using next available ID: " + panierId);
        }
        
        try (Connection conn = getConnection()) {
            // Disable auto-commit to use transaction
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            System.out.println("Auto-commit disabled for transaction");
            
            try {
                // Try a simpler approach with direct SQL execution
                try (Statement stmt = conn.createStatement()) {
                    String sql;
                    
                    if (panierId <= 0) {
                        // Use auto-increment
                        sql = String.format(
                            "INSERT INTO panier (idReservation, date, total) VALUES (%s, '%s', %s)",
                            panier.getIdReservation(),
                            panier.getDate(),
                            panier.getTotal()
                        );
                    } else {
                        // Use specified ID
                        sql = String.format(
                            "INSERT INTO panier (idPanier, idReservation, date, total) VALUES (%d, %s, '%s', %s)",
                            panierId,
                            panier.getIdReservation(),
                            panier.getDate(),
                            panier.getTotal()
                        );
                    }
                    
                    System.out.println("Executing SQL: " + sql);
                    
                    int rowsAffected = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                    System.out.println("Rows affected: " + rowsAffected);
                    
                    if (rowsAffected > 0) {
                        if (panierId <= 0) {
                            // Get the auto-generated ID
                            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int generatedId = generatedKeys.getInt(1);
                                    System.out.println("Generated ID: " + generatedId);
                                    idPanierField.setText(String.valueOf(generatedId));
                                }
                            }
                        }
                        
                        // Commit the transaction
                        conn.commit();
                        System.out.println("Transaction committed successfully");
                        return true;
                    }
                    
                    // Rollback if no rows affected
                    conn.rollback();
                    System.out.println("Transaction rolled back - no rows affected");
                    return false;
                }
            } catch (SQLException e) {
                // Rollback on error
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
                throw e; // Re-throw the exception
            } finally {
                // Restore original auto-commit setting
                try {
                    conn.setAutoCommit(originalAutoCommit);
                    System.out.println("Auto-commit restored to original setting");
                } catch (SQLException e) {
                    System.err.println("Error restoring auto-commit: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding panier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            
            // Show detailed error message
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", 
                     "Erreur SQL: " + e.getMessage() + "\nSQL State: " + e.getSQLState() + 
                     "\nError Code: " + e.getErrorCode());
            
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error adding panier: " + e.getMessage());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue: " + e.getMessage());
            
            return false;
        }
    }
    
    /**
     * Update an existing panier in the database
     * 
     * @param panier The panier with updated information
     * @return true if the operation was successful, false otherwise
     */
    private boolean updatePanier(Panier panier) {
        String sql = "UPDATE panier SET idReservation = ?, date = ?, total = ? " +
                     "WHERE idPanier = ?";
        
        try (Connection conn = getConnection()) {
            // Disable auto-commit to use transaction
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            System.out.println("Auto-commit disabled for transaction");
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Log the values being updated
                System.out.println("=== UPDATING PANIER IN DATABASE ===");
                System.out.println("SQL: " + sql);
                System.out.println("idPanier: " + panier.getIdPanier());
                System.out.println("idReservation: " + panier.getIdReservation());
                System.out.println("date: " + panier.getDate());
                System.out.println("total: " + panier.getTotal());
                System.out.println("statut (UI only): " + panier.getStatut());
                
                pstmt.setInt(1, Integer.parseInt(panier.getIdReservation()));
                pstmt.setDate(2, Date.valueOf(panier.getDate()));
                pstmt.setFloat(3, Float.parseFloat(panier.getTotal()));
                pstmt.setInt(4, Integer.parseInt(panier.getIdPanier()));
                
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);
                
                if (rowsAffected > 0) {
                    // Commit the transaction
                    conn.commit();
                    System.out.println("Transaction committed successfully");
                    return true;
                } else {
                    // Rollback if no rows affected
                    conn.rollback();
                    System.out.println("Transaction rolled back - no rows affected");
                    return false;
                }
            } catch (SQLException e) {
                // Rollback on error
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
                throw e; // Re-throw the exception
            } finally {
                // Restore original auto-commit setting
                try {
                    conn.setAutoCommit(originalAutoCommit);
                    System.out.println("Auto-commit restored to original setting");
                } catch (SQLException e) {
                    System.err.println("Error restoring auto-commit: " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating panier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            
            // Show detailed error message
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", 
                     "Erreur SQL: " + e.getMessage() + "\nSQL State: " + e.getSQLState() + 
                     "\nError Code: " + e.getErrorCode());
            
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error updating panier: " + e.getMessage());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue: " + e.getMessage());
            
            return false;
        }
    }
    
    /**
     * Delete a panier from the database
     * 
     * @param id The ID of the panier to delete
     * @return true if the operation was successful, false otherwise
     */
    private boolean deletePanier(String id) {
        String sql = "DELETE FROM panier WHERE idPanier = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(id));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting panier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get panier items by panier ID
     * 
     * @param idPanier The ID of the panier
     * @return ObservableList of panier items
     */
    private ObservableList<String> getPanierItems(String idPanier) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String sql = "SELECT * FROM panier_item WHERE idPanier = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(idPanier));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String itemDescription = rs.getString("description");
                    float itemPrice = rs.getFloat("prix");
                    items.add(itemDescription + ": " + String.format("%.2f", itemPrice) + " TND");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting panier items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Load paniers from database
    private void loadPaniersFromDatabase() {
        System.out.println("=== LOADING PANIERS FROM DATABASE ===");
        
        // First, test the database connection
        if (!testConnection()) {
            System.err.println("Database connection test failed. Cannot load paniers.");
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                     "Impossible de se connecter à la base de données.");
            
            // Use sample data as fallback
            loadSampleData();
            return;
        }
        
        try {
            paniers = getAllPaniers();
            System.out.println("Loaded " + paniers.size() + " paniers from database");
            
            // Debug: print all loaded paniers
            for (Panier p : paniers) {
                System.out.println("ID: " + p.getIdPanier() + 
                                  ", Reservation: " + p.getIdReservation() + 
                                  ", Date: " + p.getDate() + 
                                  ", Total: " + p.getTotal() + 
                                  ", Statut: " + p.getStatut());
            }
            
            paniersTable.setItems(paniers);
            
            if (paniers.isEmpty()) {
                System.out.println("No paniers found in database. You can add new ones.");
            }
        } catch (Exception e) {
            System.err.println("Error loading paniers: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", 
                     "Impossible de charger les paniers: " + e.getMessage());
            
            // If database loading fails, use sample data for demonstration
            loadSampleData();
        }
    }
    
    // Load sample data for demonstration
    private void loadSampleData() {
        System.out.println("Using sample data instead");
        paniers = FXCollections.observableArrayList(
            new Panier("1", "1001", "2024-05-01", "250.00", "Confirmé"),
            new Panier("2", "1002", "2024-05-02", "175.50", "En attente"),
            new Panier("3", "1003", "2024-05-03", "320.75", "Confirmé")
        );
        paniersTable.setItems(paniers);
    }
    
    private void addButtonsToTable() {
        Callback<TableColumn<Panier, Void>, TableCell<Panier, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Panier, Void> call(final TableColumn<Panier, Void> param) {
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
                            Panier panier = getTableView().getItems().get(getIndex());
                            viewPanier(panier);
                        });
                        
                        editBtn.setOnAction(event -> {
                            Panier panier = getTableView().getItems().get(getIndex());
                            editPanier(panier);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Panier panier = getTableView().getItems().get(getIndex());
                            deletePanier(panier);
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

    /**
     * Class to represent a Reservation
     */
    private class Reservation {
        private String idReservation;
        private String dateReservation;
        private String montantTotal;
        private String statut;
        private String idVol;
        private String idHebergement;
        
        public Reservation(String idReservation, String dateReservation, String montantTotal, 
                          String statut, String idVol, String idHebergement) {
            this.idReservation = idReservation;
            this.dateReservation = dateReservation;
            this.montantTotal = montantTotal;
            this.statut = statut;
            this.idVol = idVol;
            this.idHebergement = idHebergement;
        }
        
        public String getIdReservation() { return idReservation; }
        public String getDateReservation() { return dateReservation; }
        public String getMontantTotal() { return montantTotal; }
        public String getStatut() { return statut; }
        public String getIdVol() { return idVol; }
        public String getIdHebergement() { return idHebergement; }
    }
    
    /**
     * Get all reservations from the database
     * 
     * @return List of all reservations
     */
    private List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if reservation table exists
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "reservation", null);
            
            if (tables.next()) {
                // Table exists, fetch reservations
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM reservation")) {
                    while (rs.next()) {
                        Reservation reservation = new Reservation(
                            String.valueOf(rs.getInt("idReservation")),
                            rs.getDate("dateReservation").toString(),
                            String.valueOf(rs.getFloat("montantTotal")),
                            rs.getString("statut"),
                            String.valueOf(rs.getInt("idVol")),
                            String.valueOf(rs.getInt("idHebergement"))
                        );
                        reservations.add(reservation);
                    }
                }
            } else {
                // Table doesn't exist, show error
                System.err.println("Reservation table does not exist");
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "La table 'reservation' n'existe pas dans la base de données.");
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservations: " + e.getMessage());
            e.printStackTrace();
            
            // Try with different column names if the first attempt fails
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM reservation")) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                // Print column names for debugging
                System.out.println("Reservation table columns:");
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println(i + ": " + metaData.getColumnName(i));
                }
                
                // Try to adapt to the actual column names
                while (rs.next()) {
                    try {
                        String idReservation = String.valueOf(rs.getInt(1)); // Assume first column is ID
                        String dateReservation = rs.getDate(2) != null ? rs.getDate(2).toString() : LocalDate.now().toString();
                        String montantTotal = "0.0";
                        String statut = "En attente";
                        String idVol = "0";
                        String idHebergement = "0";
                        
                        // Try to get other columns if they exist
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            if (columnName.contains("montant") || columnName.contains("total") || columnName.contains("prix")) {
                                montantTotal = String.valueOf(rs.getFloat(i));
                            } else if (columnName.contains("statut") || columnName.contains("etat")) {
                                statut = rs.getString(i);
                            } else if (columnName.contains("vol")) {
                                idVol = String.valueOf(rs.getInt(i));
                            } else if (columnName.contains("hebergement")) {
                                idHebergement = String.valueOf(rs.getInt(i));
                            }
                        }
                        
                        Reservation reservation = new Reservation(
                            idReservation, dateReservation, montantTotal, 
                            statut, idVol, idHebergement
                        );
                        reservations.add(reservation);
                    } catch (Exception ex) {
                        System.err.println("Error processing reservation row: " + ex.getMessage());
                    }
                }
            } catch (SQLException ex) {
                System.err.println("Error in fallback reservation query: " + ex.getMessage());
            }
        }
        
        return reservations;
    }
    
    /**
     * Add all reservations to the panier list
     */
    @FXML
    void handleAddAllReservations(ActionEvent event) {
        List<Reservation> reservations = getAllReservations();
        
        if (reservations.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", 
                     "Aucune réservation trouvée dans la base de données.");
            return;
        }
        
        int successCount = 0;
        int failCount = 0;
        
        for (Reservation reservation : reservations) {
            // Check if a panier already exists for this reservation
            boolean exists = false;
            for (Panier p : paniers) {
                if (p.getIdReservation().equals(reservation.getIdReservation())) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                // Create a new panier from the reservation
                Panier newPanier = new Panier(
                    String.valueOf(getNextPanierId()),
                    reservation.getIdReservation(),
                    LocalDate.now().toString(),
                    reservation.getMontantTotal()
                );
                
                // Add the panier to the database
                if (addPanier(newPanier)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
        }
        
        // Reload data from database
        loadPaniersFromDatabase();
        
        // Show result message
        if (successCount > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     successCount + " réservation(s) ajoutée(s) avec succès.\n" +
                     failCount + " réservation(s) non ajoutée(s).");
        } else if (failCount > 0) {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                     "Aucune réservation ajoutée. " + failCount + " échec(s).");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", 
                     "Aucune nouvelle réservation à ajouter.");
        }
    }
    
    /**
     * Create a new panier with a unique ID
     */
    private void createNewPanier() {
        clearForm();
        
        // Ask user if they want to use auto-increment or specify an ID
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Nouveau panier");
        confirmDialog.setHeaderText("Création d'un nouveau panier");
        confirmDialog.setContentText("Voulez-vous utiliser l'auto-incrémentation pour l'ID du panier ?");
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Use auto-increment (leave ID field empty)
            idPanierField.setText("0");
            System.out.println("Using auto-increment for new panier");
        } else {
            // Set a new unique ID
            int nextId = getNextPanierId();
            idPanierField.setText(String.valueOf(nextId));
            System.out.println("Using manual ID: " + nextId);
        }
        
        // Set default values
        createdAtPicker.setValue(LocalDate.now());
        statutComboBox.setValue("En attente");
        
        // Enable all fields for new entry
        setFieldsEditable(true);
    }
    
    @FXML
    void handleAnnuler(ActionEvent event) {
        clearForm();
    }
    
    @FXML
    void handleNouveau(ActionEvent event) {
        createNewPanier();
    }
    
    @FXML
    void handleTestDatabase(ActionEvent event) {
        testDatabaseConnection();
    }
    
    /**
     * Open the Reservation management window
     */
    @FXML
    void handleOpenReservations(ActionEvent event) {
        System.out.println("=== OPENING RESERVATION WINDOW (HANDLER) ===");
        openReservationsDirectly();
    }
    
    private void testDatabaseConnection() {
        try {
            // Test connection
            System.out.println("Testing database connection...");
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful!");
                
                // Check for foreign key constraints and drop them
                try (Statement stmt = conn.createStatement()) {
                    System.out.println("Checking for foreign key constraints...");
                    
                    // Get information about foreign keys
                    try (ResultSet rs = conn.getMetaData().getImportedKeys(null, null, "panier")) {
                        while (rs.next()) {
                            String fkName = rs.getString("FK_NAME");
                            String pkTable = rs.getString("PKTABLE_NAME");
                            String fkTable = rs.getString("FKTABLE_NAME");
                            
                            System.out.println("Found foreign key: " + fkName + 
                                              " from " + fkTable + " to " + pkTable);
                            
                            // Try to drop the constraint
                            try {
                                String dropSQL = "ALTER TABLE " + fkTable + 
                                               " DROP FOREIGN KEY " + fkName;
                                stmt.executeUpdate(dropSQL);
                                System.out.println("Dropped foreign key constraint: " + fkName);
                            } catch (SQLException e) {
                                System.out.println("Could not drop foreign key: " + e.getMessage());
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting foreign keys: " + e.getMessage());
                    }
                    
                    // Drop and recreate tables
                    System.out.println("Dropping and recreating tables...");
                    
                    // Drop tables if they exist
                    stmt.executeUpdate("DROP TABLE IF EXISTS panier_item");
                    stmt.executeUpdate("DROP TABLE IF EXISTS panier");
                    System.out.println("Dropped existing tables");
                    
                    // Create tables
                    String createPanierSQL = "CREATE TABLE panier (" +
                        "idPanier INT PRIMARY KEY AUTO_INCREMENT, " +
                        "idReservation INT, " +
                        "date DATE, " +
                        "total FLOAT" +
                        ")";
                    
                    stmt.executeUpdate(createPanierSQL);
                    System.out.println("Created panier table");
                    
                    String createItemSQL = "CREATE TABLE panier_item (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "idPanier INT, " +
                        "description VARCHAR(255), " +
                        "prix FLOAT" +
                        ")";
                    
                    stmt.executeUpdate(createItemSQL);
                    System.out.println("Created panier_item table");
                    
                    // Try to insert a test record
                    String insertSQL = "INSERT INTO panier (idPanier, idReservation, date, total) " +
                                      "VALUES (9999, 9999, '2024-05-15', 99.99)";
                    
                    int rowsAffected = stmt.executeUpdate(insertSQL);
                    System.out.println("Test insert successful! Rows affected: " + rowsAffected);
                    
                    // Now delete the test record
                    stmt.executeUpdate("DELETE FROM panier WHERE idPanier = 9999");
                    System.out.println("Test record deleted");
                    
                    showAlert(Alert.AlertType.INFORMATION, "Test réussi", 
                             "Tables recréées et test d'insertion réussi!");
                }
                
                conn.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                         "Impossible de se connecter à la base de données.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception in testDatabaseConnection: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", 
                     "Erreur SQL: " + e.getMessage() + "\nSQL State: " + e.getSQLState() + 
                     "\nError Code: " + e.getErrorCode());
        } catch (Exception e) {
            System.err.println("Exception in testDatabaseConnection: " + e.getMessage());
            e.printStackTrace();
            
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        idPanierField.clear();
        idReservationField.clear();
        idVolField.clear();
        idHebergementField.clear();
        createdAtPicker.setValue(LocalDate.now());
        totalField.clear();
        statutComboBox.setValue(null);
        
        // Enable all fields for new entry
        setFieldsEditable(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleEnregistrer(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }
        
        // First, test the database connection
        if (!testConnection()) {
            System.err.println("Database connection test failed. Cannot save panier.");
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                     "Impossible de se connecter à la base de données. Opération annulée.");
            return;
        }
        
        try {
            System.out.println("=== SAVING PANIER ===");
            System.out.println("ID: " + idPanierField.getText());
            System.out.println("Reservation ID: " + idReservationField.getText());
            System.out.println("Date: " + createdAtPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
            System.out.println("Total: " + totalField.getText());
            System.out.println("Statut: " + statutComboBox.getValue());
            
            // Create new panier object
            Panier newPanier = new Panier(
                idPanierField.getText(),
                idReservationField.getText(),
                createdAtPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE),
                totalField.getText(),
                statutComboBox.getValue()
            );
            
            boolean success = false;
            boolean isUpdate = false;
            
            // Check if we're updating an existing panier
            Optional<Panier> existingPanier = paniers.stream()
                    .filter(p -> p.getIdPanier().equals(idPanierField.getText()))
                    .findFirst();
            
            if (existingPanier.isPresent()) {
                System.out.println("Updating existing panier");
                // Update existing panier in database
                success = updatePanier(newPanier);
                isUpdate = true;
                
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de mise à jour", 
                             "Impossible de mettre à jour le panier dans la base de données.");
                    return;
                }
            } else {
                System.out.println("Adding new panier");
                // Add new panier to database
                success = addPanier(newPanier);
                
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Erreur d'ajout", 
                             "Impossible d'ajouter le panier à la base de données. Vérifiez que l'ID n'est pas déjà utilisé.");
                    return;
                }
            }
            
            System.out.println("Operation " + (success ? "successful" : "failed"));
            
            if (success) {
                // Reload data from database
                loadPaniersFromDatabase();
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                          "Panier " + (isUpdate ? "mis à jour" : "ajouté") + " avec succès.");
                
                // Clear form
                clearForm();
            }
        } catch (Exception e) {
            System.err.println("Exception in handleEnregistrer: " + e.getMessage());
            e.printStackTrace();
            
            String errorMessage = "Une erreur est survenue: " + e.getMessage();
            
            // Provide more specific error messages based on exception type
            if (e instanceof NumberFormatException) {
                errorMessage = "Format de nombre invalide. Vérifiez les valeurs numériques.";
            } else if (e instanceof IllegalArgumentException) {
                errorMessage = "Format de date invalide. Utilisez le format YYYY-MM-DD.";
            } else if (e instanceof SQLException) {
                SQLException sqlEx = (SQLException) e;
                if (sqlEx.getErrorCode() == 1062) {
                    errorMessage = "ID de panier déjà utilisé. Veuillez en choisir un autre.";
                } else if (sqlEx.getErrorCode() == 1452) {
                    errorMessage = "ID de réservation invalide. Cette réservation n'existe pas.";
                }
            }
            
            showAlert(Alert.AlertType.ERROR, "Erreur", errorMessage);
            
            // Fallback to in-memory operation if database operation fails
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Mode hors ligne");
            confirmDialog.setHeaderText("Opération en mode hors ligne");
            confirmDialog.setContentText("Voulez-vous continuer en mode hors ligne ? Les modifications ne seront pas enregistrées dans la base de données.");
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Check if we're updating an existing panier
                    Optional<Panier> existingPanier = paniers.stream()
                            .filter(p -> p.getIdPanier().equals(idPanierField.getText()))
                            .findFirst();
                    
                    if (existingPanier.isPresent()) {
                        // Remove existing panier
                        paniers.remove(existingPanier.get());
                    }
                    
                    // Create new panier
                    Panier newPanier = new Panier(
                        idPanierField.getText(),
                        idReservationField.getText(),
                        createdAtPicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        totalField.getText(),
                        statutComboBox.getValue()
                    );
                    
                    // Add to table
                    paniers.add(newPanier);
                    
                    // Update summary
                    updateSummary();
                    
                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Succès (Mode hors ligne)", 
                             "Panier " + (existingPanier.isPresent() ? "mis à jour" : "ajouté") + " avec succès en mode hors ligne.");
                    
                    // Clear form
                    clearForm();
                }
            });
        }
    }
    
    private void viewPanier(Panier panier) {
        loadPanierDetails(panier);
        
        // Make fields read-only for view mode
        setFieldsEditable(false);
        
        // Show info message
        showAlert(Alert.AlertType.INFORMATION, "Information", "Affichage du panier ID: " + panier.getIdPanier());
    }
    
    private void editPanier(Panier panier) {
        loadPanierDetails(panier);
        
        // Make fields editable for edit mode
        setFieldsEditable(true);
    }
    
    private void loadPanierDetails(Panier panier) {
        // Load panier data into form
        idPanierField.setText(panier.getIdPanier());
        idReservationField.setText(panier.getIdReservation());
        createdAtPicker.setValue(LocalDate.parse(panier.getDate()));
        totalField.setText(panier.getTotal());
        statutComboBox.setValue(panier.getStatut());
        
        try {
            // Try to load panier items from database
            ObservableList<String> items = getPanierItems(panier.getIdPanier());
            
            if (items.isEmpty()) {
                // If no items found in database, use sample data
                System.out.println("No items found in database for panier ID: " + panier.getIdPanier() + ", using sample data");
                
                // Set sample vol and hebergement IDs based on panier ID
                switch (panier.getIdPanier()) {
                    case "1":
                        idVolField.setText("101");
                        idHebergementField.setText("201");
                        loadPanierItems(panier1Items);
                        break;
                    case "2":
                        idVolField.setText("102");
                        idHebergementField.setText("202");
                        loadPanierItems(panier2Items);
                        break;
                    case "3":
                        idVolField.setText("103");
                        idHebergementField.setText("203");
                        loadPanierItems(panier3Items);
                        break;
                    default:
                        idVolField.setText("");
                        idHebergementField.setText("");
                        loadPanierItems(FXCollections.observableArrayList());
                        break;
                }
            } else {
                // Use items from database
                System.out.println("Loaded " + items.size() + " items for panier ID: " + panier.getIdPanier());
                loadPanierItems(items);
                
                // Set default vol and hebergement IDs
                idVolField.setText("100");
                idHebergementField.setText("200");
            }
        } catch (Exception e) {
            System.err.println("Error loading panier items: " + e.getMessage());
            e.printStackTrace();
            
            // Use sample data as fallback
            switch (panier.getIdPanier()) {
                case "1":
                    idVolField.setText("101");
                    idHebergementField.setText("201");
                    loadPanierItems(panier1Items);
                    break;
                case "2":
                    idVolField.setText("102");
                    idHebergementField.setText("202");
                    loadPanierItems(panier2Items);
                    break;
                case "3":
                    idVolField.setText("103");
                    idHebergementField.setText("203");
                    loadPanierItems(panier3Items);
                    break;
                default:
                    idVolField.setText("");
                    idHebergementField.setText("");
                    loadPanierItems(FXCollections.observableArrayList());
                    break;
            }
        }
        
        // Update summary
        updateSummary();
    }
    
    private void loadPanierItems(ObservableList<String> items) {
        panierItems.clear();
        panierItems.addAll(items);
        panierItemsListView.setItems(panierItems);
    }
    
    private void deletePanier(Panier panier) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer le panier");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce panier ?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = deletePanier(panier.getIdPanier());
                    
                    if (success) {
                        // Reload data from database
                        loadPaniersFromDatabase();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Panier supprimé avec succès.");
                        clearForm();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le panier.");
                    }
                } catch (Exception e) {
                    System.err.println("Error deleting panier: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Fallback to in-memory operation
                    paniers.remove(panier);
                    showAlert(Alert.AlertType.INFORMATION, "Succès (Mode hors ligne)", 
                             "Panier supprimé avec succès en mode hors ligne.");
                    clearForm();
                }
            }
        });
    }
    
    private void updateSummary() {
        if (subtotalLabel != null && taxesLabel != null && totalLabel != null) {
            double total = 0.0;
            
            try {
                if (!totalField.getText().isEmpty()) {
                    total = Double.parseDouble(totalField.getText());
                }
            } catch (NumberFormatException e) {
                total = 0.0;
            }
            
            double subtotal = total / 1.2; // Assuming 20% tax
            double taxes = total - subtotal;
            
            subtotalLabel.setText(String.format("%.2f TND", subtotal));
            taxesLabel.setText(String.format("%.1f TND", taxes));
            totalLabel.setText(String.format("%.2f TND", total));
        }
    }
    
    private void handlePayment() {
        if (totalField.getText().isEmpty() || Double.parseDouble(totalField.getText()) <= 0) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Aucun montant à payer.");
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de paiement");
        confirmDialog.setHeaderText("Procéder au paiement");
        confirmDialog.setContentText("Voulez-vous procéder au paiement de " + totalField.getText() + " TND ?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert(Alert.AlertType.INFORMATION, "Paiement effectué", 
                         "Paiement de " + totalField.getText() + " TND effectué avec succès.");
                
                // Update status to "Confirmé"
                statutComboBox.setValue("Confirmé");
                
                // Save the panier with the new status
                handleEnregistrer(null);
            }
        });
    }
    
    private void setFieldsEditable(boolean editable) {
        idPanierField.setEditable(editable);
        idReservationField.setEditable(editable);
        idVolField.setEditable(editable);
        idHebergementField.setEditable(editable);
        createdAtPicker.setDisable(!editable);
        totalField.setEditable(editable);
        statutComboBox.setDisable(!editable);
    }
    
    private boolean validateInputs() {
        if (idPanierField.getText().isEmpty() || 
            idReservationField.getText().isEmpty() || 
            totalField.getText().isEmpty() || 
            statutComboBox.getValue() == null) {
            
            showAlert(Alert.AlertType.WARNING, "Champ manquant", 
                     "Veuillez remplir tous les champs obligatoires.");
            return false;
        }
        
        // Validate ID format (assuming numeric IDs)
        try {
            Integer.parseInt(idPanierField.getText());
            Integer.parseInt(idReservationField.getText());
            
            if (!idVolField.getText().isEmpty()) {
                Integer.parseInt(idVolField.getText());
            }
            
            if (!idHebergementField.getText().isEmpty()) {
                Integer.parseInt(idHebergementField.getText());
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "Les identifiants doivent être des nombres entiers.");
            return false;
        }
        
        // Validate total amount
        try {
            double amount = Double.parseDouble(totalField.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                         "Le montant total doit être supérieur à zéro.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur de saisie", 
                     "Le montant total doit être un nombre valide.");
            return false;
        }
        
        return true;
    }
    
    // Model class for Panier
    public static class Panier {
        private final SimpleStringProperty idPanier;
        private final SimpleStringProperty idReservation;
        private final SimpleStringProperty date;
        private final SimpleStringProperty total;
        private final SimpleStringProperty statut; // Keep for UI display, even if not in DB
        
        public Panier(String idPanier, String idReservation, String date, String total, String statut) {
            this.idPanier = new SimpleStringProperty(idPanier);
            this.idReservation = new SimpleStringProperty(idReservation);
            this.date = new SimpleStringProperty(date);
            this.total = new SimpleStringProperty(total);
            this.statut = new SimpleStringProperty(statut != null ? statut : "En attente");
        }
        
        // Constructor without statut for database operations
        public Panier(String idPanier, String idReservation, String date, String total) {
            this(idPanier, idReservation, date, total, "En attente");
        }
        
        public String getIdPanier() { return idPanier.get(); }
        public String getIdReservation() { return idReservation.get(); }
        public String getDate() { return date.get(); }
        public String getTotal() { return total.get(); }
        public String getStatut() { return statut.get(); }
    }
}
