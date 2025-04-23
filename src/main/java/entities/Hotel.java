package entities;

import java.util.Date;

public class Hotel<H> {

    private int idHotel;
    private String nom;
    private Date date;
    private String destination;
    private int nbEtoile;
    private int nombreChambre;
    private  int nombreNuité;
    private float tarif;


    public Hotel(String nom, String destination, Date date,int nombreNuité,int nombreChambre, int nbEtoile, float tarif ) {

        this.nom = nom;
        this.destination = destination;
        this.date = date;
        this.nombreNuité=nombreNuité;
        this.nombreChambre = nombreChambre;
        this.nbEtoile = nbEtoile;
        this.tarif = tarif;
    }

    public Hotel(int idHotel, String nom, String destination, Date date,int nombreNuité,int nombreChambre, int nbEtoile, float tarif ) {
        this.idHotel = idHotel;
        this.nom = nom;
        this.destination = destination;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getNbEtoile() {
        return nbEtoile;
    }

    public void setNbEtoiles(int nbEtoile) {
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

    @Override
    public String toString() {
        return "Hotel{" +
                "idHotel=" + idHotel +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", destination='" + destination + '\'' +
                ", nbEtoile=" + nbEtoile +
                ", tarif=" + tarif +
                ", nombreChambre=" + nombreChambre +
                '}';
    }
}
