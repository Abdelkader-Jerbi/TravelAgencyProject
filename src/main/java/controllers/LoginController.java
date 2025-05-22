package controllers;


import entities.Session;
import entities.Utilisateur;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import services.CrudUtilisateur;
import utils.CaptchaBridge;
import utils.MyDatabase;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;


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
    @FXML
    private TextField showPasswordField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private WebView captchaWebView;

    private String captchaToken = null;
    private String userRole;
    private String userEmail;
    private String userPassword;




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

        // Initialize captcha WebView
        captchaWebView.setVisible(false);  // Hide captcha at start

        loadCaptcha();

    }


    public void loadCaptcha() {
        WebEngine engine = captchaWebView.getEngine();
        
        // Enable JavaScript console logging
        engine.setOnAlert(event -> System.out.println("JavaScript Alert: " + event));
        
        // Set up the bridge before loading the page
        CaptchaBridge bridge = new CaptchaBridge(token -> {
            captchaToken = token;
            System.out.println("Token from CAPTCHA: " + token);
            
            // Make sure UI updates happen on the JavaFX thread
            javafx.application.Platform.runLater(() -> {
                captchaWebView.setVisible(false);
                loginErrorMsg.setText("Captcha verified. Processing login..."); 
                proceedAfterCaptcha();
            });
        });

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("Captcha page loaded successfully");
                try {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaApp", bridge);
                    System.out.println("Java bridge object set to window.javaApp");
                    
                    // Test the bridge
                    engine.executeScript(
                        "console.log('Testing bridge availability: ' + " +
                        "(typeof javaApp !== 'undefined' ? 'Bridge available' : 'Bridge NOT available'));"
                    );
                } catch (Exception e) {
                    System.err.println("Error setting up JavaScript bridge: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Failed to load captcha page");
                Throwable exception = engine.getLoadWorker().getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });

        // Load local captcha.html file
        //File captchaFile = new File("src/main/resources/ReCaptcha.html");
        engine.load("http://localhost:8000/ReCaptcha.html");
    }

    public void proceedAfterCaptcha() {
        if (captchaToken == null || captchaToken.isEmpty()) {
            loginErrorMsg.setText("Please complete the CAPTCHA.");
            return;
        }

        Utilisateur user = Session.getLoggedInUser();

        if (user == null) {
            loginErrorMsg.setText("Session expired. Please log in again.");
            return;
        }

        if ("USER".equalsIgnoreCase(user.getRole().name())) {
            loadDashboard();
        } else if ("ADMIN".equalsIgnoreCase(user.getRole().name())) {
            loadDashboardAdmin();
        } else {
            loginErrorMsg.setText("Unknown role.");
        }
        captchaToken = null;
    }



    public void loginButtonAction(ActionEvent event) {
            loginErrorMsg.setText("");

            if (emailUsername.getText().isBlank() || passwordField.getText().isBlank()) {
                loginErrorMsg.setText("Please enter your email and password.");
                return;
            }

            validateLogin();
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
        String verifyLogin = "SELECT COUNT(*) FROM utilisateur WHERE email = ? AND password = ?";
        String roleQuery = "SELECT role FROM utilisateur WHERE email = ?";
        CrudUtilisateur crudUtilisateur = new CrudUtilisateur();

        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {

            // Set query parameters
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password); // Consider hashing here

            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next() && resultSet.getInt(1) == 1) {
                loginErrorMsg.setText("Login successful!");
                PreparedStatement roleStatement = connection.prepareStatement(roleQuery);
                roleStatement.setString(1, email);
                ResultSet roleResult = roleStatement.executeQuery();

                if (roleResult.next()) {
                    Utilisateur user = crudUtilisateur.getUserFromDatabase(email, password);
                    if (user != null) {
                        Session.setLoggedInUser(user); // save full user in session
                        userRole = user.getRole().name(); // get ENUM as string
                        captchaWebView.setVisible(true); // show captcha
                        loginErrorMsg.setText("Credentials valid. Please solve CAPTCHA.");
                    } else {
                        loginErrorMsg.setText("Error retrieving user details.");
                    }

                    loginErrorMsg.setText("Credentials valid. Please solve CAPTCHA.");
                    captchaToken = null;
                    captchaWebView.setVisible(true); // Show CAPTCHA

                }

            } else {
                loginErrorMsg.setText("Invalid email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            loginErrorMsg.setText("An error occurred while connecting to the database.");
        }
    }

    public void showPassword() {
        if (showPasswordCheckBox.isSelected()) {
            showPasswordField.setText(passwordField.getText());
            showPasswordField.setVisible(true);
            showPasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(showPasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            showPasswordField.setVisible(false);
            showPasswordField.setManaged(false);
        }
    }

    public void registerUser(ActionEvent Event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterUtilisateur.fxml"));
            Stage stage = (Stage)((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void loadDashboard() {
        try {
            System.out.println("Loading user dashboard...");
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
            stage.show();

            ((Stage) emailUsername.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            loginErrorMsg.setText("Error loading user dashboard.");
        }
    }


    private void loadDashboardAdmin() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/View/hoteltable.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) emailUsername.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
