package controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import utils.MyDatabase;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.net.URL;


public class LoginController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginErrorMsg;
    @FXML
    private ImageView brandingView;
    @FXML
    private ImageView lockView;
    @FXML
    private TextField emailUsername;
    @FXML
    private PasswordField passwordField;




    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // File brandingFile = new File("@../../Images/lastMinuteTravel.jpg");
        //  Image brandingImage = new Image(brandingFile.toURI().toString());
        try {
            Image brandingImage = new Image(getClass().getResource("/Images/lastMinuteTravel.jpg").toString());
            brandingView.setImage(brandingImage);
        } catch (NullPointerException e) {
            System.err.println("Image not found");
        }
        // File lockFile = new File("@../../Images/lastMinuteTravel.jpg");
        // Image lockImage = new Image(lockFile.toURI().toString());
        try {
            Image lockImage = new Image(getClass().getResource("/Images/lock.png").toString());
            lockView.setImage(lockImage);
        } catch (NullPointerException e) {
            System.err.println("Image not found");
        }

    }


    public void loginButtonAction(ActionEvent event) {
        loginErrorMsg.setText("Your credentials are incorrect. Please try again.");
        if(emailUsername.getText().isBlank() == false && passwordField.getText().isBlank() == false) {
            validateLogin();
        }else
            loginErrorMsg.setText("Your credentials are incorrect. Please try again.");
    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        String email = emailUsername.getText().trim();
        String password = passwordField.getText().trim();

        // Basic input validation
        if (email.isEmpty() || password.isEmpty()) {
            loginErrorMsg.setText("Please enter both email and password.");
            return;
        }

        // SQL query
        String verifyLogin = "SELECT COUNT(*) FROM utilisateur WHERE email = ? AND tel = ?";

        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {

            // Set query parameters
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password); // Consider hashing here

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) == 1) {
                loginErrorMsg.setText("Login successful!");
                 loadDashboard();
            } else {
                loginErrorMsg.setText("Invalid email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            loginErrorMsg.setText("An error occurred while connecting to the database.");
        }
    }

    private void loadDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) emailUsername.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
