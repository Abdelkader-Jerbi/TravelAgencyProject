package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import entities.Hotel;

import java.io.IOException;

public class ShowHotel extends VBox {

    @FXML private ImageView hotelImageView;
    @FXML private Label nameLabel;
    @FXML private Label ratingLabel;
    @FXML private Label locationLabel;
    @FXML private Label priceLabel;
    @FXML private Button bookButton;


    private Hotel hotel; // ✅ Fix 1: Declare hotel field

    public ShowHotel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/ShowHotel.fxml"));
        fxmlLoader.setRoot(this); // Set the root to this instance (ShowHotel)
        fxmlLoader.setController(this); // Set the controller to this instance (ShowHotel)

        try {
            fxmlLoader.load(); // Load the FXML file
        } catch (IOException e) {
            throw new RuntimeException(e); // Handle loading errors
        }
    }

    public void setHotelData(Hotel hotel) {
        this.hotel = hotel;

        nameLabel.setText(hotel.getNom());
        ratingLabel.setText(getStarRating(hotel.getNbEtoile()));
        locationLabel.setText(hotel.getLocalisation());
        priceLabel.setText(String.format("$%.2f / night", hotel.getTarif()));

        if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(hotel.getImageUrl(), true);
                image.progressProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() == 1.0) {
                        hotelImageView.setImage(image);
                    }
                });
            } catch (Exception e) {
                hotelImageView.setImage(new Image(getClass().getResourceAsStream("/Images/photo1.jpg")));
            }
        } else {
            hotelImageView.setImage(new Image(getClass().getResourceAsStream("/Images/photo2.jpg")));
        }
        bookButton.setOnAction(e -> handleBookAction());
    }

    @FXML
    private void handleBookAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/BookingForm.fxml"));
            Parent root = loader.load();



            Stage stage = new Stage();
            stage.setTitle("Booking Form");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading BookingForm.fxml:");
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Unexpected error:");
            ex.printStackTrace();
        }
    }

    private String getStarRating(int rating) {
        return "★".repeat(Math.max(0, rating)) + "☆".repeat(Math.max(0, 5 - rating));
    }
}
