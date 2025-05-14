package DetailVoitureController;

import com.example.DetailVoitureController;
import entities.Voiture;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CrudVoiture;

import java.io.IOException;
import java.util.List;

public class AfficherVoitureVisiteurController {

    @FXML
    private TilePane voitureTilePane;

    private final CrudVoiture crudVoiture = new CrudVoiture();

    @FXML
    public void initialize() {
        loadVoitures();
    }

    private void loadVoitures() {
        try {
            List<Voiture> voitures = crudVoiture.getAllVoitures();

            for (Voiture voiture : voitures) {
                VBox card = createVoitureCard(voiture);
                voitureTilePane.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des voitures : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createVoitureCard(Voiture voiture) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefWidth(180);

        // Image de la voiture
        ImageView imageView = new ImageView();
        try {
            String imagePath = voiture.getImagePath(); // Assurez-vous que le chemin est correct
            Image image = new Image(getClass().getResource("/" + imagePath).toExternalForm());
            imageView.setImage(image);
            imageView.setFitWidth(160);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Erreur de chargement image : " + voiture.getImagePath());
        }

        // Informations sur la voiture
        Label marqueLabel = new Label("Marque : " + voiture.getMarque());
        Label matriculeLabel = new Label("Matricule : " + voiture.getMatricule());
        Label prixLabel = new Label("Prix : " + voiture.getPrixParJour() + " DT/jour");

        // Bouton "Voir"
        Button voirBtn = new Button("Details");
        voirBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
       voirBtn.setOnAction(e -> ouvrirFenetreDetail(voiture));

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, marqueLabel, matriculeLabel, prixLabel, voirBtn);
        return card;
    }

    private void ouvrirFenetreDetail(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsVoitureVisiteur.fxml"));
            Parent root = loader.load();

            // Passer l'ID de la voiture
            DetailVoitureController controller = loader.getController();
            controller.wait(voiture.getId());

            Stage stage = new Stage();
            stage.setTitle("Détails de la voiture");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre détail : " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
