package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Desktop;

public class PDFViewer implements Initializable {
    @FXML private TableView<PDFFile> pdfTable;
    @FXML private TableColumn<PDFFile, String> fileNameColumn;
    @FXML private TableColumn<PDFFile, String> dateColumn;
    @FXML private TableColumn<PDFFile, Void> actionColumn;

    private ObservableList<PDFFile> pdfList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        refreshPDFList();
    }

    private void setupTable() {
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("generatedDate"));

        // Setup action column with View button
        actionColumn.setCellFactory(createActionButtonCellFactory());
    }

    private Callback<TableColumn<PDFFile, Void>, TableCell<PDFFile, Void>> createActionButtonCellFactory() {
        return new Callback<TableColumn<PDFFile, Void>, TableCell<PDFFile, Void>>() {
            @Override
            public TableCell<PDFFile, Void> call(TableColumn<PDFFile, Void> param) {
                return new TableCell<PDFFile, Void>() {
                    private final Button viewButton = new Button("View");
                    private final HBox buttons = new HBox(10, viewButton);

                    {
                        viewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        buttons.setAlignment(Pos.CENTER);
                        
                        viewButton.setOnAction(event -> {
                            PDFFile pdf = getTableView().getItems().get(getIndex());
                            openPDF(pdf.getFile());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : buttons);
                    }
                };
            }
        };
    }

    @FXML
    private void refreshPDFList() {
        try {
            // Get the project root directory
            File projectRoot = new File(System.getProperty("user.dir"));
            File pdfDir = new File(projectRoot, "pdfs");
            
            if (!pdfDir.exists()) {
                boolean created = pdfDir.mkdir();
                if (!created) {
                    showError("Error", "Could not create PDFs directory at: " + pdfDir.getAbsolutePath());
                    return;
                }
            }

            File[] files = pdfDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (files == null) {
                showError("Error", "Could not access PDFs directory at: " + pdfDir.getAbsolutePath());
                return;
            }

            List<PDFFile> pdfFiles = Arrays.stream(files)
                    .map(file -> new PDFFile(
                        file.getName(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified())),
                        file
                    ))
                    .collect(Collectors.toList());

            pdfList.setAll(pdfFiles);
            pdfTable.setItems(pdfList);

            if (pdfFiles.isEmpty()) {
                showInfo("Information", "No PDF files found in: " + pdfDir.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Error refreshing PDF list: " + e.getMessage());
        }
    }

    private void openPDF(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                showError("Error", "Desktop is not supported on this system.");
            }
        } catch (Exception e) {
            showError("Error", "Could not open PDF file: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to represent PDF files in the table
    public static class PDFFile {
        private final String fileName;
        private final String generatedDate;
        private final File file;

        public PDFFile(String fileName, String generatedDate, File file) {
            this.fileName = fileName;
            this.generatedDate = generatedDate;
            this.file = file;
        }

        public String getFileName() { return fileName; }
        public String getGeneratedDate() { return generatedDate; }
        public File getFile() { return file; }
    }
} 