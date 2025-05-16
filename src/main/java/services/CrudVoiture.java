package services;

import entities.Voiture;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudVoiture {

    private final Connection conn;

    public CrudVoiture() {
        // Initialisation de la connexion avec MyDatabase
        this.conn = MyDatabase.getInstance().getConnection();
    }

    // Ajouter une voiture (inclut imagePath)
    public void ajouterVoiture(Voiture voiture) throws SQLException {
        String req = "INSERT INTO voiture (marque, modele, matricule, PrixParJour, imagePath , disponible ) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, voiture.getMarque());
            pst.setString(2, voiture.getModele());
            pst.setString(3, voiture.getMatricule());
            pst.setDouble(4, voiture.getPrixParJour());

            // Gestion du champ imagePath : on s'assure qu'il a une valeur même si vide
            String imagePath = voiture.getImagePath() != null ? voiture.getImagePath() : "";
            pst.setString(5, imagePath);
            pst.setBoolean(6, voiture.isDisponible());
            pst.executeUpdate();
            System.out.println("Voiture ajoutée avec succès !");
        }
    }
    // Récupérer toutes les voitures
    public List<Voiture> getAllVoitures() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        String req = "SELECT * FROM voiture";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) {
            Voiture v = new Voiture(
                    rs.getInt("id"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("matricule"),
                    rs.getDouble("prixParJour"),
                    rs.getString("imagePath"), // très important !
                    rs.getBoolean("disponible")
            );
            voitures.add(v);
        }
        return voitures;
    }


    // Modifier une voiture
    public void updateVoiture(Voiture voiture) throws SQLException {
        String sql = "UPDATE voiture SET marque = ?, modele = ?, matricule = ?, prixParJour = ?, imagePath = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voiture.getMarque());
            stmt.setString(2, voiture.getModele());
            stmt.setString(3, voiture.getMatricule());
            stmt.setDouble(4, voiture.getPrixParJour());
            stmt.setString(5, voiture.getImagePath() != null ? voiture.getImagePath() : "");
            stmt.setInt(6, voiture.getId());
            stmt.executeUpdate();
        }
    }

    // Mettre à jour la disponibilité de la voiture
    public void mettreAJourVoiture(Voiture voiture) {
        String req = "UPDATE voiture SET disponible = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setBoolean(1, voiture.isDisponible());
            pst.setInt(2, voiture.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la voiture avec ID " + voiture.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supprimer une voiture
    public void deleteVoiture(int id) throws SQLException {
        String sql = "DELETE FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Récupérer une voiture par ID
    public Voiture getVoitureById(int id) throws SQLException {
        String sql = "SELECT id, marque, modele, matricule, prixParJour, imagePath, disponible FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Voiture voiture = new Voiture();
                    voiture.setId(rs.getInt("id"));
                    voiture.setMarque(rs.getString("marque"));
                    voiture.setModele(rs.getString("modele"));
                    voiture.setMatricule(rs.getString("matricule"));
                    voiture.setPrixParJour(rs.getDouble("prixParJour"));
                    voiture.setImagePath(rs.getString("imagePath"));
                    voiture.setDisponible(rs.getBoolean("disponible"));
                    return voiture;
                }
            }
        }
        return null; // Retourne null si la voiture n'est pas trouvée
    }
    // Exemple dans CrudVoiture
    public List<Voiture> getVoituresPage(int page, int size) throws SQLException {
        int offset = page * size;
        List<Voiture> voitures = new ArrayList<>();

        String sql = "SELECT * FROM voiture LIMIT ? OFFSET ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, size);
            pst.setInt(2, offset);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Voiture v = new Voiture(
                            rs.getInt("id"),
                            rs.getString("marque"),
                            rs.getString("modele"),
                            rs.getString("matricule"),
                            rs.getDouble("prixParJour"),
                            rs.getString("imagePath"),
                            rs.getBoolean("disponible")
                    );
                    voitures.add(v);
                }
            }
        }

        return voitures;
    }

    // Vérifier si une voiture existe par ID
    public boolean checkIfVoitureExistsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Si le résultat existe, la voiture est présente
            }
        }
    }

}
