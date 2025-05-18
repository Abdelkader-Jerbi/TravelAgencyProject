package controllers;

import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.EmailSender;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    private TextField EmailForgetPasswordField;

    @FXML
    private Button ButtonSendCodeForgotPass;

    @FXML
    private Label statusLabel;

    private String verificationCode;
    private String userEmail;
    
    @FXML
    public void handleCancel() {
        // Close the current window
        Stage stage = (Stage) EmailForgetPasswordField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSendCode() {
        String email = EmailForgetPasswordField.getText();

        if (email == null || email.isEmpty()) {
            statusLabel.setText("Please enter your email.");
            return;
        }

        String code = generateVerificationCode();

        try {
            EmailSender.sendEmail(email, code);
            statusLabel.setText("Verification code sent!");

            // Save the code for Step 3
            this.verificationCode = code;
            this.userEmail = email;
        } catch (MessagingException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to send email.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            ResetPassword controller = loader.getController();
            controller.setVerificationCode(verificationCode);
            controller.setUserEmail(userEmail);

            Stage stage = new Stage();
            stage.setTitle("Verify Code");
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit code
        return String.valueOf(code);
    }




}
