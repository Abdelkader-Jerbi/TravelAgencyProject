package services;

import entities.Utilisateur;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudUtilisateur implements CrudMethods<Utilisateur> {
    Connection connection;
    public CrudUtilisateur(){
        connection= MyDatabase.getInstance().getConnection();

    }
    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {

        String req="insert into utilisateur (nom,prenom,email,tel,role)"+
                "values('"+ utilisateur.getNom()+"','"+ utilisateur.getPrenom()+"','"+ utilisateur.getEmail()+"','"+utilisateur.getTel()+"','"+utilisateur.getRole()+"')";

        Statement statement=connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Utilisateur ajouté");


    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {
        String req="update utilisateur set nom=?,prenom=?,email=?,tel=?  where id=?";
        PreparedStatement preparedStatement= connection.prepareStatement(req);
        preparedStatement.setString(1, utilisateur.getNom());
        preparedStatement.setString(2, utilisateur.getPrenom());
        preparedStatement.setString(3, utilisateur.getEmail());
        preparedStatement.setInt(4, utilisateur.getTel());
        preparedStatement.setInt(5, utilisateur.getId());
        preparedStatement.executeUpdate();


    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<Utilisateur> afficher() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req="select * from utilisateur";
        Statement statement= connection.createStatement();

       ResultSet rs= statement.executeQuery(req);
       while (rs.next()){
           Utilisateur utilisateur = new Utilisateur();
           utilisateur.setNom(rs.getString("nom"));
           utilisateur.setPrenom(rs.getString("prenom"));
           utilisateur.setEmail(rs.getString("email"));
           utilisateur.setRole(rs.getString("role"));
           utilisateur.setTel(rs.getInt("tel"));
           utilisateur.setId(rs.getInt("id"));

           utilisateurs.add(utilisateur);
       }


        return utilisateurs;
    }
    public List<String> getAllEmails() {
        List<String> emails = new ArrayList<>();
        String req = "SELECT email FROM utilisateur ";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(req)) {

            while (rs.next()) {
                emails.add(rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

}
