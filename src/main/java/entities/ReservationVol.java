package entities;

import java.sql.Date;


public class ReservationVol {

    private int idReservation;
    private Date dateReservation;
    private Date dateDebut;
    private Date dateFin;
    private Float prixTotal;
    private int nbPersonnes;
    private String statut;
    private Voiture voiture;
    private String villeRetour;
    private String villeDepart;
    private String nom;
    private String prenom;


    public ReservationVol(int idReservation, float prixTotal, String statut, Date dateReservation, int idHotel, int idVol, int idUser, int idVoiture) {

        this.idReservation = idReservation;
        this.dateReservation = dateReservation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.villeRetour = villeRetour;
        this.villeDepart = villeDepart;
        this.prixTotal = prixTotal;
        this.nbPersonnes = nbPersonnes;
        this.statut = statut;
        this.voiture = voiture;
        this.nom = nom;
        this.prenom = prenom;
    }

    // ✅ (Optionnel) constructeur ReservationVol sans ID (utile avant insertion en base)
    public ReservationVol(Date dateReservation, Date dateDebut, Date dateFin,
                       String villeRetour, String villeDepart, Float prixTotal,
                       int nbPersonnes, String statut, Voiture voiture) {
        this.dateReservation = dateReservation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.villeRetour = villeRetour;
        this.villeDepart = villeDepart;
        this.prixTotal = prixTotal;
        this.nbPersonnes = nbPersonnes;
        this.statut = statut;
        this.voiture = voiture;
    }

    // ✅ Getters & Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Float getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Float prixTotal) {
        this.prixTotal = prixTotal;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public String getVilleRetour() {
        return villeRetour;
    }

    public void setVilleRetour(String villeRetour) {
        this.villeRetour = villeRetour;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public String toString() {
        return "ReservationVol{" +
                "idReservation=" + idReservation +
                ", dateReservation=" + dateReservation +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", prixTotal=" + prixTotal +
                ", nbPersonnes=" + nbPersonnes +
                ", statut='" + statut + '\'' +
                ", voiture=" + voiture +
                ", villeRetour='" + villeRetour + '\'' +
                ", villeDepart='" + villeDepart + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
