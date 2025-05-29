package Controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entities.Voiture;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.CrudVoiture;
import Controller.ReservationVoitureController;
import Controller.ThemeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class VoitureController {

    @FXML private TableView<Voiture> voitureTable;

    @FXML private TableColumn<Voiture, String> colMarque;
    @FXML private TableColumn<Voiture, String> colModele;
    @FXML private TableColumn<Voiture, String> colMatricule;
    @FXML private TableColumn<Voiture, Double> colPrix;
    @FXML private TableColumn<Voiture, String> colDisponible;
    @FXML private TableColumn<Voiture, String> imageCol;
    @FXML private TableColumn<Voiture, Void> colAction;

    @FXML private Pagination pagination;
    @FXML private Button addOrUpdateButton;
    @FXML private Button refreshButton;
    @FXML private Button exportPdfButton;
    @FXML private TextField searchField;
    @FXML private TextField colorField;
    // @FXML private Slider prixSlider;
    // @FXML private Label prixValueLabel;
    @FXML private ComboBox<String> disponibiliteFilter;

    private List<Voiture> allVoitures = List.of();
    private List<Voiture> filteredVoitures = List.of();

    private final CrudVoiture voitureDAO = new CrudVoiture();

    private static final int ROWS_PER_PAGE = 5;

    @FXML
    public void initialize() {
        // Initialiser les colonnes avec leurs propriétés
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        colDisponible.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().isDisponible() ? "Disponible" : "Indisponible"));

        // Affichage des images dans la colonne image
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image("file:" + imagePath, 80, 50, true, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        // Colonne des actions (Modifier / Supprimer)
        colAction.setCellFactory(getActionCellFactory());

        // Boutons et filtres
        addOrUpdateButton.setOnAction(e -> openAjouterWindow());
        refreshButton.setOnAction(e -> refreshPage());
        exportPdfButton.setOnAction(e -> exportPDF());
        disponibiliteFilter.setItems(FXCollections.observableArrayList("Tous", "Disponible", "Indisponible"));
        disponibiliteFilter.setValue("Tous");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        colorField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        // prixSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        //     int prix = newVal.intValue();
        //     prixValueLabel.setText(String.valueOf(prix));
        //     applyFilter();
        // });
        // prixValueLabel.setText(String.valueOf((int)prixSlider.getValue()));
        disponibiliteFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Configuration de la pagination
        pagination.setMaxPageIndicatorCount(5);
        pagination.setStyle("-fx-background-color: transparent; -fx-page-information-visible: true; -fx-padding: 10 0 0 0;");

        // Charger les données initiales
        loadAllVoitures();
    }

    // Recharge la liste complète des voitures depuis la base
    private void loadAllVoitures() {
        try {
            allVoitures = voitureDAO.getAllVoitures();
            filteredVoitures = (allVoitures != null) ? allVoitures : List.of();
            applyFilter();
        } catch (SQLException e) {
            afficherErreur("Erreur de chargement : " + e.getMessage());
            allVoitures = List.of();
            filteredVoitures = List.of();
            updatePaginationFiltered();
        }
    }

    // Appliquer les filtres texte et disponibilité
    private void applyFilter() {
        String searchText = searchField.getText().toLowerCase().trim();
        String dispo = disponibiliteFilter.getValue();
        String colorText = colorField.getText().toLowerCase().trim();
        double prixMax = 1000000; // Valeur très haute par défaut
        // Si tu veux activer le slider de prix, décommente la ligne suivante :
        // prixMax = prixSlider != null ? prixSlider.getValue() : 1000000;

        filteredVoitures = allVoitures.stream()
                .filter(v -> (
                        searchText.isEmpty() ||
                        (v.getMarque() != null && v.getMarque().toLowerCase().contains(searchText)) ||
                        (v.getModele() != null && v.getModele().toLowerCase().contains(searchText)) ||
                        (v.getMatricule() != null && v.getMatricule().toLowerCase().contains(searchText)) ||
                        (v.getCouleur() != null && v.getCouleur().toLowerCase().contains(searchText)) ||
                        (v.getCarburant() != null && v.getCarburant().toLowerCase().contains(searchText)) ||
                        (v.getCategorie() != null && v.getCategorie().toLowerCase().contains(searchText))
                ))
                .filter(v -> (colorText.isEmpty() || (v.getCouleur() != null && v.getCouleur().toLowerCase().contains(colorText))))
                .filter(v -> ("Tous".equals(dispo) ||
                                ("Disponible".equals(dispo) && v.isDisponible()) ||
                                ("Indisponible".equals(dispo) && !v.isDisponible())))
                .filter(v -> v.getPrixParJour() <= prixMax)
                .toList();

        updatePaginationFiltered();
    }

    // Met à jour la pagination selon les voitures filtrées
    private void updatePaginationFiltered() {
        int pageCount = (int) Math.ceil((double) filteredVoitures.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);

        int currentPage = pagination.getCurrentPageIndex();
        if (currentPage >= pagination.getPageCount()) {
            currentPage = 0;
        }
        pagination.setCurrentPageIndex(currentPage);

        pagination.setPageFactory(this::createPageFiltered);
    }

    // Crée une page de la table avec les voitures filtrées pour la pagination
    private javafx.scene.Node createPageFiltered(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredVoitures.size());
        ObservableList<Voiture> pageVoitures = FXCollections.observableArrayList(filteredVoitures.subList(fromIndex, toIndex));
        voitureTable.setItems(pageVoitures);
        return voitureTable;
    }

    // Fabrique pour la colonne d'action : boutons Modifier, Supprimer et Détails
    private Callback<TableColumn<Voiture, Void>, TableCell<Voiture, Void>> getActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Voiture, Void> call(final TableColumn<Voiture, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("✏️ Modifier");
                    private final Button btnSupprimer = new Button("🗑️ Supprimer");
                    private final Button btnDetails = new Button("👁️ Détails");
                    private final HBox pane = new HBox(12, btnModifier, btnSupprimer, btnDetails);

                    {
                        btnModifier.getStyleClass().setAll("button", "action-btn", "modifier");
                        btnModifier.setMinWidth(70);
                        btnModifier.setPrefHeight(26);
                        btnSupprimer.getStyleClass().setAll("button", "action-btn", "supprimer");
                        btnSupprimer.setMinWidth(70);
                        btnSupprimer.setPrefHeight(26);
                        btnDetails.getStyleClass().setAll("button", "action-btn", "details");
                        btnDetails.setMinWidth(70);
                        btnDetails.setPrefHeight(26);
                        pane.setAlignment(javafx.geometry.Pos.CENTER);

                        btnModifier.setOnAction(event -> {
                            Voiture voiture = getTableView().getItems().get(getIndex());
                            openModifierWindow(voiture);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Voiture voiture = getTableView().getItems().get(getIndex());
                            supprimerVoiture(voiture);
                        });

                        btnDetails.setOnAction(event -> {
                            Voiture voiture = getTableView().getItems().get(getIndex());
                            openDetailWindow(voiture);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };
    }

    // Supprime une voiture après confirmation
    private void supprimerVoiture(Voiture voiture) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Confirmer la suppression");
        alert.setContentText("Voulez-vous vraiment supprimer la voiture " + voiture.getMarque() + " " + voiture.getModele() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    voitureDAO.deleteVoiture(voiture.getId());
                    allVoitures = voitureDAO.getAllVoitures();
                    applyFilter();
                    afficherInfo("Voiture supprimée avec succès !");
                } catch (SQLIntegrityConstraintViolationException e) {
                    afficherErreur("Impossible de supprimer cette voiture car elle est liée à une réservation.");
                } catch (SQLException e) {
                    afficherErreur("Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    // Ouvre la fenêtre de modification ou ajout d'une voiture
    private void openModifierWindow(Voiture voiture) {
        try {
            if (voiture == null) {
                afficherErreur("Aucune voiture sélectionnée pour la modification.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVoiture.fxml"));
            Parent root = loader.load();

            ModifierVoitureController ctrl = loader.getController();
                ctrl.setVoiture(voiture);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier voiture");
            stage.showAndWait();

            // Rafraîchir après fermeture modale
            refreshPage();

        } catch (IOException e) {
            afficherErreur("Erreur ouverture modification : " + e.getMessage());
        }
    }

    // Actualise la page actuelle en rechargeant les données
    private void refreshPage() {
        loadAllVoitures();
    }

    // Affiche un message d'information
    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Affiche un message d'erreur
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode appelée depuis ModifierVoitureController pour mettre à jour la liste après ajout/modification
    public void updateListAfterChange() {
        loadAllVoitures();
    }

    // Exporter la liste filtrée en PDF
    @FXML
    private void exportPDF() {
        if (filteredVoitures.isEmpty()) {
            afficherInfo("Aucune voiture à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(voitureTable.getScene().getWindow());

        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36); // marges gauche, droite, haut, bas
            PdfWriter writer = PdfWriter.getInstance(document, fos);

            // Pied de page avec numéro de page
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    Phrase footer = new Phrase("Page " + writer.getPageNumber(),
                            FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
                    ColumnText.showTextAligned(writer.getDirectContent(),
                            Element.ALIGN_CENTER, footer,
                            (document.right() - document.left()) / 2 + document.leftMargin(),
                            document.bottom() - 10, 0);
                }
            });

            document.open();

            // Logo en haut (optionnel)


            // Titre avec style
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLUE);
            Paragraph title = new Paragraph("Liste des Voitures", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Largeur des colonnes (optionnel)
            float[] columnWidths = {2f, 2f, 2f, 1.5f, 1.5f, 2f, 2f, 1.5f, 2f, 1.5f};
            table.setWidths(columnWidths);

            // En-tête avec couleur et bordure personnalisée
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            BaseColor headerColor = new BaseColor(0, 102, 204); // bleu foncé

            addCellToHeader(table, "Marque", headerFont, headerColor);
            addCellToHeader(table, "Modèle", headerFont, headerColor);
            addCellToHeader(table, "Matricule", headerFont, headerColor);
            addCellToHeader(table, "Prix/jour", headerFont, headerColor);
            addCellToHeader(table, "Disponible", headerFont, headerColor);
            addCellToHeader(table, "Année", headerFont, headerColor);

            // Corps du tableau avec lignes alternées
            boolean alternate = false;
            BaseColor rowColor1 = BaseColor.WHITE;
            BaseColor rowColor2 = new BaseColor(224, 235, 255); // bleu très clair

            for (Voiture v : filteredVoitures) {
                BaseColor backgroundColor = alternate ? rowColor2 : rowColor1;
                addCellToBody(table, v.getMarque(), backgroundColor);
                addCellToBody(table, v.getModele(), backgroundColor);
                addCellToBody(table, v.getMatricule(), backgroundColor);
                addCellToBody(table, String.valueOf(v.getPrixParJour()), backgroundColor);
                addCellToBody(table, v.isDisponible() ? "Oui" : "Non", backgroundColor);
                addCellToBody(table, String.valueOf(v.getAnnee()), backgroundColor);
                alternate = !alternate;
            }

            document.add(table);
            document.close();

            afficherInfo("Export PDF réussi avec design amélioré !");
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }

    private void addCellToHeader(PdfPTable table, String header, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addCellToBody(PdfPTable table, String value, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);
    }


    private void addCellToHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void addCellToBody(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Phrase(value)));
    }

    // Ouvre la fenêtre d'ajout d'une nouvelle voiture
    private void openAjouterWindow() {
        try {
            System.out.println("Tentative d'ouverture de la fenêtre d'ajout...");
            URL url = getClass().getResource("/AjouterVoiture.fxml");
            if (url == null) {
                throw new IOException("Le fichier AjouterVoiture.fxml n'a pas été trouvé dans le classpath");
            }
            System.out.println("URL du fichier FXML : " + url);
            
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            AjouterVoitureController ctrl = loader.getController();
            ctrl.setVoitureTable(voitureTable);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une voiture");
            // Appliquer le thème courant
            if (ThemeManager.isDarkMode()) {
                if (!root.getStyleClass().contains("dark-root")) {
                    root.getStyleClass().add("dark-root");
                }
            } else {
                root.getStyleClass().remove("dark-root");
            }
            stage.showAndWait();

            // Rafraîchir après fermeture modale
            refreshPage();

        } catch (IOException e) {
            e.printStackTrace(); // Afficher la stack trace complète
            afficherErreur("Erreur ouverture ajout : " + e.getMessage() + "\n" + e.getCause());
        }
    }

    // Ouvre la fenêtre de détails d'une voiture
    private void openDetailWindow(Voiture voiture) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailVoitureAdmin.fxml"));
            Parent root = loader.load();

            Controller.DetailVoitureAdminController ctrl = loader.getController();
            ctrl.setVoiture(voiture);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la voiture");
            // Appliquer le thème courant
            if (ThemeManager.isDarkMode()) {
                if (!root.getStyleClass().contains("dark-root")) {
                    root.getStyleClass().add("dark-root");
                }
            } else {
                root.getStyleClass().remove("dark-root");
            }
            stage.showAndWait();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture détails : " + e.getMessage());
        }
    }
}
