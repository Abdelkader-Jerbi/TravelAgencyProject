package services;

import entities.Reservation;
import entities.Voiture;
import utils.MyDatabase;

import java.sql.*;

public class CrudReservation {

    private Connection conn;

    // Constructeur de la classe pour obtenir la connexion à la base de données
    public CrudReservation() {
        this.conn = MyDatabase.getInstance().getConnection();
    }

    // Méthode pour ajouter une réservation
    public void ajouterReservation(Reservation res) {
        // Requête SQL avec les noms de colonnes comme dans ta table
        String sql = "INSERT INTO reservation (dateReservation, dateDebut, dateFin, villeRetour, villeDepart, prixTotal, nbPersonnes, statut, idvoiture) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // ✅ Si dateReservation est null, on met la date du jour
            java.sql.Date dateReservation = (res.getDateReservation() != null)
                    ? new java.sql.Date(res.getDateReservation().getTime())
                    : new java.sql.Date(System.currentTimeMillis());

            // Remplir les paramètres de la requête
            pst.setDate(1, dateReservation);  // dateReservation
            pst.setDate(2, new java.sql.Date(res.getDateDebut().getTime()));  // dateDebut
            pst.setDate(3, new java.sql.Date(res.getDateFin().getTime()));  // dateFin
            pst.setString(4, res.getVilleRetour());  // villeRetour
            pst.setString(5, res.getVilleDepart());  // villeDepart
            pst.setFloat(6, res.getPrixTotal());  // prixTotal
            pst.setInt(7, res.getNbPersonnes());  // nbPersonnes
            pst.setString(8, res.getStatut());  // statut
            pst.setInt(9, res.getVoiture().getId());  // idvoiture

            // Exécution de la requête
            pst.executeUpdate();
            System.out.println("✅ Réservation ajoutée !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    // Méthode pour réserver une voiture (mettre à jour son statut de disponibilité)
    public boolean reserverVoiture(int idVoiture) {
        String query = "UPDATE voitures SET disponible = false WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idVoiture);  // Passer l'ID de la voiture à réserver
            int rowsUpdated = stmt.executeUpdate();

            return rowsUpdated > 0; // Retourne true si la mise à jour a réussi
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Si une erreur survient, retourne false
        }
    }
    public void mettreAJourVoiture(Voiture voiture) {
        String sql = "UPDATE voitures SET marque = ?, modele = ?, prixParJour = ?, matricule = ?, disponible = ?, imagePath = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // Remplir les paramètres de la requête SQL
            pst.setString(1, voiture.getMarque());  // Marque de la voiture
            pst.setString(2, voiture.getModele());  // Modèle de la voiture
            pst.setDouble(3, voiture.getPrixParJour());  // Prix par jour
            pst.setString(4, voiture.getMatricule());  // Matricule
            pst.setBoolean(5, voiture.isDisponible());  // Disponibilité
            pst.setString(6, voiture.getImagePath());  // Chemin de l'image
            pst.setInt(7, voiture.getId());  // ID de la voiture à mettre à jour

            // Exécuter la requête de mise à jour
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Voiture mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune voiture trouvée avec cet ID pour la mise à jour.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la voiture : " + e.getMessage());
        }
    }
}
