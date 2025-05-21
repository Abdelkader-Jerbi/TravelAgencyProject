package entities;
public class Categorie {
    private int idCategorie;
    private String description;

    public Categorie() {
    }

    public Categorie(int idCategorie, String description) {
        this.idCategorie = idCategorie;
        this.description = description;
    }

    // Getters and Setters
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}