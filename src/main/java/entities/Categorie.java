package entities;

public class Categorie {
    private int id ;
    private  Enumnom nom ;

    public Categorie(int id, Enumnom nom) {
        this.id = id;
        this.nom = nom;
    }

    public Categorie(Enumnom nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTaux() {
        return nom.getTaux();
    }
    public Enumnom getNom() {
        return nom;
    }

    public void setNom(Enumnom nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom=" + nom +
                '}';
    }
}
