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
        String sql = "SELECT id, marque, modele, matricule, prixParJour, imagePath , disponible FROM voiture";  // Ajout de 'imagePath' si nécessaire

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Voiture voiture = new Voiture();
                voiture.setId(rs.getInt("id"));
                voiture.setMarque(rs.getString("marque"));
                voiture.setModele(rs.getString("modele"));
                voiture.setMatricule(rs.getString("matricule"));
                voiture.setPrixParJour(rs.getDouble("prixParJour"));
                voiture.setImagePath(rs.getString("imagePath"));  // Assurez-vous que 'imagePath' existe dans votre base de données
                voiture.setDisponible(rs.getBoolean("disponible"));
                voitures.add(voiture);
            }
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

    // Supprimer une voiture
    public void deleteVoiture(int id) throws SQLException {
        String sql = "DELETE FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Vérifier si un ID existe déjà
    public boolean idExiste(int id) {
        String query = "SELECT COUNT(*) FROM voiture WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
