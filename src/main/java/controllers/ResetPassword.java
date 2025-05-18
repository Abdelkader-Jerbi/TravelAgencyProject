package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ResetPassword {
    @FXML private TextField verifyCodeField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField confirmPassField;
    @FXML private Label verifyCodeStatusLabel;

    private String actualCode;
    private String userEmail;

    // Setters called from ForgotPasswordController
    public void setVerificationCode(String code) {
        this.actualCode = code;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    public void handleReset() {
        String enteredCode = verifyCodeField.getText();
        String newPassword = newPassField.getText();
        String confirmPassword = confirmPassField.getText();

        if (!enteredCode.equals(actualCode)) {
            verifyCodeStatusLabel.setText("Incorrect code.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            verifyCodeStatusLabel.setText("Passwords do not match.");
            return;
        }

        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String updateQuery = "UPDATE utilisateur SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, newPassword); // Optional: hash the password
            stmt.setString(2, userEmail);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                verifyCodeStatusLabel.setText("✅ Password updated!");
            } else {
                verifyCodeStatusLabel.setText("❌ Email not found in database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            verifyCodeStatusLabel.setText("Database error occurred.");
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.play();

        Stage currentStage = (Stage) verifyCodeStatusLabel.getScene().getWindow();
        currentStage.close();
    }
}
