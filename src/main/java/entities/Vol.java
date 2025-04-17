package entities;

import java.util.Date;

public class Vol {

    private int id_vol ;
    private  String depart ;
    private  String destination ;
    private Date date ;
    private double prix ;

    public Vol(int id_vol, String depart, String destination, Date date, double prix) {
        this.id_vol = id_vol;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.prix = prix;
    }

    public int getId_vol() {
        return id_vol;
    }

    public void setId_vol(int id_vol) {
        this.id_vol = id_vol;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Vol{" +
                "id_vol=" + id_vol +
                ", depart='" + depart + '\'' +
                ", destination='" + destination + '\'' +
                ", date=" + date +
                ", prix=" + prix +
                '}';
    }
}
