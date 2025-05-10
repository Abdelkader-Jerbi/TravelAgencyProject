package entities;

/**
 * Classe représentant un vol
 */
public class Vol {
    private int idVol;
    private String numVol;
    private String compagnie;
    private String depart;
    private String destination;
    private String dateDepart;
    private String heure;
    private double prix;
    private int capacite;
    private String statut;

    /**
     * Constructeur par défaut
     */
    public Vol() {
    }

    /**
     * Constructeur avec tous les attributs
     * 
     * @param idVol ID du vol
     * @param numVol Numéro du vol
     * @param compagnie Compagnie aérienne
     * @param depart Ville de départ
     * @param destination Ville de destination
     * @param dateDepart Date de départ
     * @param heure Heure de départ
     * @param prix Prix du vol
     * @param capacite Capacité du vol
     * @param statut Statut du vol
     */
    public Vol(int idVol, String numVol, String compagnie, String depart, String destination, 
               String dateDepart, String heure, double prix, int capacite, String statut) {
        this.idVol = idVol;
        this.numVol = numVol;
        this.compagnie = compagnie;
        this.depart = depart;
        this.destination = destination;
        this.dateDepart = dateDepart;
        this.heure = heure;
        this.prix = prix;
        this.capacite = capacite;
        this.statut = statut;
    }

    /**
     * Constructeur sans ID (pour l'insertion en base de données)
     * 
     * @param numVol Numéro du vol
     * @param compagnie Compagnie aérienne
     * @param depart Ville de départ
     * @param destination Ville de destination
     * @param dateDepart Date de départ
     * @param heure Heure de départ
     * @param prix Prix du vol
     * @param capacite Capacité du vol
     * @param statut Statut du vol
     */
    public Vol(String numVol, String compagnie, String depart, String destination, 
               String dateDepart, String heure, double prix, int capacite, String statut) {
        this.numVol = numVol;
        this.compagnie = compagnie;
        this.depart = depart;
        this.destination = destination;
        this.dateDepart = dateDepart;
        this.heure = heure;
        this.prix = prix;
        this.capacite = capacite;
        this.statut = statut;
    }

    /**
     * Constructeur simplifié pour l'affichage dans l'interface
     * 
     * @param numVol Numéro du vol
     * @param compagnie Compagnie aérienne
     * @param depart Ville de départ
     * @param destination Ville de destination
     * @param dateDepart Date de départ
     * @param heure Heure de départ
     * @param prix Prix du vol (en format String)
     */
    public Vol(String numVol, String compagnie, String depart, String destination, 
               String dateDepart, String heure, String prix) {
        this.numVol = numVol;
        this.compagnie = compagnie;
        this.depart = depart;
        this.destination = destination;
        this.dateDepart = dateDepart;
        this.heure = heure;
        this.prix = Double.parseDouble(prix);
        this.capacite = 200; // Valeur par défaut
        this.statut = "Disponible"; // Valeur par défaut
    }

    // Getters et Setters
    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public String getNumVol() {
        return numVol;
    }

    public void setNumVol(String numVol) {
        this.numVol = numVol;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
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

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Vol{" +
                "idVol=" + idVol +
                ", numVol='" + numVol + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", depart='" + depart + '\'' +
                ", destination='" + destination + '\'' +
                ", dateDepart='" + dateDepart + '\'' +
                ", heure='" + heure + '\'' +
                ", prix=" + prix +
                ", capacite=" + capacite +
                ", statut='" + statut + '\'' +
                '}';
    }
}