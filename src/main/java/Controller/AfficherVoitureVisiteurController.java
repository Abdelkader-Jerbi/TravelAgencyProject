package Controller;

import entities.Voiture;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.CrudVoiture;
import Controller.ReservationVoitureController;
import Controller.ThemeManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherVoitureVisiteurController {

    @FXML private VBox voitureListVBox;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private HBox navBar; // Already added via FXML, ensure it's here
    @FXML private StackPane cartIconContainer;
    @FXML private Label cartItemCountLabel;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    // Filtres avancés
    @FXML private TextField modeleField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<String> carburantCombo;
    @FXML private ComboBox<String> boiteVitesseCombo;
    @FXML private TextField couleurField;
    @FXML private CheckBox climatisationCheckBox;
    @FXML private Spinner<Integer> nbPlacesSpinner;
    @FXML private Spinner<Integer> nbPortesSpinner;
    @FXML private TextField anneeField;
    @FXML private Slider prixSlider;
    @FXML private Slider kmSlider;
    @FXML private Label prixValueLabel;
    @FXML private Label kmValueLabel;
    @FXML private Button resetFiltersBtn;
    @FXML private TilePane voitureTilePane;
    @FXML private ComboBox<String> themeComboBox;

    @FXML private VBox sidebar;

    @FXML private ScrollPane voitureListScrollPane;

    private final CrudVoiture crudVoiture = new CrudVoiture();
    private List<Voiture> masterList = new ArrayList<>();
    private int currentCartItems = 0;
    private boolean darkMode = false;

    // Map des gouvernorats et leurs villes
    private static final Map<String, List<String>> gouvernoratVilles = Map.of(
            "Tunis", List.of("Tunis", "La Marsa", "Le Bardo", "Carthage", "Le Kram", "Sidi Bou Said", "El Menzah", "El Omrane", "El Mourouj"),
            "Ariana", List.of("Ariana", "Raoued", "La Soukra", "Ettadhamen", "Mnihla", "Kalâat el-Andalous"),
            "Ben Arous", List.of("Ben Arous", "Hammam Lif", "Hammam Chott", "Mégrine", "Radès", "Mornag"),
            "Manouba", List.of("Manouba", "Douar Hicher", "Oued Ellil", "Tebourba", "Borj El Amri"),
            "Nabeul", List.of("Nabeul", "Hammamet", "Kélibia", "Korba", "Menzel Temime", "Dar Chaâbane"),
            "Sousse", List.of("Sousse", "Hammam Sousse", "Msaken", "Akouda", "Kalaâ Kebira"),
            "Monastir", List.of("Monastir", "Moknine", "Bekalta", "Ksar Hellal", "Ksibet el-Médiouni"),
            "Mahdia", List.of("Mahdia", "Souassi", "El Jem", "Bou Merdes", "Chebba"),
            "Sfax", List.of("Sfax", "Sakiet Ezzit", "Sakiet Eddaïer", "Gremda", "El Ain"),
            "Gabès", List.of("Gabès", "Métouia", "El Hamma", "Matmata", "Ghannouch")
    );

    @FXML
    public void initialize() {
        // Initialiser les listes déroulantes
        initializeComboBoxes();

        // Initialiser les spinners
        initializeSpinners();

        // Initialiser les sliders
        initializeSliders();

        // Initialize Cart display
        updateCartCount(ReservationVoitureController.getGlobalCartCount());
        if (cartIconContainer != null) {
            cartIconContainer.setOnMouseClicked(event -> {
                System.out.println("Cart icon clicked! Implement navigation to reservation/cart page.");
                // TODO: Implement navigation to the cart/reservation validation page
            });
        }

        // Charger les véhicules
        loadVoitures();

        // Ajouter les listeners
        addListeners();

        // Dans initialize(), ajoute :
        if (themeComboBox != null) {
            themeComboBox.getItems().addAll("Mode normal", "Mode sombre");
            themeComboBox.setValue(darkMode ? "Mode sombre" : "Mode normal");
        }
    }

    private void initializeComboBoxes() {
        // Catégories
        categorieCombo.getItems().addAll("Citadine", "Berline", "SUV", "Break", "Coupé", "Cabriolet", "4x4");

        // Carburants
        carburantCombo.getItems().addAll("Essence", "Diesel", "Électrique", "Hybride", "GPL");

        // Boîtes de vitesse
        boiteVitesseCombo.getItems().addAll("Manuelle", "Automatique", "Semi-automatique");
    }

    private void initializeSpinners() {
        // Nombre de places (1-9)
        nbPlacesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 4));

        // Nombre de portes (2-6)
        nbPortesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 6, 4));
    }

    private void initializeSliders() {
        // Prix slider
        prixSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            prixValueLabel.setText(String.format("%.0f TND", newVal.doubleValue()));
        });

        // Km slider
        kmSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            kmValueLabel.setText(String.format("%.0f km", newVal.doubleValue()));
        });
    }

    private void addListeners() {
        // Recherche rapide
        searchField.textProperty().addListener((obs, old, neu) -> applyFilter());

        // Boutons
        searchButton.setOnAction(e -> applyFilter());
        resetFiltersBtn.setOnAction(e -> applyFilter());

        // Filtres avancés
        modeleField.textProperty().addListener((obs, old, neu) -> applyFilter());
        categorieCombo.valueProperty().addListener((obs, old, neu) -> applyFilter());
        carburantCombo.valueProperty().addListener((obs, old, neu) -> applyFilter());
        boiteVitesseCombo.valueProperty().addListener((obs, old, neu) -> applyFilter());
        couleurField.textProperty().addListener((obs, old, neu) -> applyFilter());
        climatisationCheckBox.selectedProperty().addListener((obs, old, neu) -> applyFilter());
        nbPlacesSpinner.valueProperty().addListener((obs, old, neu) -> applyFilter());
        nbPortesSpinner.valueProperty().addListener((obs, old, neu) -> applyFilter());
        anneeField.textProperty().addListener((obs, old, neu) -> applyFilter());
        prixSlider.valueProperty().addListener((obs, old, neu) -> applyFilter());
        kmSlider.valueProperty().addListener((obs, old, neu) -> applyFilter());
    }

    private void loadVoitures() {
        try {
            List<Voiture> allVoitures = crudVoiture.getAllVoitures();
            System.out.println("loadVoitures: Fetched " + (allVoitures != null ? allVoitures.size() : "null") + " cars from DB.");
            if (allVoitures != null && !allVoitures.isEmpty()) {
                allVoitures.forEach(v -> System.out.println("DB car: " + v.getMarque() + " " + v.getModele() + ", Img: " + v.getImagePath() + ", Disponible: " + v.isDisponible()));
            }

            // Filter to keep only available cars
            if (allVoitures != null) {
                masterList = allVoitures.stream()
                        .filter(Voiture::isDisponible)
                        .collect(Collectors.toList());
            } else {
                masterList = new ArrayList<>(); // Initialize to empty list if allVoitures is null
            }

            System.out.println("loadVoitures: masterList (available cars) size = " + masterList.size());
            if (!masterList.isEmpty()) {
                masterList.forEach(v -> System.out.println("Available car in masterList: " + v.getMarque() + " " + v.getModele() + ", Img: " + v.getImagePath()));
            }

            applyFilter();
        } catch (SQLException e) { // More specific exception
            System.err.println("SQL Error in loadVoitures: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Catch other potential exceptions
            System.err.println("Generic Error in loadVoitures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilter() {
        System.out.println("applyFilter: called");
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String modele = modeleField.getText() != null ? modeleField.getText().toLowerCase().trim() : "";
        String categorie = categorieCombo.getValue();
        String carburant = carburantCombo.getValue();
        String boiteVitesse = boiteVitesseCombo.getValue();
        String couleur = couleurField.getText() != null ? couleurField.getText().toLowerCase().trim() : "";
        boolean climatisation = climatisationCheckBox.isSelected();
        int nbPlaces = nbPlacesSpinner.getValue();
        int nbPortes = nbPortesSpinner.getValue();
        int annee = 0;
        try {
            annee = Integer.parseInt(anneeField.getText());
        } catch (NumberFormatException e) {
            // Ignorer si le champ est vide ou invalide
        }
        final int anneeFinal = annee;
        double prixMax = prixSlider.getValue();
        double kmMax = kmSlider.getValue();

        boolean allDefault =
            (searchText.isEmpty() &&
            (modele.isEmpty()) &&
            (categorie == null || categorie.isEmpty()) &&
            (carburant == null || carburant.isEmpty()) &&
            (boiteVitesse == null || boiteVitesse.isEmpty()) &&
            (couleur.isEmpty()) &&
            !climatisation &&
            nbPlaces == 4 &&
            nbPortes == 4 &&
            anneeFinal == 0 &&
            prixMax == prixSlider.getMax() &&
            kmMax == kmSlider.getMax());

        System.out.println("masterList size = " + (masterList != null ? masterList.size() : "null"));
        System.out.println("Filtrage: nbPlaces=" + nbPlaces + ", nbPortes=" + nbPortes + ", annee=" + anneeFinal + ", prixMax=" + prixMax + ", kmMax=" + kmMax);

        if (allDefault) {
            voitureListVBox.getChildren().clear();
            for (Voiture v : masterList) {
                voitureListVBox.getChildren().add(createVoitureCard(v));
            }
            System.out.println("Tous les filtres sont par défaut, affichage de toute la liste (" + masterList.size() + " voitures)");
            return;
        }

        List<Voiture> filtered = masterList.stream()
                .filter(Voiture::isDisponible)
                .filter(v -> searchText.isEmpty() ||
                        (v.getMarque() != null && v.getMarque().toLowerCase().contains(searchText)) ||
                        (v.getModele() != null && v.getModele().toLowerCase().contains(searchText)) ||
                        (v.getMatricule() != null && v.getMatricule().toLowerCase().contains(searchText)) ||
                        (v.getCouleur() != null && v.getCouleur().toLowerCase().contains(searchText)))
                .filter(v -> modele.isEmpty() ||
                        (v.getModele() != null && v.getModele().toLowerCase().contains(modele)))
                .filter(v -> categorie == null || categorie.isEmpty() ||
                        (v.getCategorie() != null && v.getCategorie().equalsIgnoreCase(categorie)))
                .filter(v -> carburant == null || carburant.isEmpty() ||
                        (v.getCarburant() != null && v.getCarburant().equalsIgnoreCase(carburant)))
                .filter(v -> boiteVitesse == null || boiteVitesse.isEmpty() ||
                        (v.getBoiteVitesse() != null && v.getBoiteVitesse().equalsIgnoreCase(boiteVitesse)))
                .filter(v -> couleur.isEmpty() ||
                        (v.getCouleur() != null && v.getCouleur().toLowerCase().contains(couleur)))
                // Ne filtre la climatisation que si la case est cochée
                .filter(v -> !climatisation || v.isClimatisation())
                // N'applique PAS de filtre sur nbPlaces si valeur par défaut (4)
                .filter(v -> (nbPlaces == 4) || v.getNbPlaces() == nbPlaces)
                // N'applique PAS de filtre sur nbPortes si valeur par défaut (4)
                .filter(v -> (nbPortes == 4) || v.getNbPortes() == nbPortes)
                .filter(v -> anneeFinal == 0 || v.getAnnee() == anneeFinal)
                .filter(v -> v.getPrixParJour() <= prixMax)
                .filter(v -> v.getKilometrage() <= kmMax)
                .collect(java.util.stream.Collectors.toList());
        System.out.println("applyFilter: filtered list size = " + filtered.size());

        voitureListVBox.getChildren().clear();
        if (filtered.isEmpty()) {
            System.out.println("applyFilter: No cars to display after filtering.");
            Label msg = new Label("Aucune voiture disponible pour le moment.");
            msg.setStyle("-fx-font-size: 16px; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-padding: 30;");
            voitureListVBox.getChildren().add(msg);
            return;
        }
        for (Voiture v : filtered) {
            voitureListVBox.getChildren().add(createVoitureCard(v));
        }

        // Si darkMode, applique dark-card à chaque VBox enfant de voitureListVBox
        if (darkMode) {
            for (Node node : voitureListVBox.getChildren()) {
                if (node instanceof VBox) node.getStyleClass().add("dark-card");
            }
        }
    }

    private VBox createVoitureCard(Voiture voiture) {
        // 1. Root VBox for the card
        VBox cardRoot = new VBox(10);
        cardRoot.setStyle("-fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 6, 0, 0, 4);");
        cardRoot.setPrefWidth(680); // Adjust as needed to fit within the ScrollPane
        cardRoot.setMaxWidth(VBox.USE_PREF_SIZE);

        // 2. HBox for (Image, MainInfo, PriceBox)
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.CENTER_LEFT);

        // 2.1. ImageView
        ImageView voitureImage = new ImageView();
        voitureImage.setFitWidth(200);
        voitureImage.setFitHeight(120);
        voitureImage.setPreserveRatio(true);

        try {
            String imagePath = voiture.getImagePath();
            System.out.println("createVoitureCard: Attempting to load image for " + voiture.getMarque() + " " + voiture.getModele() + " from path: [" + imagePath + "]");
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                // Ensure the path is absolute or correctly relative to where the app expects it.
                // JavaFX Image constructor with "file:" prefix expects an absolute path or a path relative to the application's root.
                File imgFile = new File(imagePath);
                String fullPath = imgFile.getAbsolutePath();
                System.out.println("createVoitureCard: Absolute image path: " + fullPath);

                Image img = new Image("file:" + fullPath.replace("\\", "/"), 200, 120, true, true);

                if (img.isError()) {
                    System.err.println("Error loading image: " + fullPath + " - " + (img.getException() != null ? img.getException().getMessage() : "Unknown error"));
                    // Optionally set a placeholder image, e.g., from resources
                    // InputStream placeholderStream = getClass().getResourceAsStream("/images/placeholder.png");
                    // if (placeholderStream != null) voitureImage.setImage(new Image(placeholderStream));
                } else {
                    voitureImage.setImage(img);
                    System.out.println("createVoitureCard: Image loaded successfully for " + fullPath);
                }
            } else {
                System.err.println("Image path is null or empty for voiture ID: " + voiture.getId() + " (" + voiture.getMarque() + " " + voiture.getModele() + ")");
                // Optionally set a placeholder image
            }
        } catch (Exception e) {
            System.err.println("Exception loading image for " + voiture.getMarque() + " " + voiture.getModele() + " (Path: " + voiture.getImagePath() + "): " + e.getMessage());
            e.printStackTrace();
            // Optionally set a placeholder image
        }

        // 2.2. VBox for MainInfo (Badges, Name, SubName, GridPane Details, Location)
        VBox mainInfoVBox = new VBox(6);
        HBox.setHgrow(mainInfoVBox, Priority.ALWAYS);

        // 2.2.1. HBox for Badges ("Coup de cœur", "Idéal pour les familles")
        HBox badgesHBox = new HBox(8);
        Label badgeCoupDeCoeur = new Label("Coup de cœur");
        badgeCoupDeCoeur.setStyle("-fx-background-color: #003b95; -fx-text-fill: white; -fx-padding: 2 6 2 6; -fx-font-size: 10px; -fx-background-radius: 4;");
        Label badgeFamilles = new Label("Idéal pour les familles");
        badgeFamilles.setStyle("-fx-background-color: #003b95; -fx-text-fill: white; -fx-padding: 2 6 2 6; -fx-font-size: 10px; -fx-background-radius: 4;");
        badgesHBox.getChildren().addAll(badgeCoupDeCoeur, badgeFamilles);

        // 2.2.2. Label for Car Name
        Label nomVoitureLabel = new Label(voiture.getMarque() + " " + voiture.getModele());
        nomVoitureLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 2.2.3. Label for SubName
        Label subNameLabel = new Label("ou voiture moyenne similaire");
        subNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // 2.2.4. GridPane for Details
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(5);
        detailsGrid.setStyle("-fx-padding: 5 0 5 0;");

        ColumnConstraints col1 = new ColumnConstraints(); col1.setHgrow(Priority.SOMETIMES);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setHgrow(Priority.SOMETIMES);
        detailsGrid.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < 3; i++) {
            RowConstraints row = new RowConstraints(); /*row.setMinHeight(10.0);*/ row.setPrefHeight(25.0); row.setVgrow(Priority.SOMETIMES);
            detailsGrid.getRowConstraints().add(row);
        }

        Label seatsLabel = new Label("🚗 " + voiture.getNbPlaces() + " sièges");
        seatsLabel.setStyle("-fx-font-size: 12px;");
        detailsGrid.add(seatsLabel, 0, 0);

        Label luggageLargeLabel = new Label("🧳 1 grande valise"); // Static
        luggageLargeLabel.setStyle("-fx-font-size: 12px;");
        detailsGrid.add(luggageLargeLabel, 0, 1);

        Label mileageLabel = new Label("🛣️ Kilométrage illimité"); // Static as per FXML/image
        mileageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        detailsGrid.add(mileageLabel, 0, 2);

        Label transmissionLabel = new Label("⚙️ " + (voiture.getBoiteVitesse() != null ? voiture.getBoiteVitesse() : "N/A"));
        transmissionLabel.setStyle("-fx-font-size: 12px;");
        detailsGrid.add(transmissionLabel, 1, 0);

        Label luggageSmallLabel = new Label("🧳 1 petite valise"); // Static
        luggageSmallLabel.setStyle("-fx-font-size: 12px;");
        detailsGrid.add(luggageSmallLabel, 1, 1);
        // Row 2, Col 1 is empty

        // 2.2.5. Label for Location
        Label locationLabel = new Label("Tunis Aéroport - Dans le terminal"); // Static for now
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #003b95;");

        mainInfoVBox.getChildren().addAll(badgesHBox, nomVoitureLabel, subNameLabel, detailsGrid, locationLabel);

        // 2.3. VBox for Price & Actions
        VBox priceActionsVBox = new VBox(10);
        priceActionsVBox.setAlignment(Pos.CENTER_RIGHT);

        Label prixPourLabel = new Label("Prix Par jour :");
        prixPourLabel.setStyle("-fx-font-size: 12px;");

        Label prixLabel = new Label(String.format("%.0f TND", voiture.getPrixParJour() * 3));
        prixLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        Label annulationLabel = new Label("Annulation gratuite");
        annulationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");

        Button voirOffreButton = new Button("Voir l'offre");
        voirOffreButton.setStyle("-fx-background-color: #006ce4; -fx-text-fill: white; -fx-padding: 6 12 6 12; -fx-font-size: 13px; -fx-background-radius: 4; -fx-cursor: hand;");
        voirOffreButton.setOnAction(e -> {
            // Ouvre uniquement la fenêtre de détails, sans toucher au panier
            ouvrirFenetreDetail(voiture.getId());
        });

        priceActionsVBox.getChildren().addAll(prixPourLabel, prixLabel, annulationLabel, voirOffreButton);
        topSection.getChildren().addAll(voitureImage, mainInfoVBox, priceActionsVBox);

        // 3. Separator
        Separator separator = new Separator();
        // separator.setPrefWidth(200.0); // Let VBox manage width

        // 4. HBox for Footer
        HBox footerSection = new HBox(10);
        footerSection.setAlignment(Pos.CENTER_LEFT);
        footerSection.setStyle("-fx-padding: 8 0 0 0;");

        // 4.1. HBox for Budget/Rating
        HBox budgetRatingHBox = new HBox(5);
        budgetRatingHBox.setAlignment(Pos.CENTER_LEFT);
   ///     Label budgetTextLabel = new Label("Budget"); // Changed variable name to avoid conflict
    ///    budgetTextLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 12px;");
        Label ratingScoreLabel = new Label("7.8"); // Static
        ratingScoreLabel.setStyle("-fx-background-color: #003b95; -fx-text-fill: white; -fx-padding: 2 5; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 3;");

        VBox ratingTextVBox = new VBox(0);
        ratingTextVBox.setStyle("-fx-padding: 0 0 0 3px;");
        Label ratingDescLabel = new Label("Bien"); // Static
        ratingDescLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        Label avisCountLabel = new Label("400+ avis"); // Static, changed variable name
        avisCountLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        ratingTextVBox.getChildren().addAll(ratingDescLabel, avisCountLabel);
    ///    budgetRatingHBox.getChildren().addAll(budgetTextLabel, ratingScoreLabel, ratingTextVBox);

        // 4.2. Pane for spacer
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 4.3. Label "ⓘ Infos importantes"
        Label infosImportantesLabel = new Label("ⓘ Infos importantes");
        infosImportantesLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #006ce4; -fx-cursor: hand;");
        infosImportantesLabel.setTooltip(new Tooltip("Afficher les informations importantes"));
        // infosImportantesLabel.setOnMouseClicked(e -> {/* Action */});

        // 4.4. Label "✉ Envoyer le devis par e-mail"
        Label envoyerDevisLabel = new Label("✉ Envoyer le devis par e-mail");
        envoyerDevisLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #006ce4; -fx-cursor: hand;");
        envoyerDevisLabel.setTooltip(new Tooltip("Envoyer le devis par e-mail"));
        // envoyerDevisLabel.setOnMouseClicked(e -> {/* Action */});

        footerSection.getChildren().addAll(budgetRatingHBox, spacer, infosImportantesLabel, envoyerDevisLabel);

        cardRoot.getChildren().addAll(topSection, separator, footerSection);

        if (darkMode) cardRoot.getStyleClass().add("dark-card");

        return cardRoot;
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
            // Appliquer le thème courant
            if (ThemeManager.isDarkMode()) {
                root.getStyleClass().add("dark-root");
            } else {
                root.getStyleClass().remove("dark-root");
            }
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetFilters() {
        System.out.println("Bouton réinitialiser cliqué !");
        // Recherche rapide
        if (searchField != null) searchField.clear();
        // Champs texte
        if (modeleField != null) modeleField.clear();
        if (couleurField != null) couleurField.clear();
        if (anneeField != null) anneeField.clear();
        // ComboBox
        if (categorieCombo != null) categorieCombo.getSelectionModel().clearSelection();
        if (carburantCombo != null) carburantCombo.getSelectionModel().clearSelection();
        if (boiteVitesseCombo != null) boiteVitesseCombo.getSelectionModel().clearSelection();
        // CheckBox
        if (climatisationCheckBox != null) climatisationCheckBox.setSelected(false);
        // Spinners
        if (nbPlacesSpinner != null && nbPlacesSpinner.getValueFactory() != null) nbPlacesSpinner.getValueFactory().setValue(4);
        if (nbPortesSpinner != null && nbPortesSpinner.getValueFactory() != null) nbPortesSpinner.getValueFactory().setValue(4);
        // Sliders
        if (prixSlider != null) prixSlider.setValue(prixSlider.getMax());
        if (kmSlider != null) kmSlider.setValue(kmSlider.getMax());
        // Appliquer le filtre
        applyFilter();
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) voitureListVBox.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizeWindow() {
        Stage stage = (Stage) voitureListVBox.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) voitureListVBox.getScene().getWindow();
        stage.close();
    }

    private void updateCartCount(int count) {
        if (cartItemCountLabel != null) {
            if (count > 0) {
                cartItemCountLabel.setText(String.valueOf(count));
                cartItemCountLabel.setVisible(true);
            } else {
                cartItemCountLabel.setVisible(false);
            }
        }
    }

    @FXML
    private void refreshPage() {
        System.out.println("Actualisation de la page demandée");
        resetFilters(); // Réinitialise tous les filtres
        loadVoitures(); // Recharge la liste depuis la base de données
    }

    @FXML
    private void toggleTheme() {
        Scene scene = voitureListVBox.getScene();
        if (scene == null) return;
        Parent root = scene.getRoot();
        boolean toDark = !darkMode;
        Controller.ThemeManager.setDarkMode(toDark);
        if (toDark) {
            root.getStyleClass().add("dark-root");
            voitureListVBox.getStyleClass().add("dark-main-bg");
            sidebar.getStyleClass().add("dark-sidebar");
            navBar.getStyleClass().add("dark-nav-bar");
            if (voitureListScrollPane != null) {
                voitureListScrollPane.getStyleClass().add("dark-scroll-pane");
            }
            for (Node node : voitureListVBox.getChildren()) {
                if (node instanceof VBox) node.getStyleClass().add("dark-card");
            }
            themeComboBox.setValue("Mode sombre");
            darkMode = true;
        } else {
            root.getStyleClass().remove("dark-root");
            voitureListVBox.getStyleClass().remove("dark-main-bg");
            sidebar.getStyleClass().remove("dark-sidebar");
            navBar.getStyleClass().remove("dark-nav-bar");
            if (voitureListScrollPane != null) {
                voitureListScrollPane.getStyleClass().remove("dark-scroll-pane");
            }
            for (Node node : voitureListVBox.getChildren()) {
                if (node instanceof VBox) node.getStyleClass().remove("dark-card");
            }
            themeComboBox.setValue("Mode normal");
            darkMode = false;
        }
        applyThemeToAllWindows();
    }

    private void applyThemeToAllWindows() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Scene scene = ((Stage) window).getScene();
                if (scene != null) {
                    Parent root = scene.getRoot();
                    if (Controller.ThemeManager.isDarkMode()) {
                        if (!root.getStyleClass().contains("dark-root")) {
                            root.getStyleClass().add("dark-root");
                        }
                    } else {
                        root.getStyleClass().remove("dark-root");
                    }
                }
            }
        }
    }

    @FXML
    private void onThemeChange() {
        String selected = themeComboBox.getValue();
        if ("Mode sombre".equals(selected)) {
            if (!darkMode) {
                toggleTheme();
            }
        } else if ("Mode normal".equals(selected)) {
            if (darkMode) {
                toggleTheme();
            }
        }
    }

    public void refreshCartCount() {
        updateCartCount(ReservationVoitureController.getGlobalCartCount());
    }
}

