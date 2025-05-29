package services;

import entities.Voiture;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudVoiture {

    private final Connection conn;

    public CrudVoiture() {
        this.conn = MyDatabase.getInstance().getConnection();
    }

    // ✅ Ajouter une voiture avec tous les champs
    public void ajouterVoiture(Voiture voiture) throws SQLException {
        String req = "INSERT INTO voiture (marque, modele, matricule, prixParJour, imagePath, disponible, " +
                "carburant, boite_vitesse, nb_places, nb_portes, couleur, climatisation, kilometrage, annee, categorie) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setString(1, voiture.getMarque());
            pst.setString(2, voiture.getModele());
            pst.setString(3, voiture.getMatricule());
            pst.setDouble(4, voiture.getPrixParJour());
            pst.setString(5, voiture.getImagePath() != null ? voiture.getImagePath() : "");
            pst.setBoolean(6, voiture.isDisponible());
            pst.setString(7, voiture.getCarburant());
            pst.setString(8, voiture.getBoiteVitesse());
            pst.setInt(9, voiture.getNbPlaces());
            pst.setInt(10, voiture.getNbPortes());
            pst.setString(11, voiture.getCouleur());
            pst.setBoolean(12, voiture.isClimatisation());
            pst.setInt(13, voiture.getKilometrage());
            pst.setInt(14, voiture.getAnnee());
            pst.setString(15, voiture.getCategorie());

            pst.executeUpdate();
            System.out.println("Voiture ajoutée avec succès !");
        }
    }

    // ✅ Récupérer toutes les voitures
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
                    rs.getString("imagePath"),
                    rs.getBoolean("disponible"),
                    rs.getString("carburant"),
                    rs.getString("boite_vitesse"),
                    rs.getInt("nb_places"),
                    rs.getInt("nb_portes"),
                    rs.getString("couleur"),
                    rs.getBoolean("climatisation"),
                    rs.getInt("kilometrage"),
                    rs.getInt("annee"),
                    rs.getString("categorie")
            );
            voitures.add(v);
        }
        return voitures;
    }

    // ✅ Modifier une voiture
    public void updateVoiture(Voiture voiture) throws SQLException {
        String sql = "UPDATE voiture SET marque = ?, modele = ?, matricule = ?, prixParJour = ?, imagePath = ?, disponible = ?, " +
                "carburant = ?, boite_vitesse = ?, nb_places = ?, nb_portes = ?, couleur = ?, climatisation = ?, kilometrage = ?, annee = ?, categorie = ? " +
                "WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, voiture.getMarque());
            pst.setString(2, voiture.getModele());
            pst.setString(3, voiture.getMatricule());
            pst.setDouble(4, voiture.getPrixParJour());
            pst.setString(5, voiture.getImagePath() != null ? voiture.getImagePath() : "");
            pst.setBoolean(6, voiture.isDisponible());
            pst.setString(7, voiture.getCarburant());
            pst.setString(8, voiture.getBoiteVitesse());
            pst.setInt(9, voiture.getNbPlaces());
            pst.setInt(10, voiture.getNbPortes());
            pst.setString(11, voiture.getCouleur());
            pst.setBoolean(12, voiture.isClimatisation());
            pst.setInt(13, voiture.getKilometrage());
            pst.setInt(14, voiture.getAnnee());
            pst.setString(15, voiture.getCategorie());
            pst.setInt(16, voiture.getId());

            pst.executeUpdate();
        }
    }

    // ✅ Supprimer une voiture

    public void deleteVoiture(int id) throws SQLException {
        String sql = "DELETE FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
/*
    public void deleteVoiture(int id) throws SQLException {
        String sql = "DELETE FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aucune voiture supprimée, ID non trouvé");
            }
        }
    }
*/
    // ✅ Mettre à jour la disponibilité
    public void mettreAJourDisponibilite(int id, boolean dispo) throws SQLException {
        String req = "UPDATE voiture SET disponible = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(req)) {
            pst.setBoolean(1, dispo);
            pst.setInt(2, id);
            pst.executeUpdate();
        }
    }

    // ✅ Récupérer une voiture par ID
    public Voiture getVoitureById(int id) throws SQLException {
        String sql = "SELECT * FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Voiture(
                            rs.getInt("id"),
                            rs.getString("marque"),
                            rs.getString("modele"),
                            rs.getString("matricule"),
                            rs.getDouble("prixParJour"),
                            rs.getString("imagePath"),
                            rs.getBoolean("disponible"),
                            rs.getString("carburant"),
                            rs.getString("boite_vitesse"),
                            rs.getInt("nb_places"),
                            rs.getInt("nb_portes"),
                            rs.getString("couleur"),
                            rs.getBoolean("climatisation"),
                            rs.getInt("kilometrage"),
                            rs.getInt("annee"),
                            rs.getString("categorie")
                    );
                }
            }
        }
        return null;
    }

    // ✅ Pagination
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
                            rs.getBoolean("disponible"),
                            rs.getString("carburant"),
                            rs.getString("boite_vitesse"),
                            rs.getInt("nb_places"),
                            rs.getInt("nb_portes"),
                            rs.getString("couleur"),
                            rs.getBoolean("climatisation"),
                            rs.getInt("kilometrage"),
                            rs.getInt("annee"),
                            rs.getString("categorie")
                    );
                    voitures.add(v);
                }
            }
        }
        return voitures;
    }

    // ✅ Vérifier si une voiture existe
    public boolean checkIfVoitureExistsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM voiture WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
