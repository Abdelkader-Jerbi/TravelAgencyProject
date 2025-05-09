package entities;

public class Reclamation {

    private int idReclamation;
    private int idUser;
    private int idCategorie;
    private String date;
    private String commentaire;
    private String statut;

    public Reclamation() {
    }

    public Reclamation(int idReclamation, int idUser, int idCategorie, String date, String commentaire, String statut) {
        this.idReclamation = idReclamation;
        this.idUser = idUser;
        this.idCategorie = idCategorie;
        this.date = date;
        this.commentaire = commentaire;
        this.statut = statut;
    }

    public Reclamation(int idUser, int idCategorie, String date, String commentaire, String statut) {
        this.idUser = idUser;
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

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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

    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", idUser=" + idUser +
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
