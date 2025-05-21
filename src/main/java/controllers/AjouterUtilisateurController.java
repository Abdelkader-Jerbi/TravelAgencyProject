package controllers;

import entities.Role;
import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import services.CrudUtilisateur;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterUtilisateurController {

    @FXML
    private TextField telTF;

    @FXML
    private TextField nomTF;

    @FXML
    private TextField prenomTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField roleTF;

    @FXML
    private PasswordField passwordTF;

    @FXML
    void AjouterUtilisateur(ActionEvent event) {

        if (!validateInputs()) {
            return; // Exit the method if validation fails
        }

        CrudUtilisateur CrudUtilisateur = new CrudUtilisateur();
        Utilisateur utilisateur = new Utilisateur(Integer.parseInt(telTF.getText()),nomTF.getText(),prenomTF.getText(),emailTF.getText(),passwordTF.getText(), Role.USER);
        try {
            CrudUtilisateur.ajouter(utilisateur);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("Utilisateur a ete ajouté avec succes !");
            alert.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUtilisateur.fxml"));

        } catch (SQLException e) {
            showErrorAlert(e.getMessage());
        }
    }
    

    private boolean validateInputs() {
        // Check for empty fields
        if (nomTF.getText().trim().isEmpty()) {
            showErrorAlert("Le nom ne peut pas être vide.");
            return false;
        }
        
        if (prenomTF.getText().trim().isEmpty()) {
            showErrorAlert("Le prénom ne peut pas être vide.");
            return false;
        }
        
        if (emailTF.getText().trim().isEmpty()) {
            showErrorAlert("L'email ne peut pas être vide.");
            return false;
        }
        
        if (telTF.getText().trim().isEmpty()) {
            showErrorAlert("Le numéro de téléphone ne peut pas être vide.");
            return false;
        }
        
        if (passwordTF.getText().isEmpty()) {
            showErrorAlert("Le mot de passe ne peut pas être vide.");
            return false;
        }
        
        // Validate email format
        if (!isValidEmail(emailTF.getText())) {
            showErrorAlert("Format d'email invalide.");
            return false;
        }
        
        // Validate telephone number
        if (!isValidPhoneNumber(telTF.getText())) {
            showErrorAlert("Le numéro de téléphone doit contenir uniquement des chiffres.");
            return false;
        }
        
        // Validate password strength
        if (passwordTF.getText().length() < 6) {
            showErrorAlert("Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }
        
        return true;
    }
    

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d+");
    }
    

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void AfficherUtilisateur(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherUtilisateur.fxml"));
            nomTF.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    public void retourLogin(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            nomTF.getScene().setRoot(root);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
