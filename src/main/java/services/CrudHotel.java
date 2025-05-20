package services;

import entities.Hotel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        String query = "INSERT INTO hotel (image,nom, Localisation, Date, Chambre, nbEtoile, tarif, idCategorie, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, hotel.getImage());
            ps.setString(2, hotel.getNom());
            ps.setString(3, hotel.getLocalisation());
            ps.setDate(4, new java.sql.Date(hotel.getDate().getTime()));
            ps.setString(5, hotel.getChambre());
            ps.setInt(6, hotel.getNbEtoile());
            ps.setFloat(7, hotel.getTarif());
            ps.setInt(8, getCategorieId(hotel.getCategorieType()));
            ps.setString(9, hotel.getDescription());

            ps.executeUpdate();
            System.out.println("✅ Hotel added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void modify(Hotel hotel) throws SQLException {
        String query = "UPDATE hotel SET image= ?, nom = ?, Localisation = ?, Date = ?, Chambre = ?, nbEtoile = ?, tarif = ?, idCategorie = ?, description = ? WHERE idHotel = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, hotel.getImage());
            ps.setString(2, hotel.getNom());
            ps.setString(3, hotel.getLocalisation());
            ps.setDate(4, new java.sql.Date(hotel.getDate().getTime()));
            ps.setString(5, hotel.getChambre());
            ps.setInt(6, hotel.getNbEtoile());
            ps.setFloat(7, hotel.getTarif());
            ps.setInt(8, getCategorieId(hotel.getCategorieType()));
            ps.setString(9, hotel.getDescription());
            ps.setInt(10, hotel.getIdHotel());


            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Hotel updated successfully.");
            } else {
                System.out.println("⚠️ No hotel found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void delete(int idHotel) throws SQLException {
        String query = "DELETE FROM hotel WHERE idHotel = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idHotel);

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

        String query = "SELECT h.*, c.TypeCatégorie FROM hotel h " +
                "JOIN categoriehotel c ON h.idCategorie = c.idCategorie";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                int idHotel = rs.getInt("idHotel");
                String image = rs.getString("image");
                String nom = rs.getString("nom");
                String localisation = rs.getString("Localisation");
                Date date = rs.getDate("Date");
                String chambre = rs.getString("Chambre");
                int nbEtoile = rs.getInt("nbEtoile");
                float tarif = rs.getFloat("tarif");
                String description = rs.getString("description");
                String typeCatégorie = rs.getString("TypeCatégorie");

                Hotel hotel = new Hotel(idHotel, image, nom, localisation, date, description, typeCatégorie, chambre, nbEtoile, tarif);
                hotel.setCategorieType(typeCatégorie);
                hotels.add(hotel);

                System.out.println("Image column value: " + hotel.getImage());
                System.out.println("Looking for: " + new File("View/images/" + hotel.getImage()).getAbsolutePath());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return hotels;
    }

    private int getCategorieId(String categorieType) throws SQLException {
        String query = "SELECT idCategorie FROM categoriehotel WHERE TypeCatégorie = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categorieType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idCategorie");
                } else {
                    throw new SQLException("Category type not found: " + categorieType);
                }
            }
        }
    }

    public void addReservation(int idHotel) throws SQLException {
        String query = "INSERT INTO reservation (idHotel) VALUES (?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idHotel);
            ps.executeUpdate();
            System.out.println("✅ Hotel reservation added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}

