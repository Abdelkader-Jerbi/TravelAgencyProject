package services;

import controllers.PanierController.Panier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Service class for Panier CRUD operations
 * This class handles database operations directly without a separate DAO layer
 */
public class CrudPanier {
    
    private static final String URL = "jdbc:mysql://localhost:3306/pi";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    /**
     * Constructor
     */
    public CrudPanier() {
        // Empty constructor
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
    public boolean testConnection() {
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
    public ObservableList<Panier> getAllPaniers() {
        ObservableList<Panier> paniers = FXCollections.observableArrayList();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM panier")) {
            
            while (rs.next()) {
                Panier panier = new Panier(
                    String.valueOf(rs.getInt("idPanier")),
                    String.valueOf(rs.getInt("idReservation")),
                    rs.getDate("date").toString(),
                    String.valueOf(rs.getFloat("total")),
                    rs.getString("statut")
                );
                paniers.add(panier);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting paniers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paniers;
    }
    
    /**
     * Get a panier by its ID
     * 
     * @param id The ID of the panier to retrieve
     * @return The panier with the specified ID, or null if not found
     */
    public Panier getPanierById(String id) {
        String sql = "SELECT * FROM panier WHERE idPanier = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Panier(
                        String.valueOf(rs.getInt("idPanier")),
                        String.valueOf(rs.getInt("idReservation")),
                        rs.getDate("date").toString(),
                        String.valueOf(rs.getFloat("total")),
                        rs.getString("statut")
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
     * Add a new panier to the database
     * 
     * @param panier The panier to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addPanier(Panier panier) {
        String sql = "INSERT INTO panier (idPanier, idReservation, date, total, statut) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Log the values being inserted
            System.out.println("=== ADDING PANIER TO DATABASE ===");
            System.out.println("SQL: " + sql);
            System.out.println("idPanier: " + panier.getIdPanier());
            System.out.println("idReservation: " + panier.getIdReservation());
            System.out.println("date: " + panier.getDate());
            System.out.println("total: " + panier.getTotal());
            System.out.println("statut: " + panier.getStatut());
            
            pstmt.setInt(1, Integer.parseInt(panier.getIdPanier()));
            pstmt.setInt(2, Integer.parseInt(panier.getIdReservation()));
            pstmt.setDate(3, Date.valueOf(panier.getDate()));
            pstmt.setFloat(4, Float.parseFloat(panier.getTotal()));
            pstmt.setString(5, panier.getStatut());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding panier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error adding panier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing panier in the database
     * 
     * @param panier The panier with updated information
     * @return true if the operation was successful, false otherwise
     */
    public boolean updatePanier(Panier panier) {
        String sql = "UPDATE panier SET idReservation = ?, date = ?, total = ?, statut = ? WHERE idPanier = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Log the values being updated
            System.out.println("=== UPDATING PANIER IN DATABASE ===");
            System.out.println("SQL: " + sql);
            System.out.println("idPanier: " + panier.getIdPanier());
            System.out.println("idReservation: " + panier.getIdReservation());
            System.out.println("date: " + panier.getDate());
            System.out.println("total: " + panier.getTotal());
            System.out.println("statut: " + panier.getStatut());
            
            pstmt.setInt(1, Integer.parseInt(panier.getIdReservation()));
            pstmt.setDate(2, Date.valueOf(panier.getDate()));
            pstmt.setFloat(3, Float.parseFloat(panier.getTotal()));
            pstmt.setString(4, panier.getStatut());
            pstmt.setInt(5, Integer.parseInt(panier.getIdPanier()));
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating panier: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error updating panier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a panier from the database
     * 
     * @param id The ID of the panier to delete
     * @return true if the operation was successful, false otherwise
     */
    public boolean deletePanier(String id) {
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
     * Search for paniers by various criteria
     * 
     * @param searchTerm The term to search for
     * @return ObservableList of paniers matching the search criteria
     */
    public ObservableList<Panier> searchPaniers(String searchTerm) {
        // Get all paniers
        ObservableList<Panier> allPaniers = getAllPaniers();
        
        // Filter paniers based on search term
        return allPaniers.filtered(panier -> 
            panier.getIdPanier().contains(searchTerm) ||
            panier.getIdReservation().contains(searchTerm) ||
            panier.getDate().contains(searchTerm) ||
            panier.getTotal().contains(searchTerm) ||
            panier.getStatut().toLowerCase().contains(searchTerm.toLowerCase())
        );
    }
    
    /**
     * Filter paniers by status
     * 
     * @param status The status to filter by (e.g., "Confirmé", "En attente", "Annulé")
     * @return ObservableList of paniers with the specified status
     */
    public ObservableList<Panier> filterByStatus(String status) {
        // Get all paniers
        ObservableList<Panier> allPaniers = getAllPaniers();
        
        // Filter paniers based on status
        return allPaniers.filtered(panier -> 
            panier.getStatut().equals(status)
        );
    }
}
