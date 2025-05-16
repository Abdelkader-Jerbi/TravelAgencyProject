package Controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import entities.Voiture;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CrudVoiture;

import com.itextpdf.text.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoitureController {

    @FXML private TableView<Voiture> voitureTable;
    @FXML private TableColumn<Voiture, String> imagePath;
    @FXML private TableColumn<Voiture, String> marqueCol;
    @FXML private TableColumn<Voiture, String> modelCol;
    @FXML private TableColumn<Voiture, Double> prixCol;
    @FXML private TableColumn<Voiture, String> matriculeCol;
    @FXML private TableColumn<Voiture, String> disponible;
    @FXML private TableColumn<Voiture, Void> actionCol;

    @FXML private Button addOrUpdateButton;
    @FXML private Button refreshButton;
    @FXML private Button btnExportPdf;
    @FXML private Pagination pagination;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> disponibiliteFilter;

    private final CrudVoiture crud = new CrudVoiture();
    private final String baseDir = "C:/images_voitures/";

    private List<Voiture> allVoitures;
    private List<Voiture> filteredVoitures;

    private final int rowsPerPage = 5;

    // Couleur d'entête pour PDF
    private static final BaseColor HEADER_BG_COLOR = new BaseColor(0, 121, 107);
    // Chemin logo (modifiable)
    private static final String LOGO_PATH = "C:/images_voitures/logo.png";

    @FXML
    public void initialize() {
        // Liaison colonnes aux propriétés
        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        disponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        // Cellule personnalisée pour afficher l’image
        imagePath.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        String fullPath = path.startsWith("file:") || path.startsWith("http")
                                ? path
                                : "file:///" + (baseDir + path).replace("\\", "/");
                        Image img = new Image(fullPath, 100, 0, true, true);
                        imageView.setImage(img);
                        imageView.setFitWidth(100);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                        setText(null);
                    } catch (Exception e) {
                        setGraphic(null);
                        setText(null);
                    }
                }
            }
        });

        // Setup filtre disponibilité
        disponibiliteFilter.setItems(FXCollections.observableArrayList("Tous", "Disponible", "Indisponible"));
        disponibiliteFilter.setValue("Tous");

        // Gestion boutons
        addOrUpdateButton.setOnAction(e -> openAjouterWindow());
        refreshButton.setOnAction(e -> refreshTable());
        btnExportPdf.setOnAction(e -> exportPdf());

        // Ecouteurs filtres texte et combo
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        disponibiliteFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Ajouter boutons action dans la table
        addActionButtonsToTable();

        // Chargement initial
        loadAllVoitures();
    }

    private void loadAllVoitures() {
        try {
            allVoitures = crud.getAllVoitures();
            if (allVoitures == null) allVoitures = List.of();
            filteredVoitures = allVoitures;
            applyFilter();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement : " + e.getMessage());
            allVoitures = List.of();
            filteredVoitures = List.of();
            updatePaginationFiltered();
        }
    }

    private void applyFilter() {
        String searchText = searchField.getText().toLowerCase().trim();
        String dispo = disponibiliteFilter.getValue();

        filteredVoitures = allVoitures.stream()
                .filter(v -> (searchText.isEmpty() || v.getMarque().toLowerCase().contains(searchText)
                        || v.getModele().toLowerCase().contains(searchText)
                        || v.getMatricule().toLowerCase().contains(searchText))
                        && ("Tous".equals(dispo) ||
                        ("Disponible".equals(dispo) && v.isDisponible()) ||
                        ("Indisponible".equals(dispo) && !v.isDisponible())))
                .toList();

        updatePaginationFiltered();
    }

    private void updatePaginationFiltered() {
        int pageCount = (int) Math.ceil((double) filteredVoitures.size() / rowsPerPage);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPageFiltered);
    }

    private TableView<Voiture> createPageFiltered(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, filteredVoitures.size());
        voitureTable.setItems(FXCollections.observableArrayList(filteredVoitures.subList(fromIndex, toIndex)));
        return voitureTable;
    }

    private void refreshTable() {
        loadAllVoitures();
    }

    private void addActionButtonsToTable() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(10, editButton, deleteButton);

            {
                pane.setAlignment(Pos.CENTER);
                editButton.setOnAction(e -> openModifierWindow(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> supprimerVoiture(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void supprimerVoiture(Voiture voiture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Confirmer suppression");
        alert.setContentText("Supprimer " + voiture.getMarque() + " " + voiture.getModele() + " ?");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    crud.deleteVoiture(voiture.getId());
                    afficherInfo("Voiture supprimée.");
                    refreshTable();
                } catch (SQLException e) {
                    afficherErreur("Erreur suppression : " + e.getMessage());
                }
            }
        });
    }

    private void openAjouterWindow() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXML/ajouterVoiture.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une voiture");
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture : " + e.getMessage());
        }
    }

    private void openModifierWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/modifierVoiture.fxml"));
            Parent root = loader.load();
            ModifierVoitureController ctrl = loader.getController();
            ctrl.setVoiture(voiture);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier voiture");
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture modification : " + e.getMessage());
        }
    }

    private void afficherErreur(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void afficherInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void exportPdf() {
        Voiture selectedVoiture = voitureTable.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        String defaultFileName = (selectedVoiture != null) ? selectedVoiture.getModele() + ".pdf" : "voitures.pdf";
        fileChooser.setInitialFileName(defaultFileName);

        Stage stage = (Stage) voitureTable.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile == null) {
            return; // Annulé par utilisateur
        }

        try {
            if (selectedVoiture != null) {
                exportSingleVoiture(selectedFile.getAbsolutePath(), selectedVoiture, baseDir);
            } else {
                exportAllVoitures(selectedFile.getAbsolutePath(), filteredVoitures, baseDir);
            }
            afficherInfo("Export PDF réussi !");
        } catch (Exception e) {
            afficherErreur("Erreur export PDF : " + e.getMessage());
        }
    }

    public void exportSingleVoiture(String filePath, Voiture voiture, String baseDir) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Logo



        document.add(new Paragraph("\n"));

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Fiche Voiture", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Image voiture


        document.add(new Paragraph("\n"));

        // Infos voiture sous forme de tableau
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        addCellToTable(table, "Marque", true);
        addCellToTable(table, voiture.getMarque(), false);

        addCellToTable(table, "Modèle", true);
        addCellToTable(table, voiture.getModele(), false);

        addCellToTable(table, "Prix par jour", true);
        addCellToTable(table, String.format("%.2f", voiture.getPrixParJour()), false);

        addCellToTable(table, "Matricule", true);
        addCellToTable(table, voiture.getMatricule(), false);

        addCellToTable(table, "Disponibilité", true);
        addCellToTable(table, voiture.isDisponible() ? "Disponible" : "Indisponible", false);

        document.add(table);
        document.close();
    }

    public void exportAllVoitures(String filePath, List<Voiture> voitures, String baseDir) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Logo

        document.add(new Paragraph("\n"));

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Liste des Voitures", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 2, 3, 2, 3});

        // Entête avec style
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        String[] headers = {"Marque", "Modèle", "Prix par jour", "Matricule", "Disponibilité", "Image"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(HEADER_BG_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Ajout données
        for (Voiture v : voitures) {
            table.addCell(v.getMarque());
            table.addCell(v.getModele());
            table.addCell(String.format("%.2f", v.getPrixParJour()));
            table.addCell(v.getMatricule());
            table.addCell(v.isDisponible() ? "Disponible" : "Indisponible");


        }

        document.add(table);
        document.close();
    }

    private void addCellToTable(PdfPTable table, String text, boolean isHeader) {
        Font font = isHeader
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)
                : FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(224, 224, 224));
        }
        table.addCell(cell);
    }
}
