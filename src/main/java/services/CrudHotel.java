package services;

import entities.Hotel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudHotel implements HotelBooking<Hotel> {

    private Connection connection;

    public CrudHotel() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/travelagency", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Hotel hotel) throws SQLException {
        String query = "INSERT INTO hotel (nom, Destination, Date, nombreNuité, nombreChambre, nbEtoile, tarif) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Use try-with-resources to ensure that the PreparedStatement is closed properly
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getDestination());
            ps.setDate(3, new java.sql.Date(hotel.getDate().getTime()));  // Ensure the correct Date conversion
            ps.setInt(4, hotel.getNombreNuité());
            ps.setInt(5, hotel.getNombreChambre());
            ps.setInt(6, hotel.getNbEtoile());
            ps.setFloat(7, hotel.getTarif());

            ps.executeUpdate();
            System.out.println("✅ Hotel added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Rethrow exception to be handled by the calling code
        }
    }

    @Override
    public void modify(Hotel hotel) throws SQLException {
        String query = "UPDATE hotel SET nom = ?, Destination = ?, Date = ?, nombreNuité = ?, nombreChambre = ?, nbEtoile = ?, tarif = ? WHERE idHotel = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, hotel.getNom());
            ps.setString(2, hotel.getDestination());
            ps.setDate(3, new java.sql.Date(hotel.getDate().getTime()));  // Ensure the correct Date conversion
            ps.setInt(4, hotel.getNombreNuité());
            ps.setInt(5, hotel.getNombreChambre());
            ps.setInt(6, hotel.getNbEtoile());
            ps.setFloat(7, hotel.getTarif());
            ps.setInt(8, hotel.getIdHotel());  // Assuming the Hotel class has a getIdHotel() method

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Hotel updated successfully.");
            } else {
                System.out.println("⚠️ No hotel found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Rethrow exception to be handled by the calling code
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM hotel WHERE idHotel = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Hotel deleted successfully.");
            } else {
                System.out.println("⚠️ No hotel found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Rethrow exception to be handled by the calling code
        }
    }

    @Override
    public List<Hotel> afficher() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();

        String query = "SELECT * FROM hotel";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                int idHotel = rs.getInt("idHotel");
                String nom = rs.getString("nom");
                String destination = rs.getString("Destination");
                Date date = rs.getDate("Date");
                int nombreNuite = rs.getInt("nombreNuité");
                int nombreChambre = rs.getInt("nombreChambre");
                int nbEtoile = rs.getInt("nbEtoile");
                float tarif = rs.getFloat("tarif");

                Hotel hotel = new Hotel(idHotel, nom, destination, date, nombreNuite, nombreChambre, nbEtoile, tarif);
                hotels.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;  // Rethrow exception to be handled by the calling code
        }

        return hotels;
    }
}
