package Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CrudVoiture;
import entities.Voiture;
import javafx.scene.image.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.scene.control.TextField;

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
    private final String baseDir = "C:/Users/gharb/TravelAgencyProject/src/main/resources/images/";

    private List<Voiture> allVoitures;
    private List<Voiture> filteredVoitures;

    private final int rowsPerPage = 5;

    @FXML
    public void initialize() {
        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        disponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        imagePath.setCellFactory(col -> new TableCell<Voiture, String>() {
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

        disponibiliteFilter.setItems(FXCollections.observableArrayList("Tous", "Disponible", "Indisponible"));
        disponibiliteFilter.setValue("Tous");

        addOrUpdateButton.setOnAction(e -> openModifierWindow(null));
        refreshButton.setOnAction(e -> refreshTable());
        btnExportPdf.setOnAction(e -> exportPdf());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        disponibiliteFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        addActionButtonsToTable();
        loadAllVoitures();
    }

    private void loadAllVoitures() {
        try {
            allVoitures = crud.getAllVoitures();
            filteredVoitures = (allVoitures != null) ? allVoitures : List.of();
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
                .filter(v -> (searchText.isEmpty() ||
                        v.getMarque().toLowerCase().contains(searchText) ||
                        v.getModele().toLowerCase().contains(searchText) ||
                        v.getMatricule().toLowerCase().contains(searchText)) &&
                        ("Tous".equals(dispo) ||
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
        actionCol.setCellFactory(param -> new TableCell<Voiture, Void>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(10, editButton, deleteButton);

            {
                pane.setAlignment(Pos.CENTER);

                editButton.setOnAction(e -> {
                    Voiture v = getTableView().getItems().get(getIndex());
                    openModifierWindow(v);
                });

                deleteButton.setOnAction(e -> {
                    Voiture v = getTableView().getItems().get(getIndex());
                    supprimerVoiture(v);
                });
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

    private void openModifierWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVoiture.fxml"));
            Parent root = loader.load();
            ModifierVoitureController ctrl = loader.getController();
            if (voiture != null) {
                ctrl.setVoiture(voiture);
            }
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
        if (selectedFile == null) return;

        try {
            if (selectedVoiture != null) {
                exportSingleVoiture(selectedFile.getAbsolutePath(), selectedVoiture);
            } else {
                exportAllVoitures(selectedFile.getAbsolutePath(), filteredVoitures);
            }
            afficherInfo("Export PDF réussi !");
        } catch (Exception e) {
            afficherErreur("Erreur export PDF : " + e.getMessage());
        }
    }

    public void exportSingleVoiture(String filePath, Voiture voiture) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Paragraph title = new Paragraph("Détails de la voiture", new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        addCell(table, "Marque :", true);
        addCell(table, voiture.getMarque(), false);
        addCell(table, "Modèle :", true);
        addCell(table, voiture.getModele(), false);
        addCell(table, "Prix/jour :", true);
        addCell(table, String.valueOf(voiture.getPrixParJour()), false);
        addCell(table, "Matricule :", true);
        addCell(table, voiture.getMatricule(), false);
        addCell(table, "Disponible :", true);
        addCell(table, voiture.isDisponible() ? "Oui" : "Non", false);

        document.add(table);
        document.close();
    }

    public void exportAllVoitures(String filePath, List<Voiture> voitures) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Paragraph title = new Paragraph("Liste des voitures", new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new int[]{3, 3, 2, 3, 2});

        addCell(table, "Marque", true);
        addCell(table, "Modèle", true);
        addCell(table, "Prix/jour", true);
        addCell(table, "Matricule", true);
        addCell(table, "Disponible", true);

        for (Voiture v : voitures) {
            addCell(table, v.getMarque(), false);
            addCell(table, v.getModele(), false);
            addCell(table, String.valueOf(v.getPrixParJour()), false);
            addCell(table, v.getMatricule(), false);
            addCell(table, v.isDisponible() ? "Oui" : "Non", false);
        }

        document.add(table);
        document.close();
    }

    private void addCell(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        if (isHeader) {
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
        } else {
            cell.setPadding(4);
        }
        table.addCell(cell);
    }
}
