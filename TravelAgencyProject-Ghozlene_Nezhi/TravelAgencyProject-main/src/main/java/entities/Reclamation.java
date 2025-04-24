package entities;

public class Reclamation {

    private int idReclamation, idUser;
    private String date;
    private String categorie ,commentaire;

    public Reclamation() {
    }

    public Reclamation(int idAvis, int idUser, String date, String categorie, String commentaire) {
        this.idReclamation = idAvis;
        this.idUser = idUser;
        this.date = date;
        this.categorie = categorie;
        this.commentaire = commentaire;
    }

    public Reclamation(int idUser, String date, String categorie, String commentaire) {
        this.idUser = idUser;
        this.date = date;
        this.categorie = categorie;
        this.commentaire = commentaire;
    }
    public int getIdAvis() {
        return idReclamation;
    }
    public void setIdAvis(int idAvis) {
        this.idReclamation = idAvis;
    }
    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public String getCommentaire() {
        return commentaire;
    }
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", idUser=" + idUser +
                ", date=" + date +
                ", categorie='" + categorie + '\'' +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}
