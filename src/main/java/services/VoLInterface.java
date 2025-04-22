package services;

import entities.Enumnom;
import entities.Vol;

import java.util.Date;
import java.util.List;

public interface VoLInterface {
    List<Vol> chercherVol(String depart, String destination,  String dateString, Enumnom categorie);
}
