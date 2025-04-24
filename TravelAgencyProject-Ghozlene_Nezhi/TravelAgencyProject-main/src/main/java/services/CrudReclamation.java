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
        String sql = "INSERT INTO avis (idAvis, idUser, categorie, date, commentaire) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getIdAvis()); // ou getIdReclamation()
        stmt.setInt(2, reclamation.getIdUser());
        stmt.setString(3, reclamation.getCategorie());
        stmt.setString(4, reclamation.getDate());
        stmt.setString(5, reclamation.getCommentaire());
        stmt.executeUpdate();
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {
        String sql = "UPDATE avis SET idUser=?, categorie=?, date=?, commentaire=? WHERE idAvis=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reclamation.getIdUser());
        stmt.setString(2, reclamation.getCategorie());
        stmt.setString(3, reclamation.getDate());
        stmt.setString(4, reclamation.getCommentaire());
        stmt.setInt(5, reclamation.getIdAvis()); // ou getIdReclamation()
        stmt.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM avis WHERE idAvis=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM avis";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Reclamation r = new Reclamation();
            r.setIdAvis(rs.getInt("idAvis"));  // ou setIdReclamation
            r.setIdUser(rs.getInt("idUser"));
            r.setCategorie(rs.getString("categorie"));
            r.setDate(rs.getString("date"));
            r.setCommentaire(rs.getString("commentaire"));
            list.add(r);
        }

        return list;
    }
}
