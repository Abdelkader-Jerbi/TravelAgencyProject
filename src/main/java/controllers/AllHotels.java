package controllers;

import entities.Hotel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.CrudHotel;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

    public class AllHotels implements Initializable {

        @FXML
        private HBox hotelListContainer;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            CrudHotel hotelService = new CrudHotel();

            try {
                List<Hotel> hotels = hotelService.afficher();  // Correct method name

                for (Hotel hotel : hotels) {
                    ShowHotel hotelCard = new ShowHotel();
                    hotelCard.setHotelData(hotel);
                    hotelListContainer.getChildren().add(hotelCard);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Optionally show an alert to the user
            }
        }
    }


