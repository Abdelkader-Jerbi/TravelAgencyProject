package services;

import entities.Categorie;
import entities.Enumnom;
import entities.Vol;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrudVol implements VoLInterface {

    @Override
    public List<Vol> chercherVol(String depart, String destination, String dateString, String dateRetourString, Enumnom categorie) {
        List<Vol> volsTrouves = new ArrayList<>();
        List<Vol> allVols = getAllVols(); // Appel correct

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");



        for (Vol vol : allVols) {
            String volDate = dbFormat.format(vol.getDate());
            if (vol.getDepart().equalsIgnoreCase(depart) &&
                    vol.getDestination().equalsIgnoreCase(destination) &&
                    volDate.equals(dateString) &&
                    vol.getCategorie().getNom().equals(categorie)) {
                volsTrouves.add(vol);
            }
        }
        return volsTrouves;
    }

   public List<Vol> getAllVols() {
        List<Vol> vols = new ArrayList<>();
       String sql = "SELECT v.id_vol, v.depart, v.destination, v.date, v.dateRetour, v.prix, c.id, c.nom " +
               "FROM vol v JOIN categorie c ON v.categorie_id = c.id";


       try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_vol");
                String depart = rs.getString("depart");
                String destination = rs.getString("destination");
                Date date = rs.getDate("date");
                Date dateRetour = rs.getDate("dateRetour");

                double prix = rs.getDouble("prix");

                int catId = rs.getInt("id");
                String nomCatStr = rs.getString("nom").trim().toLowerCase(); // Utiliser trim() pour enlever les espaces inutiles

                Enumnom nomCat = null;
                try {
                    nomCat = Enumnom.valueOf(nomCatStr);
                } catch (IllegalArgumentException e) {
                    // Traiter l'erreur si la chaîne ne correspond pas à une valeur de l'énumération
                    System.out.println("Valeur de l'énumération invalide : " + nomCatStr);
                    // Si nécessaire, vous pouvez assigner une valeur par défaut ou ignorer cet enregistrement
                    continue;
                }

                Categorie cat = new Categorie(catId, nomCat);
                Vol vol = new Vol(id, depart, destination, date,dateRetour, prix, cat);
                vols.add(vol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return vols;
    }

    @Override
    public void supprimerVol(int id) {
        String sql = "DELETE FROM vol WHERE id_vol = ?";
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("Suppression du vol avec ID : " + id);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // ou logger proprement selon ton projet
        }
    }

    @Override
    public void modifierVol(Vol vol) {
        String sql = "UPDATE vol SET depart = ?, destination = ?, date = ?, dateRetour = ?, prix = ?, categorie_id = ? WHERE id_vol = ?";
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vol.getDepart());
            stmt.setString(2, vol.getDestination());
            stmt.setDate(3, new java.sql.Date(vol.getDate().getTime()));
            stmt.setDate(4, vol.getDateRetour() != null ? new java.sql.Date(vol.getDateRetour().getTime()) : null);
            stmt.setDouble(5, vol.getPrix());
            stmt.setInt(6, vol.getCategorie().getId()); // selon ta structure
            stmt.setInt(7, vol.getId_vol());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Categorie getCategorieByNom(String nom) throws SQLException {
        String sql = "SELECT * FROM categorie WHERE nom = ?";
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Enumnom categorieNom = Enumnom.valueOf(rs.getString("nom"));
                return new Categorie(rs.getInt("id"), categorieNom);
            }
        }

        return null;
    }

    @Override
    public void ajouterVol(Vol vol) throws SQLException {
        String sql = "INSERT INTO vol (depart, destination, date, dateRetour, prix, categorie_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vol.getDepart());
            ps.setString(2, vol.getDestination());
            ps.setDate(3, new java.sql.Date(vol.getDate().getTime()));
            ps.setDate(4, vol.getDateRetour() != null ? new java.sql.Date(vol.getDateRetour().getTime()) : null);
            ps.setDouble(5, vol.getPrix());
            ps.setInt(6, vol.getCategorie().getId()); // car tu stockes une FK catégorie

            ps.executeUpdate();
        }
    }
//taux
    @Override
    public double calculerPrixFinal(Vol vol) {
        return vol.getPrix() * vol.getCategorie().getTaux();
    }

    @Override
    public List<Vol> chercherVolParDepartEtDestination(String depart, String destination) {
        List<Vol> volsTrouves = new ArrayList<>();
        List<Vol> allVols = getAllVols();

        for (Vol vol : allVols) {
            if (vol.getDepart().equalsIgnoreCase(depart) &&
                    vol.getDestination().equalsIgnoreCase(destination)) {
                volsTrouves.add(vol);
            }
        }

        return volsTrouves;
    }


}
