

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainLayout.fxml"));
        Parent root = loader.load();
        
        // Obtenir le contrôleur principal

        // Configurer la scène
        Scene scene = new Scene(root);
        // Appliquer le CSS navbar-sidebar.css à la scène
        scene.getStylesheets().add(getClass().getResource("/navbar-sidebar.css").toExternalForm());
        primaryStage.setTitle("Travel Agency");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 