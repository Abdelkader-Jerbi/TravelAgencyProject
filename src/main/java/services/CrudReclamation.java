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
        String sql = "INSERT INTO reclamation (id, idCategorie, date, commentaire, statut) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getId());
        stmt.setInt(2, reclamation.getIdCategorie());
        stmt.setString(3, reclamation.getDate());
        stmt.setString(4, reclamation.getCommentaire());
        stmt.setString(5, reclamation.getStatut());
        stmt.executeUpdate();
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
        String sql = "UPDATE reclamation SET id=?, idCategorie=?, date=?, commentaire=?, statut=?, reponse=? WHERE idReclamation=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getId());
        stmt.setInt(2, reclamation.getIdCategorie());
        stmt.setString(3, reclamation.getDate());
        stmt.setString(4, reclamation.getCommentaire());
        stmt.setString(5, reclamation.getStatut());
        stmt.setString(6, reclamation.getReponse());
        stmt.setInt(7, reclamation.getIdReclamation());
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
        String sql = "SELECT r.*, u.email FROM reclamation r JOIN utilisateur u ON r.id = u.id";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Reclamation r = new Reclamation();
            r.setIdReclamation(rs.getInt("idReclamation"));
            r.setId(rs.getInt("id"));
            r.setIdCategorie(rs.getInt("idCategorie"));
            r.setDate(rs.getString("date"));
            r.setCommentaire(rs.getString("commentaire"));
            r.setStatut(rs.getString("statut"));
            r.setEmail(rs.getString("email"));
            r.setReponse(rs.getString("reponse"));
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
                reclamation.setId(rs.getInt("id"));
                reclamation.setIdCategorie(rs.getInt("idCategorie"));
                reclamation.setDate(rs.getString("date"));
                reclamation.setCommentaire(rs.getString("commentaire"));
                reclamation.setStatut(rs.getString("statut"));
                reclamation.setReponse(rs.getString("reponse"));
                return reclamation;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reclamation> getReclamationsByUserId(int userId) throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation WHERE id = ?";
        
        try (Connection conn = MyDatabase.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setIdReclamation(rs.getInt("idReclamation"));
                reclamation.setId(rs.getInt("id"));
                reclamation.setIdCategorie(rs.getInt("idCategorie"));
                reclamation.setDate(rs.getString("date"));
                reclamation.setCommentaire(rs.getString("commentaire"));
                reclamation.setStatut(rs.getString("statut"));
                reclamations.add(reclamation);
            }
        }
        
        return reclamations;
    }

    public List<String> getAdminEmails() throws SQLException {
        List<String> adminEmails = new ArrayList<>();
        String sql = "SELECT email FROM utilisateur WHERE role = 'ADMIN'";
        
        System.out.println("Recherche des emails des administrateurs...");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String email = rs.getString("email");
                adminEmails.add(email);
                System.out.println("Email admin trouvé : " + email);
            }
        }
        
        if (adminEmails.isEmpty()) {
            System.out.println("Aucun email d'administrateur trouvé dans la base de données");
        }
        
        return adminEmails;
    }
}
