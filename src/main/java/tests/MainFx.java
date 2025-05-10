package tests;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import utils.DatabaseConnectionPool;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Main JavaFX application class.
 * This class is responsible for creating and managing the user interface.
 */
public class MainFx extends Application {

    private Stage primaryStage;
    private static final String CSS_BUTTON_NORMAL = 
        "-fx-background-color: white;" +
        "-fx-text-fill: #1976d2;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-border-color: #1976d2;" +
        "-fx-border-width: 2px;" +
        "-fx-border-radius: 5px;" +
        "-fx-background-radius: 5px;" +
        "-fx-cursor: hand;";
        
    private static final String CSS_BUTTON_HOVER = 
        "-fx-background-color: #1976d2;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-border-color: #1976d2;" +
        "-fx-border-width: 2px;" +
        "-fx-border-radius: 5px;" +
        "-fx-background-radius: 5px;" +
        "-fx-cursor: hand;";

    /**
     * Main method to launch the application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize database connection pool in background
        CompletableFuture.runAsync(() -> {
            try {
                // Initialize the connection pool
                DatabaseConnectionPool.getInstance();
                System.out.println("Database connection pool initialized successfully");
            } catch (Exception e) {
                System.err.println("Error initializing database connection pool: " + e.getMessage());
                e.printStackTrace();
                
                // Show error UI on JavaFX thread
                Platform.runLater(() -> showDatabaseErrorUI(primaryStage, e.getMessage()));
            }
        });
        
        // Show the main menu
        showMainMenu();
        
        // Set up close handler
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application closing, cleaning up resources...");
            Platform.exit();
        });
    }
    
    /**
     * Show an error UI when the database connection fails
     * 
     * @param stage The stage to show the UI on
     * @param errorMessage The error message to display
     */
    private void showDatabaseErrorUI(Stage stage, String errorMessage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setStyle("-fx-background-color: #f44336;");
        
        Label title = new Label("Erreur de Connexion à la Base de Données");
        title.setFont(Font.font(24));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        
        Label errorMsg = new Label("Impossible de se connecter à la base de données.");
        errorMsg.setFont(Font.font(18));
        errorMsg.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        
        Label detailsMsg = new Label("Erreur: " + errorMessage);
        detailsMsg.setFont(Font.font(14));
        detailsMsg.setStyle("-fx-text-fill: #555;");
        detailsMsg.setWrapText(true);
        
        Label infoMsg = new Label("Vérifiez que le serveur MySQL est en cours d'exécution et que les paramètres de connexion sont corrects.");
        infoMsg.setFont(Font.font(14));
        infoMsg.setWrapText(true);
        
        Button retryButton = new Button("Réessayer");
        retryButton.setStyle(CSS_BUTTON_NORMAL);
        retryButton.setOnAction(e -> {
            // Retry database connection
            CompletableFuture.runAsync(() -> {
                try {
                    DatabaseConnectionPool.getInstance();
                    System.out.println("Database connection pool initialized successfully on retry");
                    
                    // Show main menu on success
                    Platform.runLater(this::showMainMenu);
                } catch (Exception ex) {
                    System.err.println("Error initializing database connection pool on retry: " + ex.getMessage());
                    ex.printStackTrace();
                    
                    // Update error message
                    Platform.runLater(() -> detailsMsg.setText("Erreur: " + ex.getMessage()));
                }
            });
        });
        
        content.getChildren().addAll(errorMsg, detailsMsg, infoMsg, retryButton);
        root.setCenter(content);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Erreur de Connexion - Travel Agency");
        
        // Make the window responsive
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // If the stage is not showing, show it
        if (!stage.isShowing()) {
            stage.show();
        }
    }
    
    /**
     * Show a fallback UI when FXML loading fails
     * 
     * @param stage The stage to show the UI on
     */
    private void showFallbackUI(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setStyle("-fx-background-color: #1976d2;");
        
        Label title = new Label("Gestion des Réservations");
        title.setFont(Font.font(24));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        
        Label errorMsg = new Label("Impossible de charger l'interface FXML.");
        errorMsg.setFont(Font.font(18));
        errorMsg.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        
        Label infoMsg = new Label("Vérifiez que les fichiers FXML sont présents dans le répertoire resources.");
        infoMsg.setFont(Font.font(14));
        
        Button backButton = new Button("Retour au Menu Principal");
        backButton.setStyle(CSS_BUTTON_NORMAL);
        backButton.setOnAction(e -> showMainMenu());
        
        content.getChildren().addAll(errorMsg, infoMsg, backButton);
        root.setCenter(content);
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Erreur - Gestion des Réservations");
        
        // Make the window responsive
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // If the stage is not showing, show it
        if (!stage.isShowing()) {
            stage.show();
        }
    }
    
    /**
     * Show the main menu
     */
    private void showMainMenu() {
        // Create main container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Create header
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setStyle("-fx-background-color: #1976d2;");
        header.prefWidthProperty().bind(root.widthProperty()); // Make header width match the root width
        
        Label title = new Label("Travel Agency Management");
        title.setFont(Font.font(24));
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);
        
        // Create menu options
        VBox menuContainer = new VBox(20);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setPadding(new Insets(50));
        // Make the menu container grow with the window
        menuContainer.setFillWidth(true);
        
        Label menuTitle = new Label("Sélectionnez une interface");
        menuTitle.setFont(Font.font(18));
        menuTitle.setStyle("-fx-font-weight: bold;");
        
        Button panierBtn = createMenuButton("Gestion de Panier", "/Gestion_Panier_Responsive.fxml", "Gestion de Panier");
        Button reservationBtn = createMenuButton("Gestion des Réservations", "/Gestion_Reservations_Responsive.fxml", "Gestion des Réservations");
        
        menuContainer.getChildren().addAll(menuTitle, panierBtn, reservationBtn);
        root.setCenter(menuContainer);
        
        // Create footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #0d47a1;");
        footer.prefWidthProperty().bind(root.widthProperty()); // Make footer width match the root width
        
        Label footerText = new Label("© 2024 Travel Agency - Tous droits réservés");
        footerText.setStyle("-fx-text-fill: white;");
        
        footer.getChildren().add(footerText);
        root.setBottom(footer);
        
        // Set scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Travel Agency Management");
        
        // Make the window responsive
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Enable window maximization
        primaryStage.setMaximized(true);
        
        // If the stage is not showing, show it
        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }
    
    /**
     * Create a menu button with consistent styling
     * 
     * @param text The button text
     * @param fxmlPath The path to the FXML file to load
     * @param title The title for the window
     * @return The styled button
     */
    private Button createMenuButton(String text, String fxmlPath, String title) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(60);
        button.setMaxWidth(Double.MAX_VALUE); // Allow button to grow horizontally
        button.setStyle(CSS_BUTTON_NORMAL);
        
        button.setOnMouseEntered(e -> button.setStyle(CSS_BUTTON_HOVER));
        button.setOnMouseExited(e -> button.setStyle(CSS_BUTTON_NORMAL));
        button.setOnAction(e -> loadInterface(fxmlPath, title));
        
        return button;
    }
    
    /**
     * Load an interface from an FXML file
     * 
     * @param fxmlPath The path to the FXML file
     * @param title The title for the window
     */
    private void loadInterface(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Create a back button
            Button backButton = new Button("Retour au Menu");
            backButton.setStyle(
                "-fx-background-color: #f5f7fa;" +
                "-fx-text-fill: #1976d2;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            );
            backButton.setOnAction(e -> showMainMenu());
            
            // Add back button to the scene
            if (root instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) root;
                HBox topContainer = new HBox(backButton);
                topContainer.setPadding(new Insets(10, 0, 0, 10));
                topContainer.prefWidthProperty().bind(borderPane.widthProperty()); // Make container width match the border pane width
                
                if (borderPane.getTop() != null) {
                    VBox newTop = new VBox(topContainer, borderPane.getTop());
                    borderPane.setTop(newTop);
                } else {
                    borderPane.setTop(topContainer);
                }
            }
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            
            // Ensure the window is responsive
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'interface: " + fxmlPath);
            showFallbackUI(primaryStage);
        }
    }
    
    @Override
    public void stop() {
        System.out.println("JavaFX application stopping...");
    }
}
