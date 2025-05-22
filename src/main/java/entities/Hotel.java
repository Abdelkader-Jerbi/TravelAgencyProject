package entities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hotel {

    private int idHotel;
    private String nom;
    private Date date;
    private String Localisation;
    private int nbEtoile;
    private String Chambre;
    private float tarif;
    private String image;
    private String description;
    private List<String> amenities;
    private String categorieType;

    // Common hotel amenities
    public static final String[] COMMON_AMENITIES = {
        "Free WiFi",
        "Swimming Pool",
        "Fitness Center",
        "Restaurant",
        "Room Service",
        "Air Conditioning",
        "Parking",
        "Spa",
        "Business Center",
        "Conference Rooms",
        "Bar/Lounge",
        "Laundry Service",
        "24/7 Front Desk",
        "Airport Shuttle",
        "Pet Friendly",
        "Beach Access",
        "Garden",
        "Tennis Court",
        "Golf Course",
        "Kids Club"
    };

    public Hotel() {
        this.amenities = new ArrayList<>();
    }

    public Hotel(String image, String nom, String Localisation, Date date, String description, String categorieType, String Chambre, int nbEtoile, float tarif) {
        this.image = image;
        this.nom = nom;
        this.Localisation = Localisation;
        this.date = date;
        this.description=description;
        this.categorieType = categorieType;
        this.Chambre = Chambre;
        this.nbEtoile = nbEtoile;
        this.tarif = tarif;
        this.amenities = new ArrayList<>();
    }

    public Hotel(int idHotel, String image, String nom, String Localisation, Date date, String description, String categorieType, String Chambre, int nbEtoile, float tarif) {
        this.idHotel = idHotel;
        this.nom = nom;
        this.Localisation = Localisation;
        this.date = date;
        this.description=description;
        this.categorieType = categorieType;
        this.Chambre = Chambre;
        this.nbEtoile = nbEtoile;
        this.tarif = tarif;
        this.image = image;
        this.amenities = new ArrayList<>();
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

    public String getChambre() {
        return Chambre;
    }

    public void setChambre(String Chambre) {
        this.Chambre = Chambre;
    }

    public String getImage() { return image; }
    public void setImage (String image) { this.image = image; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public void addAmenity(String amenity) {
        if (!this.amenities.contains(amenity)) {
            this.amenities.add(amenity);
        }
    }

    public void removeAmenity(String amenity) {
        this.amenities.remove(amenity);
    }

    public boolean hasAmenity(String amenity) {
        return this.amenities.contains(amenity);
    }

    public String getAmenitiesAsString() {
        return String.join(", ", amenities);
    }

    public void setAmenitiesFromString(String amenitiesString) {
        if (amenitiesString != null && !amenitiesString.isEmpty()) {
            String[] amenityArray = amenitiesString.split(",");
            this.amenities.clear();
            for (String amenity : amenityArray) {
                this.amenities.add(amenity.trim());
            }
        }
    }

    public String getCategorieType() {
        return categorieType;
    }

    public void setCategorieType(String categorieType) {
        this.categorieType = categorieType;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "idHotel=" + idHotel +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", Localisation='" + Localisation + '\'' +
                ", nbEtoile=" + nbEtoile +
                ", tarif=" + tarif +
                ", Chambre=" + Chambre +
                ", amenities=" + amenities +
                '}';
    }
}
