package entities;

import java.sql.Date;
import java.time.LocalDate;


public class Hotel<H> {

    private int idHotel;
    private String nom;
    private Date date;
    private String Localisation;
    private int nbEtoile;
    private int nombreChambre;
    private  int nombreNuité;
    private float tarif;
    private String image;


    public Hotel(String nom, String Localisation, Date date,int nombreNuité,int nombreChambre, int nbEtoile, float tarif ) {

        this.nom = nom;
        this.Localisation = Localisation;
        this.date = date;
        this.nombreNuité=nombreNuité;
        this.nombreChambre = nombreChambre;
        this.nbEtoile = nbEtoile;
        this.tarif = tarif;
    }

    public Hotel(int idHotel, String nom, String Localisation, Date date,int nombreNuité,int nombreChambre, int nbEtoile, float tarif ) {
        this.idHotel = idHotel;
        this.nom = nom;
        this.Localisation = Localisation;
        this.date = date;
        this.nombreNuité=nombreNuité;
        this.nombreChambre = nombreChambre;
        this.nbEtoile = nbEtoile;
        this.tarif = tarif;
    }


    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return Localisation;
    }

    public void setLocalisation(String Localisation) {
        this.Localisation = Localisation;
    }

    public int getNbEtoile() {
        return nbEtoile;
    }

    public void setNbEtoile(int nbEtoile) {
        this.nbEtoile = nbEtoile;
    }

    public float getTarif() {
        return tarif;
    }

    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNombreChambre() {
        return nombreChambre;
    }

    public int getNombreNuité() {
        return nombreNuité;
    }

    public void setNombreNuité(int nombreNuité) {
        this.nombreNuité = nombreNuité;
    }

    public void setNombreChambre(int nombreChambre) {
        this.nombreChambre = nombreChambre;
    }

    public String getImageUrl() { return image; }
    public void setImage (String image) { this.image = image; }

    @Override
    public String toString() {
        return "Hotel{" +
                "idHotel=" + idHotel +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", Localisation='" + Localisation + '\'' +
                ", nbEtoile=" + nbEtoile +
                ", tarif=" + tarif +
                ", nombreChambre=" + nombreChambre +
                '}';
    }
}
