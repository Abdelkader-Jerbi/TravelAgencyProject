
package entities;

public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private String matricule;
    private double prixParJour;
    private String imagePath ;
    private boolean disponible  ;

    public Voiture() {}

    public Voiture(int id, String marque, String modele, String matricule, double prixParJour, String imagePath , boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour;
        this.imagePath ="";
        this.disponible = Boolean.parseBoolean("true");
    }

    public Voiture(String marque, String modele, String matricule, double prixParJour , String imagePath , boolean disponible) {
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.prixParJour = prixParJour;
        this.imagePath = imagePath;
        this.disponible = disponible ;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public double getPrixParJour() { return prixParJour; }
    public void setPrixParJour(double prixParJour) { this.prixParJour = prixParJour; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public boolean isDisponible() { return disponible ; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

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


}






/*package entities;

import javafx.beans.property.*;

public class Voiture {

    private final IntegerProperty id;
    private final StringProperty marque;
    private final StringProperty modele;
    private final StringProperty matricule;
    private final DoubleProperty prixParJour;
    private final StringProperty imagePath;
    private boolean disponible;




    public Voiture(int id, String marque, String modele, String matricule, double prixParJour, boolean disponible, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.marque = new SimpleStringProperty(marque);
        this.modele = new SimpleStringProperty(modele);
        this.matricule = new SimpleStringProperty(matricule);
        this.prixParJour = new SimpleDoubleProperty(prixParJour);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.disponible = disponible; // Initialisation de la disponibilité
    }

    public Voiture(int id, String marque, String modele, String matricule, double prixParJour, String imagePath) {
        this(id, marque, modele, matricule, prixParJour, true, imagePath); // Par défaut, disponible est true
    }



    // Getters simples pour les propriétés
    public int getId() {
        return id.get();
    }

    public String getMarque() {
        return marque.get();
    }

    public String getModele() {
        return modele.get();
    }

    public String getMatricule() {
        return matricule.get();
    }

    public double getPrixParJour() {
        return prixParJour.get();
    }

    public String getImagePath() {
        return imagePath.get();
    }

    // Getters pour les propriétés (pour les liaisons dans JavaFX)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty marqueProperty() {
        return marque;
    }

    public StringProperty modeleProperty() {
        return modele;
    }

    public StringProperty matriculeProperty() {
        return matricule;
    }

    public DoubleProperty prixParJourProperty() {
        return prixParJour;
    }

    public StringProperty imagePathProperty() {
        return imagePath;
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setMarque(String marque) {
        this.marque.set(marque);
    }

    public void setModele(String modele) {
        this.modele.set(modele);
    }

    public void setMatricule(String matricule) {
        this.matricule.set(matricule);
    }

    public void setPrixParJour(double prixParJour) {
        this.prixParJour.set(prixParJour);
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    public boolean isDisponible() {
        return disponible;
    }
}
*/
/*package entities;

import javafx.beans.property.*;

public class Voiture {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty marque = new SimpleStringProperty();
    private final StringProperty modele = new SimpleStringProperty();
    private final StringProperty matricule = new SimpleStringProperty();
    private final DoubleProperty prixParJour = new SimpleDoubleProperty(); // correspond à prixjour dans la base
    private boolean disponible;
    private String imagePath;
    public Voiture(int id, String marque, String modele, String matricule, double prixParJour , String imagePath) {
        this.id.set(id);
        this.marque.set(marque);
        this.modele.set(modele);
        this.matricule.set(matricule);
        this.prixParJour.set(prixParJour);
        this.disponible = disponible;
        this.imagePath = imagePath;  // Chemin de l'image

    }

    public Voiture(String marque, String modele, String matricule, double prixParJour) {
        this.marque.set(marque);
        this.modele.set(modele);
        this.matricule.set(matricule);
        this.prixParJour.set(prixParJour);
    }

    public Voiture(String model, double price, boolean available, String imagePath) {
    }

    // Getters and setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getMarque() { return marque.get(); }
    public void setMarque(String value) { marque.set(value); }
    public StringProperty marqueProperty() { return marque; }
    public String getImagePath() {
        return imagePath;
    }
    public String getModele() { return modele.get(); }
    public void setModele(String value) { modele.set(value); }
    public StringProperty modeleProperty() { return modele; }

    public String getMatricule() { return matricule.get(); }
    public void setMatricule(String value) { matricule.set(value); }
    public StringProperty matriculeProperty() { return matricule; }

    public double getPrixParJour() { return prixParJour.get(); }
    public void setPrixParJour(double value) { prixParJour.set(value); }
    public DoubleProperty prixParJourProperty() { return prixParJour; }



    public boolean isDisponible() {
        return disponible;
    }

    public void setdisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
*/