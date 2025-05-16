package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UtilisateurInfo {

    @FXML
    private TextField tel;

    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    @FXML
    private TextField email;

    @FXML
    private TextField role;

    public void setAge(String tel) {this.tel.setText(tel);
    }

    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.setText(prenom);
    }

    public void setEmail(String email) { this.email.setText(email);}

    public void setRole(String role) { this.role.setText(role);}
}
