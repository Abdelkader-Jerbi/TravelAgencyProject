package controllers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entities.Enumnom;
import entities.StatutVol;
import entities.Vol;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Popup;
import services.CrudVol;
import services.VoLInterface;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Bounds;

public class ListVolController implements Initializable {
    @FXML
    private TableView<Vol> volTable;

    @FXML
    private TableColumn<Vol, String> departCol;

    @FXML
    private TableColumn<Vol, String> destinationCol;

    @FXML
    private TableColumn<Vol, String> dateCol;

    @FXML
    private TableColumn<Vol, String> dateRetourCol;

    @FXML
    private TableColumn<Vol, String> prixFinalCol;

    @FXML
    private TableColumn<Vol, Double> prixCol;

    @FXML
    private TableColumn<Vol, String> categorieCol;
    @FXML
    private TableColumn<Vol, Void> actionCol;

    @FXML
    private DatePicker dateAllerDebutPicker;
    @FXML
    private DatePicker dateRetourPicker;
    @FXML
    private ComboBox<String> destinationField;
    @FXML
    private ComboBox<String> departField;
    @FXML
    private ComboBox<String> categorieCombo;
    @FXML
    private TextField prixMaxField;
    @FXML
    private Pagination pagination;
    @FXML
    private TableColumn<Vol, StatutVol> statutCol;
    @FXML
    private TableColumn<Vol, String> colEnPromotion;

    @FXML
    private TableColumn<Vol, Double> colPourcentage;

    private List<Vol> volsAffiches = FXCollections.observableArrayList();

    // la taille d'une page
    private static final int ROWS_PER_PAGE = 5;

    private VoLInterface volService = new CrudVol();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL = "https://restcountries.com/v3.1/name/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger la liste des pays
        loadCountries();
        
        // Configuration de l'autocomplétion pour le champ destination
        destinationField.setEditable(true);
        destinationField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                filterComboBox(destinationField, newValue);
            }
        });

        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colEnPromotion.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getEnpromotion();
            if (value == null) value = "Hors Promotion"; // au cas où
            return new SimpleStringProperty(value);
        });
        colPourcentage.setCellValueFactory(new PropertyValueFactory<>("pourcentagePromotion"));

        categorieCombo.setItems(FXCollections.observableArrayList(
                Arrays.stream(Enumnom.values())
                        .map(Enum::name)
                        .collect(Collectors.toList())
        ));

        departCol.setCellValueFactory(new PropertyValueFactory<>("depart"));
        dateRetourCol.setCellValueFactory(cellData -> {
            Date retourDate = cellData.getValue().getDateRetour();
            String formattedRetour = retourDate != null
                    ? new SimpleDateFormat("yyyy-MM-dd").format(retourDate)
                    : "—";
            return new SimpleStringProperty(formattedRetour);
        });

        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateCol.setCellValueFactory(cellData -> {
            // On formate la date en String
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd")
                    .format(cellData.getValue().getDate());
            return new SimpleStringProperty(formattedDate);
        });
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        categorieCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCategorie().getNom().toString()
                )
        );
        prixFinalCol.setCellValueFactory(cellData ->
        {
            double prixFinal = volService.calculerPrixFinal(cellData.getValue());
            String prixFormate = String.format("%.2f DT", prixFinal);
            return new SimpleStringProperty(prixFormate);
        });

        afficherVols();
        ajouterBoutonSuppression();
    }
    private void afficherVols() {
        List<Vol> vols = volService.getAllVols();
        ObservableList<Vol> observableVols = FXCollections.observableArrayList(vols);
        volTable.setItems(observableVols);
        volsAffiches = vols;
        System.out.println(volsAffiches.size());
        setupPagination();
    }
    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) volsAffiches.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, volsAffiches.size());

        List<Vol> pageData = volsAffiches.subList(fromIndex, toIndex);
        volTable.setItems(FXCollections.observableArrayList(pageData));

        // Utilise un conteneur pour que JavaFX redessine correctement la page
        VBox tableContainer = new VBox(volTable);
        return tableContainer;
    }



    private void ajouterBoutonSuppression() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton= new Button();  ;
            private final Button editButton = new Button(" Modifier");
            private final HBox buttonBox = new HBox(10);
            {
                ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("/icones/delete.png"))
                );
                icon.setFitHeight(16);
                icon.setFitWidth(16);
                deleteButton.setText(" Supprimer");
                deleteButton.setGraphic(icon);
                deleteButton.setContentDisplay(ContentDisplay.RIGHT);

                deleteButton.setStyle("-fx-background-color: #ff5c5c; -fx-text-fill: white; -fx-background-radius: 15;");


                deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right,  #ff7f7f, #e74c3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                );
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #e74c3c,  #ff7f7f);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0.0, 0, 2);"
                ));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #ff7f7f,#e74c3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                ));

                deleteButton.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());


                    volService.supprimerVol(vol.getId_vol());
                    getTableView().getItems().remove(vol);
                });
                ImageView editIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("/icones/icons8-edit-30.png")) // à créer/ajouter dans resources
                );
                editIcon.setFitHeight(16);
                editIcon.setFitWidth(16);
                editButton.setGraphic(editIcon);
                editButton.setContentDisplay(ContentDisplay.RIGHT);
                editButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);" +
                                "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;" +
                                "-fx-cursor: hand; -fx-padding: 5 10;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 2);"
                );

                editButton.setOnAction(event -> {
                    Vol vol = getTableView().getItems().get(getIndex());
                    System.out.println("ID du vol sélectionné : " + vol.getId_vol());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVol.fxml"));
                        Parent root = loader.load();

                        ModifierVolController controller = loader.getController();
                        controller.setVol(vol);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Vol");
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                        stage.setScene(scene);
                        stage.showAndWait();

                        // Rafraîchir la table après modification
                        afficherVols();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                buttonBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox); // ← Affiche les deux boutons
                }
            }
        });
    }
    //ajouter vol
    public void ajouterVol(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVol.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Vol");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //filter les vols
    @FXML
    private void filtrerVols(ActionEvent event) {
        List<Vol> tousLesVols = volService.getAllVols();
        List<Vol> volsFiltres = new ArrayList<>();

        for (Vol vol : tousLesVols) {
            boolean correspond = true;

            if (destinationField.getValue() != null && !destinationField.getValue().isEmpty()) {
                if (!vol.getDestination().toLowerCase().contains(destinationField.getValue().toLowerCase())) {
                    correspond = false;
                }
            }

            // Date aller >= début sélectionnée
            if (dateAllerDebutPicker.getValue() != null) {
                Date minDate = java.sql.Date.valueOf(dateAllerDebutPicker.getValue());
                if (vol.getDate().before(minDate)) {
                    correspond = false;
                }
            }

            // Date retour <= date retour sélectionnée
            if (dateRetourPicker.getValue() != null) {
                Date maxRetour = java.sql.Date.valueOf(dateRetourPicker.getValue());
                if (vol.getDateRetour() == null || vol.getDateRetour().after(maxRetour)) {
                    correspond = false;
                }
            }

            // Catégorie exacte
            if (categorieCombo.getValue() != null) {
                String valeurChoisie = categorieCombo.getValue().toUpperCase();
                if (!vol.getCategorie().getNom().name().equalsIgnoreCase(valeurChoisie)) {
                    correspond = false;
                }
            }

            // Prix <= max
            if (!prixMaxField.getText().isEmpty()) {
                try {
                    double prixMax = Double.parseDouble(prixMaxField.getText());
                    if (vol.getPrix() > prixMax) {
                        correspond = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Prix max invalide.");
                    correspond = false;
                }
            }

            if (correspond) {
                volsFiltres.add(vol);
            }
        }

        volsAffiches = volsFiltres;
        setupPagination();
        pagination.setCurrentPageIndex(0);
    }
    @FXML
    private void reinitialiserFiltres(ActionEvent event) {
        destinationField.setValue(null);
        departField.setValue(null);
        dateAllerDebutPicker.setValue(null);
        dateRetourPicker.setValue(null);
        categorieCombo.setValue(null);
        prixMaxField.clear();

        // Recharger tous les vols
        volsAffiches = volService.getAllVols();
        setupPagination();

    }


    @FXML
    private void genererPdf(ActionEvent event) {
        Document document = new Document();
        try {
            // Créer le chemin dans "Documents"
            String userHome = System.getProperty("user.home");
            String path = userHome + "/Documents/Liste_des_vols.pdf";

            // Initialiser le writer
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // === DATE EN HAUT À DROITE ===
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            Paragraph dateParagraph = new Paragraph("Tunis, le " + date,
                    FontFactory.getFont(FontFactory.HELVETICA, 10));
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraph);

            // === Titre ===
            document.add(new Paragraph("Liste des Vols",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            document.add(new Paragraph(" ")); // Ligne vide

            // === TABLEAU ===
            PdfPTable table = new PdfPTable(7); // 7 colonnes
            table.setWidthPercentage(100);

            // En-têtes
            Stream.of("Départ", "Destination", "Date", "Date Retour", "Prix", "Prix Final", "Catégorie")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            // Lignes de données
            for (Vol vol : volsAffiches) {
                table.addCell(vol.getDepart());
                table.addCell(vol.getDestination());
                table.addCell(new SimpleDateFormat("yyyy-MM-dd").format(vol.getDate()));
                table.addCell(vol.getDateRetour() != null
                        ? new SimpleDateFormat("yyyy-MM-dd").format(vol.getDateRetour()) : "—");
                table.addCell(String.valueOf(vol.getPrix()));
                table.addCell(String.format("%.2f", volService.calculerPrixFinal(vol)));
                table.addCell(vol.getCategorie().getNom().name());
            }

            document.add(table); // Ajouter le tableau

            // === SIGNATURE EN BAS DE PAGE ===
            PdfContentByte canvas = writer.getDirectContent();
            Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
            Phrase signaturePhrase = new Phrase("Signature : Agence Last Minute Travel", font);

            ColumnText.showTextAligned(
                    canvas,
                    Element.ALIGN_RIGHT,
                    signaturePhrase,
                    document.right(),
                    document.bottom() + 30, // 30 points du bas
                    0
            );

            document.close();

            // Ouvrir le PDF
            java.awt.Desktop.getDesktop().open(new java.io.File(path));
            System.out.println("PDF généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchCountries(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + query))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        JSONArray array = new JSONArray(json);
                        List<String> countries = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject country = array.getJSONObject(i);
                            String name = country.getJSONObject("name").getString("common");
                            countries.add(name);
                        }
                        Platform.runLater(() -> showCountrySuggestions(countries));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean isInternetAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.google.com"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void showCountrySuggestions(List<String> countries) {
        if (currentPopup != null) {
            currentPopup.hide();
        }

        Popup popup = new Popup();
        VBox content = new VBox(5);
        content.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1;");

        for (String country : countries) {
            Label label = new Label(country);
            label.setStyle("-fx-cursor: hand; -fx-padding: 5;");
            label.setOnMouseClicked(e -> {
                destinationField.setValue(country);
                popup.hide();
            });
            content.getChildren().add(label);
        }

        popup.getContent().add(content);
        currentPopup = popup;

        // Positionner le popup sous le champ de texte
        Bounds bounds = destinationField.localToScreen(destinationField.getBoundsInLocal());
        popup.show(destinationField.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
    }

    // Ajouter cette variable de classe
    private Popup currentPopup;

    private void loadCountries() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://restcountries.com/v3.1/all"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        JSONArray array = new JSONArray(json);
                        List<String> pays = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject country = array.getJSONObject(i);
                            String name = country.getJSONObject("name").getString("common");
                            pays.add(name);
                        }
                        Platform.runLater(() -> {
                            destinationField.getItems().clear();
                            destinationField.getItems().addAll(pays);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void filterComboBox(ComboBox<String> comboBox, String filter) {
        if (filter == null || filter.isEmpty()) {
            comboBox.show();
            return;
        }

        ObservableList<String> filteredItems = FXCollections.observableArrayList();
        for (String item : comboBox.getItems()) {
            if (item.toLowerCase().contains(filter.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        comboBox.setItems(filteredItems);
        comboBox.show();
    }

    private void searchFlights() {
        String destination = destinationField.getValue();
        String depart = departField.getValue();
        // ... existing code ...
    }

    private void clearFields() {
        destinationField.getItems().clear();
        departField.getItems().clear();
        // ... existing code ...
    }

    private void handleDestinationSelection() {
        String selectedCountry = destinationField.getValue();
        if (selectedCountry != null && !selectedCountry.isEmpty()) {
            // ... existing code ...
        }
    }
}