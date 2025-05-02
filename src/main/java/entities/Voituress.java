package entities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Voituress {
    private  int id;
    private  String marque;
    private  String modele;
    private  String matricule;
    private  Double prixParJour;
    private  String imagePath = "dd";
    private boolean disponible = true;


    public Voituress() {
    }

    public Voituress(int id, String marque, String modele, String matricule, Double prixParJour, String imagePath, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour;
        this.imagePath = imagePath;
        this.disponible = disponible;
    }

    public Voituress(String marque, String modele, String matricule, Double prixParJour) {
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public Double getPrixParJour() {
        return prixParJour;
    }

    public void setPrixParJour(Double prixParJour) {
        this.prixParJour = prixParJour;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Voituress{" +
                "id=" + id +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", matricule='" + matricule + '\'' +
                ", prixParJour='" + prixParJour + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
