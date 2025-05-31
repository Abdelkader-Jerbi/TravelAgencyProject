package controllers;

import entities.Hotel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CrudHotel;
import services.EmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ProgressIndicator;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Hyperlink;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;

// Imports for HTTP client and JSON processing


public class AllHotels implements Initializable {

    @FXML private FlowPane hotelGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> categoryFilterComboBox;

    @FXML private Label resultsCount;
    @FXML private StackPane loadingIndicator;
    @FXML private ComboBox<String> languageComboBox;

    private List<Hotel> allHotels;
    private List<Hotel> currentDisplayedHotels;
    private boolean isTranslated = false;
    private String currentLanguageCode = "en";

    private final int COLUMNS = 3;
    private static final String IMAGES_DIRECTORY = "/View/images/";
    private static final String DEFAULT_IMAGE = "default-hotel.jpg";
    // Fallback directory for file system loading if resource loading fails
    private static final String FILE_SYSTEM_IMAGES_DIR = "src/main/resources/View/images/";
    private Map<String, Image> imageCache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGridLayout();
        setupFilterComboBox();
        setupCategoryFilterComboBox();

        checkImagesDirectory(); // Check if images directory exists
        loadHotels();
        setupSearchListener();
    }

    private void checkImagesDirectory() {
        // Check if the default image exists in resources
        URL defaultImageUrl = getClass().getResource(IMAGES_DIRECTORY + DEFAULT_IMAGE);
        if (defaultImageUrl == null) {
            System.err.println("WARNING: Default image not found in resources at " + IMAGES_DIRECTORY + DEFAULT_IMAGE);
        } else {
            System.out.println("Default image found in resources at: " + defaultImageUrl);
        }

        // Check if the images directory exists in resources
        URL imagesDir = getClass().getResource(IMAGES_DIRECTORY);
        if (imagesDir == null) {
            System.err.println("WARNING: Images directory not found in resources at " + IMAGES_DIRECTORY);
        } else {
            System.out.println("Images directory found in resources at: " + imagesDir);
        }

        // Check file system paths
        java.io.File fileSystemImagesDir = new java.io.File(FILE_SYSTEM_IMAGES_DIR);
        if (!fileSystemImagesDir.exists() || !fileSystemImagesDir.isDirectory()) {
            System.err.println("WARNING: File system images directory not found at " + FILE_SYSTEM_IMAGES_DIR);
        } else {
            System.out.println("File system images directory found at: " + fileSystemImagesDir.getAbsolutePath());

            // Check if default image exists in file system
            java.io.File defaultImageFile = new java.io.File(FILE_SYSTEM_IMAGES_DIR + DEFAULT_IMAGE);
            if (!defaultImageFile.exists() || !defaultImageFile.isFile()) {
                System.err.println("WARNING: Default image not found in file system at " + defaultImageFile.getAbsolutePath());
            } else {
                System.out.println("Default image found in file system at: " + defaultImageFile.getAbsolutePath());
            }

            // List all files in the images directory
            System.out.println("Files in images directory:");
            java.io.File[] files = fileSystemImagesDir.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    System.out.println(" - " + file.getName());
                }
            }
        }
    }

    private void setupGridLayout() {
        // Set FlowPane properties
        hotelGrid.setHgap(20);
        hotelGrid.setVgap(20);
        hotelGrid.setPadding(new Insets(20));
        hotelGrid.setStyle("-fx-background-color: transparent;");
        hotelGrid.setPrefWrapLength(900.0); // This will be the width at which cards wrap to next line
    }

    private void setupFilterComboBox() {
        ObservableList<String> ratings = FXCollections.observableArrayList(
            "Toutes les évaluations",
            "5 étoiles",
            "4 étoiles",
            "3 étoiles",
            "2 étoiles",
            "1 étoile"
        );
        filterComboBox.setItems(ratings);
        filterComboBox.setValue("Toutes les évaluations");
        filterComboBox.setOnAction(e -> filterHotels());
    }

    private void setupCategoryFilterComboBox() {
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Toutes les catégories",
            "Luxe",
            "Famille",
            "Economique",
            "Affaires"
        );
        categoryFilterComboBox.setItems(categories);
        categoryFilterComboBox.setValue("Toutes les catégories");
        categoryFilterComboBox.setOnAction(e -> filterHotels());
    }


    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterHotels());
    }

    private void loadHotels() {
        showLoading(true);
        CrudHotel hotelService = new CrudHotel();

        try {
            allHotels = hotelService.afficher();
            if (allHotels != null) {
                currentDisplayedHotels = new ArrayList<>(allHotels); // Initialize currentDisplayedHotels
                displayHotels(allHotels);
                updateResultsCount(allHotels.size());
            } else {
                showError("Error", "No hotels found in the database");
            }
        } catch (SQLException e) {
            showError("Database Error", "Could not load hotels: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "An unexpected error occurred: " + e.getMessage());
        } finally {
            showLoading(false);
        }
    }

    private void filterHotels() {
        if (allHotels == null) return;

        final String searchText = searchField.getText().toLowerCase();
        final String selectedRating = filterComboBox.getValue();
        final String selectedCategory = categoryFilterComboBox.getValue();


        List<Hotel> filtered = allHotels.stream()
                .filter(hotel -> {
                    boolean matchesSearch = hotel.getNom().toLowerCase().contains(searchText) ||
                            hotel.getLocalisation().toLowerCase().contains(searchText);

                    boolean matchesRating = selectedRating.equals("Toutes les évaluations") ||
                            (selectedRating.equals("5 étoiles") && hotel.getNbEtoile() == 5) ||
                            (selectedRating.equals("4 étoiles") && hotel.getNbEtoile() == 4) ||
                            (selectedRating.equals("3 étoiles") && hotel.getNbEtoile() == 3) ||
                            (selectedRating.equals("2 étoiles") && hotel.getNbEtoile() == 2) ||
                            (selectedRating.equals("1 étoile") && hotel.getNbEtoile() == 1);

                    boolean matchesCategory = selectedCategory.equals("Toutes les catégories") ||
                            (hotel.getCategorieType() != null && hotel.getCategorieType().equals(selectedCategory));

                    return matchesSearch && matchesRating && matchesCategory;
                })
                .collect(Collectors.toList());

        currentDisplayedHotels = filtered;
        displayHotels(filtered);
        updateResultsCount(filtered.size());
    }

    private void displayHotels(List<Hotel> hotelsToDisplay) {
        hotelGrid.getChildren().clear();

        for (Hotel hotel : hotelsToDisplay) {
            final VBox hotelCard = createHotelCard(hotel);
            hotelCard.setMaxWidth(300); // Set a fixed maximum width for each card
            hotelCard.setMinWidth(300);
            hotelGrid.getChildren().add(hotelCard);
        }
    }

    private VBox createHotelCard(Hotel hotel) {
        VBox card = new VBox(0);
        card.getStyleClass().add("hotel-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinWidth(0);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2); -fx-border-radius: 15; -fx-background-radius: 15;");

        // Hotel Image Container
        BorderPane imageContainer = new BorderPane();
        imageContainer.getStyleClass().add("hotel-image-container");
        imageContainer.setMaxHeight(200);
        imageContainer.setMinHeight(200);
        imageContainer.setPrefHeight(200);
        imageContainer.setMaxWidth(Double.MAX_VALUE);
        imageContainer.setMinWidth(0);
        imageContainer.setStyle("-fx-border-radius: 10 10 0 0; -fx-background-radius: 10 10 0 0;");

        // Loading indicator
        ProgressIndicator loadingSpinner = new ProgressIndicator();
        loadingSpinner.setMaxSize(40, 40);
        loadingSpinner.setStyle("-fx-progress-color: #3498db;");

        // Image view for the hotel image
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(imageContainer.widthProperty());
        imageView.fitHeightProperty().bind(imageContainer.heightProperty());
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.getStyleClass().add("hotel-image");
        imageView.setStyle("-fx-background-color: transparent;");

        // Center the loading spinner initially
        imageContainer.setCenter(loadingSpinner);

        // Load image asynchronously
        loadHotelImage(hotel, imageView, loadingSpinner);

        // Hotel Info
        VBox info = new VBox(8);
        info.getStyleClass().add("hotel-info");
        info.setPadding(new Insets(15));
        info.setMaxWidth(Double.MAX_VALUE);
        info.setMinWidth(0);
        info.setStyle("-fx-background-color: white; -fx-border-radius: 0 0 10 10; -fx-background-radius: 0 0 10 10;");

        // Hotel Name
        Label nameLabel = new Label(hotel.getNom());
        nameLabel.getStyleClass().add("hotel-name");
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        // Location
        Label locationLabel = new Label(hotel.getLocalisation());
        locationLabel.getStyleClass().add("hotel-location");
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        locationLabel.setWrapText(true);
        locationLabel.setMaxWidth(Double.MAX_VALUE);

        // Category
        Label categoryLabel = new Label("Catégorie: " + hotel.getCategorieType());
        categoryLabel.getStyleClass().add("hotel-category");
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        categoryLabel.setWrapText(true);
        categoryLabel.setMaxWidth(Double.MAX_VALUE);

        // Rating
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label ratingLabel = new Label("★".repeat(hotel.getNbEtoile()));
        ratingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f1c40f;");
        Label ratingText = new Label(hotel.getNbEtoile() + " Etoiles");
        ratingText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        ratingBox.getChildren().addAll(ratingLabel, ratingText);

        // Price
        Label priceLabel = new Label(String.format("%.2f DT", hotel.getTarif()));
        priceLabel.getStyleClass().add("hotel-price");
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        // Buttons Container
        HBox buttonsContainer = new HBox(10);
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        buttonsContainer.setPadding(new Insets(10, 0, 0, 0));

        // Book Button
        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("book-button");
        bookButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");
        bookButton.setOnAction(e -> handleBooking(hotel));

        // Details Link
        Hyperlink detailsLink = new Hyperlink("View Details");
        detailsLink.setStyle("-fx-text-fill: #3498db; -fx-underline: true; -fx-cursor: hand;");
        detailsLink.setOnAction(event -> openHotelDetails(hotel));

        // Add HBox to push Book Now button to the right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttonsContainer.getChildren().addAll(detailsLink, spacer, bookButton);

        // Add all elements to the info VBox
        info.getChildren().addAll(nameLabel, locationLabel, categoryLabel, ratingBox, priceLabel, buttonsContainer);

        // Add image container and info to the card
        card.getChildren().addAll(imageContainer, info);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 3); -fx-border-radius: 15; -fx-background-radius: 15; -fx-translate-y: -2;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2); -fx-border-radius: 15; -fx-background-radius: 15; -fx-translate-y: 0;"));

        // Apply clipping to the image container for rounded corners
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imageContainer.widthProperty());
        clip.heightProperty().bind(imageContainer.heightProperty());
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageContainer.setClip(clip);

        return card;
    }

    private void openHotelDetails(Hotel hotel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/hoteldetails.fxml"));
            Parent root = loader.load();

            HotelDetails controller = loader.getController();
            controller.setHotel(hotel);

            Stage stage = new Stage();
            stage.setTitle("Hotel Details - " + hotel.getNom());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error", "Could not open hotel details: " + e.getMessage());
        }
    }

    private void loadHotelImage(Hotel hotel, ImageView imageView, ProgressIndicator loadingSpinner) {
        if (hotel == null || imageView == null) return;

        // Get image filename from hotel, or use default if not available
        final String imageFilename = hotel.getImage() != null && !hotel.getImage().trim().isEmpty()
            ? new java.io.File(hotel.getImage().trim()).getName()
            : DEFAULT_IMAGE;

        // Create the full resource path
        final String resourcePath = IMAGES_DIRECTORY + imageFilename;

        // Check cache first
        if (imageCache.containsKey(resourcePath)) {
            final Image cachedImage = imageCache.get(resourcePath);
            Platform.runLater(() -> {
                imageView.setImage(cachedImage);
                // Replace spinner with image
                BorderPane parent = (BorderPane) loadingSpinner.getParent();
                if (parent != null) {
                    parent.setCenter(imageView);
                }
                loadingSpinner.setVisible(false);
            });
            return;
        }

        // Load image asynchronously
        new Thread(() -> {
            try {
                System.out.println("Loading image from: " + resourcePath);

                // Try to load the image from resources first
                Image image = null;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    String imageUrlString = imageUrl.toExternalForm();
                    System.out.println("Loading from resource URL: " + imageUrlString);
                    image = new Image(imageUrlString, true); // true for background loading
                }

                // Try loading from file system if resource loading failed
                if (image == null || image.isError()) {
                    try {
                        String fileSystemPath = new java.io.File(FILE_SYSTEM_IMAGES_DIR + imageFilename).getAbsolutePath();
                        String fileUrl = "file:" + fileSystemPath;
                        System.out.println("Trying to load from file system: " + fileUrl);
                        image = new Image(fileUrl, true);
                    } catch (Exception e) {
                        System.err.println("Failed to load from file system: " + e.getMessage());
                    }
                }

                // If image still couldn't be loaded, try the default image
                if (image == null || image.isError()) {
                    System.out.println("Trying default image: " + IMAGES_DIRECTORY + DEFAULT_IMAGE);

                    // Try from resources
                    URL defaultUrl = getClass().getResource(IMAGES_DIRECTORY + DEFAULT_IMAGE);
                    if (defaultUrl != null) {
                        String defaultUrlString = defaultUrl.toExternalForm();
                        System.out.println("Loading default from resource URL: " + defaultUrlString);
                        image = new Image(defaultUrlString, true);
                    } else {
                        System.err.println("Default image URL is null, trying file system");

                        // Try from file system
                        try {
                            String fileSystemDefaultPath = new java.io.File(FILE_SYSTEM_IMAGES_DIR + DEFAULT_IMAGE).getAbsolutePath();
                            String fileSystemDefaultUrl = "file:" + fileSystemDefaultPath;
                            System.out.println("Trying default from file system: " + fileSystemDefaultUrl);
                            image = new Image(fileSystemDefaultUrl, true);
                        } catch (Exception e) {
                            System.err.println("Failed to load default from file system: " + e.getMessage());
                        }
                    }
                }

                final Image finalImage = image;

                Platform.runLater(() -> {
                    if (finalImage != null && !finalImage.isError()) {
                        // Cache the image
                        imageCache.put(resourcePath, finalImage);

                        // Set the image
                        imageView.setImage(finalImage);

                        // Replace spinner with image
                        BorderPane parent = (BorderPane) loadingSpinner.getParent();
                        if (parent != null) {
                            parent.setCenter(imageView);
                        }
                    } else {
                        System.err.println("Failed to load image: " + resourcePath);
                        // Set a placeholder background color
                        imageView.setStyle("-fx-background-color: #e0e0e0;");
                        // Add a placeholder text
                        Label placeholderLabel = new Label("No Image");
                        placeholderLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");
                        BorderPane parent = (BorderPane) loadingSpinner.getParent();
                        if (parent != null) {
                            parent.setCenter(placeholderLabel);
                        }
                    }
                    loadingSpinner.setVisible(false);
                });
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> {
                    imageView.setStyle("-fx-background-color: #e0e0e0;");
                    // Add a placeholder text
                    Label placeholderLabel = new Label("No Image");
                    placeholderLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");
                    BorderPane parent = (BorderPane) loadingSpinner.getParent();
                    if (parent != null) {
                        parent.setCenter(placeholderLabel);
                    }
                    loadingSpinner.setVisible(false);
                });
            }
        }).start();
    }

    private void handleBooking(Hotel hotel) {
        try {
            // Save the reservation using CrudHotel
            CrudHotel crudHotel = new CrudHotel();
            crudHotel.addReservation(hotel.getIdHotel());

            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Confirmed");
            alert.setHeaderText(null);
            
            // Create the content with styled labels
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20; -fx-background-color: white;");
            
            // Title
            Label titleLabel = new Label("Booking Confirmed!");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
            
            // Hotel info
            Label hotelInfoLabel = new Label(String.format("Your booking for %s in %s has been confirmed!", 
                hotel.getNom(), hotel.getLocalisation()));
            hotelInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            
            // Price box
            VBox priceBox = new VBox(5);
            priceBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 5;");
            Label priceLabel = new Label(String.format("Price: %.2f DT", hotel.getTarif()));
            priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            priceBox.getChildren().add(priceLabel);
            
            // Email message
            Label emailLabel = new Label("A confirmation email has been sent to your email address.");
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            
            // Add all elements to the content
            content.getChildren().addAll(titleLabel, hotelInfoLabel, priceBox, emailLabel);
            
            // Style the dialog pane
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white;");
            dialogPane.getStyleClass().add("custom-dialog");
            
            // Set the content
            dialogPane.setContent(content);
            
            // Add custom stylesheet
            dialogPane.getStylesheets().add(getClass().getResource("/View/css/style.css").toExternalForm());
            
            // Send confirmation email
            String userEmail = "barranifatma18@gmail.com"; // Recipient's email address
            EmailService.sendBookingConfirmation(
                userEmail,
                hotel.getNom(),
                hotel.getLocalisation(),
                hotel.getTarif(),
                hotel.getChambre(),
                hotel.getNbEtoile()
            );
            
            alert.showAndWait();
        } catch (SQLException e) {
            showError("Database Error", "Could not save the reservation: " + e.getMessage());
        }
    }

    private void updateResultsCount(int count) {
        resultsCount.setText(String.format("Found %d hotel%s", count, count == 1 ? "" : "s"));
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void handleReturnToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePage.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage currentStage = (Stage) hotelGrid.getScene().getWindow();
            
            // Create new scene and set it
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            showError("Navigation Error", "Could not return to home page: " + e.getMessage());
        }
    }
}
