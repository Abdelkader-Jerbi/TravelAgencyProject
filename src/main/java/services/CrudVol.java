package services;

import entities.Categorie;
import entities.Enumnom;
import entities.Vol;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrudVol implements VoLInterface {

    @Override
    public List<Vol> chercherVol(String depart, String destination, String dateString, Enumnom categorie) {
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

    private List<Vol> getAllVols() {
        List<Vol> vols = new ArrayList<>();
        String sql = "SELECT v.id_vol, v.depart, v.destination, v.date, v.prix, c.id, c.nom " +
                "FROM vol v JOIN categorie c ON v.categorie_id = c.id";

        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_vol");
                String depart = rs.getString("depart");
                String destination = rs.getString("destination");
                Date date = rs.getDate("date");
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
                Vol vol = new Vol(id, depart, destination, date, prix, cat);
                vols.add(vol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return vols;
    }

}
