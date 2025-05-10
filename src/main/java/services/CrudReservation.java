package services;

import controllers.ReservationController.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DatabaseConnectionPool;

import java.sql.*;

/**
 * Service class for Reservation CRUD operations
 * This class provides direct database access for reservation operations
 */
public class CrudReservation {
    
    /**
     * Test the database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all reservations from the database
     * 
     * @return ObservableList of all reservations
     */
    public ObservableList<Reservation> getAllReservations() {
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reservation")) {
            
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    String.valueOf(rs.getInt("idReservation")),
                    rs.getDate("dateReservation").toString(),
                    String.valueOf(rs.getInt("idHotel")),
                    String.valueOf(rs.getInt("idVols")),
                    String.valueOf(rs.getInt("idVoiture")),
                    rs.getString("statut"),
                    String.valueOf(rs.getFloat("prix"))
                );
                reservations.add(reservation);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reservations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    /**
     * Get a reservation by its ID
     * 
     * @param id The ID of the reservation to retrieve
     * @return The reservation with the specified ID, or null if not found
     */
    public Reservation getReservationById(String id) {
        String sql = "SELECT * FROM reservation WHERE idReservation = ?";
        
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(id));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                        String.valueOf(rs.getInt("idReservation")),
                        rs.getDate("dateReservation").toString(),
                        String.valueOf(rs.getInt("idHotel")),
                        String.valueOf(rs.getInt("idVols")),
                        String.valueOf(rs.getInt("idVoiture")),
                        rs.getString("statut"),
                        String.valueOf(rs.getFloat("prix"))
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reservation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Add a new reservation to the database
     * 
     * @param reservation The reservation to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (idReservation, dateReservation, idHotel, idVols, idVoiture, statut, prix) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Log the values being inserted
            System.out.println("=== ADDING RESERVATION TO DATABASE ===");
            System.out.println("SQL: " + sql);
            System.out.println("idReservation: " + reservation.getIdReservation());
            System.out.println("dateReservation: " + reservation.getDate());
            System.out.println("idHotel: " + reservation.getIdHotel());
            System.out.println("idVols: " + reservation.getIdVols());
            System.out.println("idVoiture: " + reservation.getIdVoiture());
            System.out.println("statut: " + reservation.getStatut());
            System.out.println("prix: " + reservation.getPrix());
            
            pstmt.setInt(1, Integer.parseInt(reservation.getIdReservation()));
            pstmt.setDate(2, Date.valueOf(reservation.getDate()));
            pstmt.setInt(3, Integer.parseInt(reservation.getIdHotel()));
            pstmt.setInt(4, Integer.parseInt(reservation.getIdVols()));
            pstmt.setInt(5, Integer.parseInt(reservation.getIdVoiture()));
            pstmt.setString(6, reservation.getStatut());
            pstmt.setFloat(7, Float.parseFloat(reservation.getPrix()));
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error adding reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing reservation in the database
     * 
     * @param reservation The reservation with updated information
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateReservation(Reservation reservation) {
        String sql = "UPDATE reservation SET dateReservation = ?, idHotel = ?, idVols = ?, " +
                     "idVoiture = ?, statut = ?, prix = ? WHERE idReservation = ?";
        
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Log the values being updated
            System.out.println("=== UPDATING RESERVATION IN DATABASE ===");
            System.out.println("SQL: " + sql);
            System.out.println("idReservation: " + reservation.getIdReservation());
            System.out.println("dateReservation: " + reservation.getDate());
            System.out.println("idHotel: " + reservation.getIdHotel());
            System.out.println("idVols: " + reservation.getIdVols());
            System.out.println("idVoiture: " + reservation.getIdVoiture());
            System.out.println("statut: " + reservation.getStatut());
            System.out.println("prix: " + reservation.getPrix());
            
            pstmt.setDate(1, Date.valueOf(reservation.getDate()));
            pstmt.setInt(2, Integer.parseInt(reservation.getIdHotel()));
            pstmt.setInt(3, Integer.parseInt(reservation.getIdVols()));
            pstmt.setInt(4, Integer.parseInt(reservation.getIdVoiture()));
            pstmt.setString(5, reservation.getStatut());
            pstmt.setFloat(6, Float.parseFloat(reservation.getPrix()));
            pstmt.setInt(7, Integer.parseInt(reservation.getIdReservation()));
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error updating reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a reservation from the database
     * 
     * @param id The ID of the reservation to delete
     * @return true if the operation was successful, false otherwise
     */
    public boolean deleteReservation(String id) {
        String sql = "DELETE FROM reservation WHERE idReservation = ?";
        
        try (Connection conn = DatabaseConnectionPool.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(id));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Search for reservations by various criteria
     * 
     * @param searchTerm The term to search for
     * @return ObservableList of reservations matching the search criteria
     */
    public ObservableList<Reservation> searchReservations(String searchTerm) {
        // Get all reservations
        ObservableList<Reservation> allReservations = getAllReservations();
        
        // Filter reservations based on search term
        return allReservations.filtered(reservation -> 
            reservation.getIdReservation().contains(searchTerm) ||
            reservation.getDate().contains(searchTerm) ||
            reservation.getIdHotel().contains(searchTerm) ||
            reservation.getIdVols().contains(searchTerm) ||
            reservation.getIdVoiture().contains(searchTerm) ||
            reservation.getStatut().toLowerCase().contains(searchTerm.toLowerCase())
        );
    }
    
    /**
     * Filter reservations by status
     * 
     * @param status The status to filter by (e.g., "Confirmé", "En attente", "Annulé")
     * @return ObservableList of reservations with the specified status
     */
    public ObservableList<Reservation> filterByStatus(String status) {
        // Get all reservations
        ObservableList<Reservation> allReservations = getAllReservations();
        
        // Filter reservations based on status
        return allReservations.filtered(reservation -> 
            reservation.getStatut().equals(status)
        );
    }
}
