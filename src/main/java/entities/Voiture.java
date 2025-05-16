package entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private String matricule;
    private double prixParJour; // Changement de type en double
    private String imagePath;
    private boolean disponible;

    // Constructeur sans paramètres
    public Voiture() {}

    // Constructeur avec tous les paramètres
    public Voiture(int id, String marque, String modele, String matricule, double prixParJour, String imagePath, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour; // Type double
        this.imagePath = imagePath;
        this.disponible = disponible;
    }

    // Constructeur sans l'ID
    public Voiture(String marque, String modele, String matricule, double prixParJour, String imagePath, boolean disponible) {
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour; // Type double
        this.imagePath = imagePath;
        this.disponible = disponible;
    }

    // Getters & Setters
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

    public double getPrixParJour() {
        return prixParJour;
    }
    public void setPrixParJour(double prixParJour) {
        this.prixParJour = prixParJour; // Type double
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
        return "Voiture{" +
                "id=" + id +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", matricule='" + matricule + '\'' +
                ", prixParJour=" + prixParJour +
                ", imagePath='" + imagePath + '\'' +
                ", disponible=" + disponible +
                '}';
    }
    public ImageView getImageView() {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(100);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
        return imageView;
    }

}
