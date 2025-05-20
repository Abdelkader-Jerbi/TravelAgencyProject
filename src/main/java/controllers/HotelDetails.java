package controllers;

import entities.Hotel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.application.Platform;
import services.GeocodingService;
import services.WeatherService;
import services.GeocodingService.Coordinates;
import services.WeatherService.WeatherData;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HotelDetails {
    @FXML private Label hotelNameLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label roomTypeLabel;
    @FXML private Label starsLabel;
    @FXML private Label priceLabel;
    @FXML private TextArea descriptionTextArea;
    @FXML private ImageView hotelImageView;

    // FXML elements for weather information
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescriptionLabel;
    @FXML private ImageView weatherIconView;

    private Hotel hotel;

    private static final String IMAGES_DIRECTORY = "/View/images/";
    private static final String FILE_SYSTEM_IMAGES_DIR = "src/main/resources/View/images/";
    private static final String DEFAULT_IMAGE = "default-hotel.jpg";

    private GeocodingService geocodingService;
    private WeatherService weatherService;
    private ExecutorService executorService;

    public void initialize() {
        geocodingService = new GeocodingService();
        weatherService = new WeatherService();
        executorService = Executors.newCachedThreadPool();

        // Shutdown executor service when the window is closed
        // This requires access to the Stage, which is not available in initialize.
        // We'll handle this in the setHotel method by adding a listener to the stage.
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        if (hotel != null) {
            hotelNameLabel.setText(hotel.getNom());
            locationLabel.setText(hotel.getLocalisation());



            roomTypeLabel.setText(hotel.getChambre());
            starsLabel.setText("★".repeat(hotel.getNbEtoile()));
            priceLabel.setText(String.format("%.2f €", hotel.getTarif()));
            descriptionTextArea.setText(hotel.getDescription());

            // Load hotel image
            loadImage(hotel.getImage(), hotelImageView);

            // --- Fetch and display weather information ---
            temperatureLabel.setText("Loading weather data...");
            weatherDescriptionLabel.setText("Please wait...");
            weatherIconView.setImage(null); // Clear previous icon

            executorService.submit(() -> {
                try {
                    String location = hotel.getLocalisation();
                    if (location == null || location.trim().isEmpty()) {
                        Platform.runLater(() -> {
                            temperatureLabel.setText("Location not available");
                            weatherDescriptionLabel.setText("Hotel location information is missing");
                            weatherIconView.setImage(null);
                        });
                        return;
                    }

                    System.out.println("Processing location: " + location);
                    // Create a new string for the cleaned location
                    final String cleanedLocation = location.trim().replaceAll("[^a-zA-Z0-9\\s,.-]", "");

                    System.out.println("Attempting to get coordinates for location: " + cleanedLocation);
                    Coordinates coordinates = geocodingService.getCoordinates(cleanedLocation);
                    
                    if (coordinates != null) {
                        System.out.println("Successfully got coordinates for " + cleanedLocation);
                        System.out.println("Latitude: " + coordinates.getLatitude() + ", Longitude: " + coordinates.getLongitude());
                        
                        WeatherData weatherData = weatherService.fetchWeatherData(coordinates.getLatitude(), coordinates.getLongitude());
                        Platform.runLater(() -> {
                            if (weatherData != null) {
                                temperatureLabel.setText(String.format("Temperature: %.1f°C", weatherData.getTemperature()));
                                weatherDescriptionLabel.setText("Conditions: " + weatherData.getWeatherDescription());
                                // Load and set weather icon
                                loadWeatherIcon(weatherData.getWeatherDescription());
                            } else {
                                temperatureLabel.setText("Weather data not available");
                                weatherDescriptionLabel.setText("Could not fetch weather information for " + cleanedLocation);
                                weatherIconView.setImage(null);
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            temperatureLabel.setText("Location not found");
                            weatherDescriptionLabel.setText("Could not find coordinates for: " + cleanedLocation);
                            weatherIconView.setImage(null);
                        });
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        temperatureLabel.setText("Error fetching weather");
                        weatherDescriptionLabel.setText("Please check your internet connection");
                        weatherIconView.setImage(null);
                    });
                }
            });
            // -------------------------------------------
        }
    }

    private void loadImage(String imagePath, ImageView imageView) {
        Image image = null;
        boolean loaded = false;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                // Try loading from resources first (assuming path is relative to resources/View/images)
                String resourcePath = IMAGES_DIRECTORY + new File(imagePath).getName();
                URL imageUrl = getClass().getResource(resourcePath);
                if (imageUrl != null) {
                    image = new Image(imageUrl.toExternalForm());
                    loaded = true;
                } else {
                    // If not found in resources, try loading directly from the file system if the path is absolute or relative to project root
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                        loaded = true;
                    } else {
                         // Try loading from the fallback file system directory
                         File fallbackFile = new File(FILE_SYSTEM_IMAGES_DIR + new File(imagePath).getName());
                         if(fallbackFile.exists()){
                             image = new Image(fallbackFile.toURI().toString());
                             loaded = true;
                         }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            }
        }

        // If image couldn't be loaded, use default image
        if (!loaded) {
            try {
                 URL defaultUrl = getClass().getResource(IMAGES_DIRECTORY + DEFAULT_IMAGE);
                 if (defaultUrl != null) {
                    image = new Image(defaultUrl.toExternalForm());
                 } else {
                     // Try loading default from file system as a last resort
                     File defaultFile = new File(FILE_SYSTEM_IMAGES_DIR + DEFAULT_IMAGE);
                      if(defaultFile.exists()){
                         image = new Image(defaultFile.toURI().toString());
                      }
                 }
            } catch (Exception e) {
                System.err.println("Error loading default image: " + DEFAULT_IMAGE + " - " + e.getMessage());
            }
        }

        if (image != null) {
            imageView.setImage(image);
        } else {
            // Set a placeholder if no image can be loaded
            System.err.println("Failed to load any image for path: " + imagePath);
            imageView.setImage(null); // Clear any previous image
            // Optionally, set a background color or a placeholder image here
        }
    }

    private void loadWeatherIcon(String weatherDescription) {
        String iconFileName = "default.png"; // Default icon

        if (weatherDescription != null) {
            String lowerCaseDescription = weatherDescription.toLowerCase();
            if (lowerCaseDescription.contains("clear")) {
                iconFileName = "clear.png";
            } else if (lowerCaseDescription.contains("cloudy")) {
                iconFileName = "cloudy.png";
            } else if (lowerCaseDescription.contains("rain")) {
                iconFileName = "rain.png";
            } else if (lowerCaseDescription.contains("snow")) {
                iconFileName = "snow.png";
            } else if (lowerCaseDescription.contains("thunderstorm")) {
                iconFileName = "thunderstorm.png";
            } else if (lowerCaseDescription.contains("fog")) {
                iconFileName = "fog.png";
            }
        }

        String iconPath = "/View/icons/weather/" + iconFileName;
        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                Image iconImage = new Image(iconUrl.toExternalForm());
                weatherIconView.setImage(iconImage);
            } else {
                System.err.println("Weather icon not found at: " + iconPath);
                weatherIconView.setImage(null); // Clear icon if not found
            }
        } catch (Exception e) {
            System.err.println("Error loading weather icon: " + e.getMessage());
            weatherIconView.setImage(null); // Clear icon on error
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        shutdownExecutorService(); // Shutdown when the window is closed
    }

    private void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // Attempt to stop executing tasks
        }
    }
} 