package services;

import entities.Reclamation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudReclamation implements CrudRec<Reclamation> {

    Connection connection;

    public CrudReclamation() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        String sql = "INSERT INTO reclamation (idUser, idCategorie, date, commentaire, statut) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getIdUser());
        stmt.setInt(2, reclamation.getIdCategorie());
        stmt.setString(3, reclamation.getDate());
        stmt.setString(4, reclamation.getCommentaire());
        stmt.setString(5, reclamation.getStatut());
        stmt.executeUpdate();
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
        String sql = "UPDATE reclamation SET idUser=?, idCategorie=?, date=?, commentaire=?, statut=? WHERE idReclamation=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getIdUser());
        stmt.setInt(2, reclamation.getIdCategorie());
        stmt.setString(3, reclamation.getDate());
        stmt.setString(4, reclamation.getCommentaire());
        stmt.setString(5, reclamation.getStatut());
        stmt.setInt(6, reclamation.getIdReclamation());
        stmt.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM reclamation WHERE idReclamation=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.email FROM reclamation r JOIN utilisateur u ON r.idUser = u.idUser";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Reclamation r = new Reclamation();
            r.setIdReclamation(rs.getInt("idReclamation"));
            r.setIdUser(rs.getInt("idUser"));
            r.setIdCategorie(rs.getInt("idCategorie"));
            r.setDate(rs.getString("date"));
            r.setCommentaire(rs.getString("commentaire"));
            r.setStatut(rs.getString("statut"));
            r.setEmail(rs.getString("email"));
            list.add(r);
        }

        return list;
    }

    public Reclamation getById(int id) {
        String query = "SELECT * FROM reclamation WHERE idReclamation = ?";
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setIdReclamation(rs.getInt("idReclamation"));
                reclamation.setIdUser(rs.getInt("idUser"));
                reclamation.setIdCategorie(rs.getInt("idCategorie"));
                reclamation.setDate(rs.getString("date"));
                reclamation.setCommentaire(rs.getString("commentaire"));
                reclamation.setStatut(rs.getString("statut"));
                return reclamation;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
