package services;

import entities.Categorie;
import entities.Enumnom;
import entities.Vol;

import java.sql.SQLException;
import java.util.List;

public interface VoLInterface {
    List<Vol> chercherVol(String depart, String destination,  String dateString, String dateRetourString, Enumnom categorie);
    List<Vol> getAllVols();
    void supprimerVol(int id);
    void modifierVol(Vol vol);
    Categorie getCategorieByNom(String nom) throws SQLException;
    void ajouterVol(Vol vol) throws SQLException;
    double calculerPrixFinal(Vol vol);
    List<Vol> chercherVolParDepartEtDestination(String depart, String destination);

}
