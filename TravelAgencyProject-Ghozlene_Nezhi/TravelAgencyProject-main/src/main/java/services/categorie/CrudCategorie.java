package services.categorie;


import entities.Categorie;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class CrudCategorie implements CrudCateg<Categorie> {

        private Connection connection;

        public CrudCategorie() {
            this.connection = MyDatabase.getInstance().getConnection();
        }

        @Override
        public void ajouter(Categorie categorie) throws SQLException {
            String sql = "INSERT INTO categorie (description) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, categorie.getDescription());
            stmt.executeUpdate();
        }

        @Override
        public void modifier(Categorie categorie) throws SQLException {
            String sql = "UPDATE categorie SET description = ? WHERE idCategorie = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, categorie.getDescription());
            stmt.setInt(2, categorie.getIdCategorie());
            stmt.executeUpdate();
        }

        @Override
        public void supprimer(int id) throws SQLException {
            String sql = "DELETE FROM categorie WHERE idCategorie = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        @Override
        public List<Categorie> afficher() throws SQLException {
            List<Categorie> list = new ArrayList<>();
            String sql = "SELECT * FROM categorie";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Categorie c = new Categorie();
                c.setIdCategorie(rs.getInt("idCategorie"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }

            return list;
        }
    }
