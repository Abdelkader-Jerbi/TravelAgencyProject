package entities;

import java.util.Date;

public class ReservationVol {
    private int idReservation;
    private double prixTotal;
    private String status;
    private Date dateReservation;
    private int idHotel;
    private int idVol;
    private int idUser;
    private int idVoiture;

    public ReservationVol(int idReservation, double prixTotal, String status, Date dateReservation, int idHotel, int idVol, int idUser, int idVoiture) {
        this.idReservation = idReservation;
        this.prixTotal = prixTotal;
        this.status = status;
        this.dateReservation = dateReservation;
        this.idHotel = idHotel;
        this.idVol = idVol;
        this.idUser = idUser;
        this.idVoiture = idVoiture;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdVoiture() {
        return idVoiture;
    }

    public void setIdVoiture(int idVoiture) {
        this.idVoiture = idVoiture;
    }
}
