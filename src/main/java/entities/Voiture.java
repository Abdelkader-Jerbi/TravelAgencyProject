package entities;

import javafx.scene.image.ImageView;

public class Voiture {
    // tes champs privés ici, avec getters, setters, etc.

    private int id;
    private String marque;
    private String modele;
    private String matricule;
    private double prixParJour;
    private String imagePath;
    private boolean disponible;   // correspond à disponibleCol
    private String carburant;
    private String boiteVitesse;
    private int nbPlaces;
    private int nbPortes;
    private String couleur;
    private boolean climatisation;
    private int kilometrage;
    private int annee;
    private String categorie;

    // Constructeur complet
    public Voiture(int id, String marque, String modele, String matricule, double prixParJour,
                   String imagePath, boolean disponible, String carburant, String boiteVitesse,
                   int nbPlaces, int nbPortes, String couleur, boolean climatisation,
                   int kilometrage, int annee, String categorie) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour;
        this.imagePath = imagePath;
        this.disponible = disponible;
        this.carburant = carburant;
        this.boiteVitesse = boiteVitesse;
        this.nbPlaces = nbPlaces;
        this.nbPortes = nbPortes;
        this.couleur = couleur;
        this.climatisation = climatisation;
        this.kilometrage = kilometrage;
        this.annee = annee;
        this.categorie = categorie;
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

    public double getPrixParJour() {
        return prixParJour;
    }

    public void setPrixParJour(double prixParJour) {
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

    public void setCarburant(String carburant) {
        this.carburant = carburant;
    }

    public String getBoiteVitesse() {
        return boiteVitesse;
    }

    public void setBoiteVitesse(String boiteVitesse) {
        this.boiteVitesse = boiteVitesse;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getNbPortes() {
        return nbPortes;
    }

    public void setNbPortes(int nbPortes) {
        this.nbPortes = nbPortes;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public boolean isClimatisation() {
        return climatisation;
    }

    public void setClimatisation(boolean climatisation) {
        this.climatisation = climatisation;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
// getters et setters

    public boolean getDisponible() {
        return disponible;
    }

    public String getCarburant() {
        return carburant;
    }

    public String getCouleur() {
        return couleur;
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
                ", carburant='" + carburant + '\'' +
                ", boiteVitesse='" + boiteVitesse + '\'' +
                ", nbPlaces=" + nbPlaces +
                ", nbPortes=" + nbPortes +
                ", couleur='" + couleur + '\'' +
                ", climatisation=" + climatisation +
                ", kilometrage=" + kilometrage +
                ", annee=" + annee +
                ", categorie='" + categorie + '\'' +
                '}';
    }

    // Méthode pour le filtre :
    public boolean matches(String searchText, String colorFilter, String carburantFilter, String dispoFilter) {
        boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                (marque != null && marque.toLowerCase().contains(searchText.toLowerCase())) ||
                (modele != null && modele.toLowerCase().contains(searchText.toLowerCase())) ||
                (matricule != null && matricule.toLowerCase().contains(searchText.toLowerCase()));

        boolean matchesColor = colorFilter == null || colorFilter.isEmpty() ||
                (couleur != null && couleur.toLowerCase().contains(colorFilter.toLowerCase()));

        boolean matchesCarburant = carburantFilter == null ||
                carburantFilter.equalsIgnoreCase("Tous") ||
                (carburant != null && carburant.equalsIgnoreCase(carburantFilter));

        boolean matchesDispo = dispoFilter == null ||
                dispoFilter.equalsIgnoreCase("Tous") ||
                (dispoFilter.equalsIgnoreCase("Disponible") && disponible) ||
                (dispoFilter.equalsIgnoreCase("Indisponible") && !disponible);

        return matchesSearch && matchesColor && matchesCarburant && matchesDispo;
    }

    // méthode pour récupérer ImageView si tu souhaites afficher l’image dans la table
    public ImageView getImageView() {
        // Charger l’image depuis imagePath, retourner un ImageView
        // Par exemple :
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(imagePath, 100, 60, true, true);
            return new ImageView(img);
        } catch (Exception e) {
            return null;
        }
    }
}
