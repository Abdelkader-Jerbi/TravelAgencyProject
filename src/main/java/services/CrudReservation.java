package services;

import entities.reservation;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrudReservation {
    private final Connection cnx;

    public CrudReservation() {
        this.cnx = MyDatabase.getInstance().getConnection();
    }

    public void ajouterReservationVolSeulement(reservation r) {
        String sql = "INSERT INTO reservation (date_reservation, id_vol, status) VALUES (?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setDate(1, new java.sql.Date(r.getDateReservation().getTime()));
            pst.setInt(2, r.getIdVol());
            pst.setString(3, r.getStatus());

            pst.executeUpdate();
            System.out.println("Réservation enregistrée avec succès (vol + status).");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de la réservation : " + e.getMessage());
        }
    }


}
