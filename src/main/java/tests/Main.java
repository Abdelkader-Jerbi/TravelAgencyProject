package tests;

import controllers.ShowHotel;
import entities.Hotel;
import entities.Utilisateur;
import services.CrudHotel;
import services.CrudUtilisateur;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CrudUtilisateur CRUDUtilisateur = new CrudUtilisateur();
        //Utilisateur p1= new Utilisateur(22,"foulen","BenFoulen");
        // Utilisateur p2= new Utilisateur(1,25,"Sami","BenFoulen");
        try {
            // servicePersonne.ajouter(p1);
           // CRUDUtilisateur.modifier(p2);
            System.out.println(CRUDUtilisateur.afficher());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}