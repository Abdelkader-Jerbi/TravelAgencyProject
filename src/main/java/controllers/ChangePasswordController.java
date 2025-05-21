package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private String userEmail;

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    @FXML
    public void handleCancel() {
        // Close the current window
        Stage stage = (Stage) currentPasswordField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSave() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All fields are required");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("New passwords do not match");
            return;
        }

        // Verify current password
        if (!verifyCurrentPassword(userEmail, currentPassword)) {
            statusLabel.setText("Current password is incorrect");
            return;
        }

        // Update password
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String updateQuery = "UPDATE utilisateur SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, newPassword); // In a real app, you should hash the password
            stmt.setString(2, userEmail);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                statusLabel.setText("✅ Password updated successfully!");
                
                // Clear the fields
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
                
                // Close the window after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            Stage stage = (Stage) statusLabel.getScene().getWindow();
                            stage.close();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                statusLabel.setText("❌ Failed to update password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error occurred");
        }
    }

    private boolean verifyCurrentPassword(String email, String password) {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String query = "SELECT * FROM utilisateur WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password); // In a real app, you would compare hashed passwords
            
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If a row is returned, the password is correct
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}