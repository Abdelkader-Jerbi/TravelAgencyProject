package entities;

/**
 * Classe représentant un hébergement
 */
public class Hebergement {
    private int idHebergement;
    private String nom;
    private String type;
    private String adresse;
    private String ville;
    private int etoiles;
    private double prixNuit;
    private int capacite;
    private String description;
    private String disponibilite;

    /**
     * Constructeur par défaut
     */
    public Hebergement() {
    }

    /**
     * Constructeur avec tous les attributs
     * 
     * @param idHebergement ID de l'hébergement
     * @param nom Nom de l'hébergement
     * @param type Type d'hébergement
     * @param adresse Adresse de l'hébergement
     * @param ville Ville de l'hébergement
     * @param etoiles Nombre d'étoiles
     * @param prixNuit Prix par nuit
     * @param capacite Capacité de l'hébergement
     * @param description Description de l'hébergement
     * @param disponibilite Disponibilité de l'hébergement
     */
    public Hebergement(int idHebergement, String nom, String type, String adresse, String ville, 
                       int etoiles, double prixNuit, int capacite, String description, String disponibilite) {
        this.idHebergement = idHebergement;
        this.nom = nom;
        this.type = type;
        this.adresse = adresse;
        this.ville = ville;
        this.etoiles = etoiles;
        this.prixNuit = prixNuit;
        this.capacite = capacite;
        this.description = description;
        this.disponibilite = disponibilite;
    }

    /**
     * Constructeur sans ID (pour l'insertion en base de données)
     * 
     * @param nom Nom de l'hébergement
     * @param type Type d'hébergement
     * @param adresse Adresse de l'hébergement
     * @param ville Ville de l'hébergement
     * @param etoiles Nombre d'étoiles
     * @param prixNuit Prix par nuit
     * @param capacite Capacité de l'hébergement
     * @param description Description de l'hébergement
     * @param disponibilite Disponibilité de l'hébergement
     */
    public Hebergement(String nom, String type, String adresse, String ville, 
                       int etoiles, double prixNuit, int capacite, String description, String disponibilite) {
        this.nom = nom;
        this.type = type;
        this.adresse = adresse;
        this.ville = ville;
        this.etoiles = etoiles;
        this.prixNuit = prixNuit;
        this.capacite = capacite;
        this.description = description;
        this.disponibilite = disponibilite;
    }

    /**
     * Constructeur simplifié pour l'affichage dans l'interface
     * 
     * @param idHebergement ID de l'hébergement (en format String)
     * @param nom Nom de l'hébergement
     * @param type Type d'hébergement
     * @param adresse Adresse de l'hébergement
     * @param ville Ville de l'hébergement
     * @param etoiles Nombre d'étoiles (en format String)
     * @param prixNuit Prix par nuit (en format String)
     */
    public Hebergement(String idHebergement, String nom, String type, String adresse, String ville, 
                       String etoiles, String prixNuit) {
        this.idHebergement = Integer.parseInt(idHebergement);
        this.nom = nom;
        this.type = type;
        this.adresse = adresse;
        this.ville = ville;
        this.etoiles = Integer.parseInt(etoiles);
        this.prixNuit = Double.parseDouble(prixNuit);
        this.capacite = 4; // Valeur par défaut
        this.description = "Description non disponible"; // Valeur par défaut
        this.disponibilite = "Disponible"; // Valeur par défaut
    }

    // Getters et Setters
    public int getIdHebergement() {
        return idHebergement;
    }

    public void setIdHebergement(int idHebergement) {
        this.idHebergement = idHebergement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getEtoiles() {
        return etoiles;
    }

    public void setEtoiles(int etoiles) {
        this.etoiles = etoiles;
    }

    public double getPrixNuit() {
        return prixNuit;
    }

    public void setPrixNuit(double prixNuit) {
        this.prixNuit = prixNuit;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

    @Override
    public String toString() {
        return "Hebergement{" +
                "idHebergement=" + idHebergement +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", adresse='" + adresse + '\'' +
                ", ville='" + ville + '\'' +
                ", etoiles=" + etoiles +
                ", prixNuit=" + prixNuit +
                ", capacite=" + capacite +
                ", description='" + description + '\'' +
                ", disponibilite='" + disponibilite + '\'' +
                '}';
    }
}