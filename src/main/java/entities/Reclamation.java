package entities;

public class Reclamation {

    private int idReclamation;
    private int id;
    private int idCategorie;
    private String date;
    private String commentaire;
    private String statut;
    private String email;
    private String reponse;

    public Reclamation() {
    }

    public Reclamation(int idReclamation, int id, int idCategorie, String date, String commentaire, String statut) {
        this.idReclamation = idReclamation;
        this.id = id;
        this.idCategorie = idCategorie;
        this.date = date;
        this.commentaire = commentaire;
        this.statut = statut;
    }

    public Reclamation(int id, int idCategorie, String date, String commentaire, String statut) {
        this.id = id;
        this.idCategorie = idCategorie;
        this.date = date;
        this.commentaire = commentaire;
        this.statut = statut;
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", id=" + id +
                ", idCategorie=" + idCategorie +
                ", date='" + date + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }

    public String getCategorie() {
        return String.valueOf(idCategorie);
    }
}
