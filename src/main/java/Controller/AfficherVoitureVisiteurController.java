package Controller;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CrudVoiture;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AfficherVoitureVisiteurController {

    @FXML private TilePane voitureTilePane;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> brandFilter;
    @FXML private Slider priceFilter;
    @FXML private Button searchButton2;

    private final CrudVoiture crudVoiture = new CrudVoiture();
    private List<Voiture> masterList = new ArrayList<>();

    @FXML
    public void initialize() {
        loadVoitures();
        setupBrandFilter();
        searchField.textProperty().addListener((obs, old, neu) -> applyFilter());
        searchButton.setOnAction(e -> applyFilter());
        searchButton2.setOnAction(e -> applyFilter());
    }

    private void loadVoitures() {
        try {
            masterList = crudVoiture.getAllVoitures();
            applyFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBrandFilter() {
        Set<String> marques = masterList.stream()
                .map(Voiture::getMarque)
                .collect(Collectors.toSet());
        brandFilter.getItems().setAll(marques);
        brandFilter.setPromptText("Marque");
    }

    private void applyFilter() {
        String text = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String brand = brandFilter.getValue();
        double maxPrice = priceFilter.getValue();

        List<Voiture> filtered = masterList.stream()
                .filter(v -> (text.isEmpty() || v.getMarque().toLowerCase().contains(text)
                        || v.getModele().toLowerCase().contains(text)
                        || v.getMatricule().toLowerCase().contains(text)))
                .filter(v -> (brand == null || brand.isEmpty() || v.getMarque().equals(brand)))
                .filter(v -> v.getPrixParJour() <= maxPrice)
                .filter(Voiture::isDisponible)
                .collect(Collectors.toList());

        voitureTilePane.getChildren().clear();
        for (Voiture v : filtered) {
            voitureTilePane.getChildren().add(createVoitureCard(v));
        }
    }

    private VBox createVoitureCard(Voiture voiture) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; " +
                "-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefWidth(180);

        ImageView iv = new ImageView();
        try {
            String path = voiture.getImagePath(); // ex: "images/pannier.png"
            URL imgUrl = getClass().getResource("/" + path);
            if (imgUrl != null) {
                Image img = new Image(imgUrl.toExternalForm());
                iv.setImage(img);
                iv.setFitWidth(160);
                iv.setFitHeight(100);
                iv.setPreserveRatio(true);
            } else {
                System.err.println("Image introuvable: " + path);
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + voiture.getImagePath());
        }

        Label marqueLbl = new Label("Marque: " + voiture.getMarque());
        Label matriculeLbl = new Label("Matricule: " + voiture.getMatricule());
        Label prixLbl = new Label("Prix: " + voiture.getPrixParJour() + " DT/jour");

        Button voirBtn = new Button("Détails");
        voirBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
        voirBtn.setOnAction(e -> ouvrirFenetreDetail(voiture.getId()));

        card.getChildren().addAll(iv, marqueLbl, matriculeLbl, prixLbl, voirBtn);
        return card;
    }

    private void ouvrirFenetreDetail(int voitureId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsVoitureVisiteur.fxml"));
            Parent root = loader.load();

            DetailVoitureController controller = loader.getController();
            controller.setVoitureId(voitureId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détail de la voiture");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
