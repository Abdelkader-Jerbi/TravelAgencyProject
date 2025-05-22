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

        // Setup action column with View and Delete buttons
        actionColumn.setCellFactory(createActionButtonCellFactory());
    }

    private Callback<TableColumn<PDFFile, Void>, TableCell<PDFFile, Void>> createActionButtonCellFactory() {
        return new Callback<TableColumn<PDFFile, Void>, TableCell<PDFFile, Void>>() {
            @Override
            public TableCell<PDFFile, Void> call(TableColumn<PDFFile, Void> param) {
                return new TableCell<PDFFile, Void>() {
                    private final Button viewButton = new Button("Voir");
                    private final Button deleteButton = new Button("Supprimer");
                    private final HBox buttons = new HBox(10, viewButton, deleteButton);

                    {
                        viewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        buttons.setAlignment(Pos.CENTER);
                        
                        viewButton.setOnAction(event -> {
                            PDFFile pdf = getTableView().getItems().get(getIndex());
                            openPDF(pdf.getFile());
                        });

                        deleteButton.setOnAction(event -> {
                            PDFFile pdf = getTableView().getItems().get(getIndex());
                            deletePDF(pdf);
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
            System.out.println("Starting PDF list refresh...");
            
            // Get the project root directory
            File projectRoot = new File(System.getProperty("user.dir"));
            System.out.println("Project root: " + projectRoot.getAbsolutePath());
            
            File pdfDir = new File(projectRoot, "pdfs");
            System.out.println("PDF directory: " + pdfDir.getAbsolutePath());
            
            if (!pdfDir.exists()) {
                System.out.println("PDF directory does not exist, creating it...");
                boolean created = pdfDir.mkdir();
                if (!created) {
                    System.out.println("Failed to create PDF directory");
                    showError("Error", "Could not create PDFs directory at: " + pdfDir.getAbsolutePath());
                    return;
                }
                System.out.println("PDF directory created successfully");
            }

            File[] files = pdfDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            System.out.println("Found " + (files != null ? files.length : 0) + " PDF files");
            
            if (files == null) {
                System.out.println("Could not access PDF directory");
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

            System.out.println("Updating table with " + pdfFiles.size() + " files");
            
            // Clear and update the table
            pdfList.clear();
            pdfList.addAll(pdfFiles);
            pdfTable.setItems(pdfList);
            
            // Force table refresh
            pdfTable.refresh();
            
            // Show success message
            if (pdfFiles.isEmpty()) {
                System.out.println("No PDF files found");
                showInfo("Information", "No PDF files found in: " + pdfDir.getAbsolutePath());
            } else {
                System.out.println("PDF list refreshed successfully");
                showInfo("Success", "PDF list has been refreshed. Found " + pdfFiles.size() + " files.");
            }
        } catch (Exception e) {
            System.err.println("Error during PDF list refresh: " + e.getMessage());
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

    private void deletePDF(PDFFile pdf) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this PDF?");
        alert.setContentText("File: " + pdf.getFileName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (pdf.getFile().delete()) {
                        showInfo("Success", "PDF file deleted successfully!");
                        refreshPDFList(); // Refresh the list after deletion
                    } else {
                        showError("Error", "Could not delete the PDF file. It may be in use by another program.");
                    }
                } catch (Exception e) {
                    showError("Error", "Failed to delete PDF file: " + e.getMessage());
                }
            }
        });
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