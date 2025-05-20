package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Hotel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.CrudHotel;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class HotelInfo {
    @FXML private TableView<Hotel> hotelTable;
    @FXML private TableColumn<Hotel, String> colNom;
    @FXML private TableColumn<Hotel, String> colDestination;
    @FXML private TableColumn<Hotel, Date> colDate;
    @FXML private TableColumn<Hotel, Integer> colChambre;
    @FXML private TableColumn<Hotel, Integer> colEtoile;
    @FXML private TableColumn<Hotel, Float> colTarif;
    @FXML private TableColumn<Hotel, Void> colAction;
    @FXML private TableColumn<Hotel, String> colImage;
    @FXML private TableColumn<Hotel, String> colDescription;
    @FXML private TableColumn<Hotel, String> colCategory;
    @FXML private TextField searchField;
    @FXML private DatePicker searchDatePicker;
    private ObservableList<Hotel> hotelList = FXCollections.observableArrayList();
    CrudHotel crudHotel = new CrudHotel();

    @FXML
    public void initialize() {
        // Column setup


        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDestination.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colChambre.setCellValueFactory(new PropertyValueFactory<>("Chambre"));
        colEtoile.setCellValueFactory(new PropertyValueFactory<>("nbEtoile"));
        colTarif.setCellValueFactory(new PropertyValueFactory<>("tarif"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categorieType"));

        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image;
                        // Check if the path is absolute (Windows path)
                        if (imagePath.contains(":\\") || imagePath.contains(":/")) {
                            // Handle absolute path
                            File imageFile = new File(imagePath);
                            if (imageFile.exists()) {
                                image = new Image(imageFile.toURI().toString());
                            } else {
                                System.err.println("Image file not found: " + imagePath);
                                setGraphic(null);
                                return;
                            }
                        } else {
                            // Handle relative path
                            String resourcePath = "/View/images/" + imagePath;
                            System.out.println("Loading image from: " + resourcePath);
                            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                                if (is != null) {
                                    image = new Image(is);
                                } else {
                                    System.err.println("Resource not found: " + resourcePath);
                                    setGraphic(null);
                                    return;
                                }
                            }
                        }
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + e.getMessage());
                        setGraphic(null);
                    }
                }
            }
        });

        try {
            hotelList.setAll(crudHotel.afficher());
        } catch (SQLException e) {
            showError("Error loading hotels", e);
        }

        FilteredList<Hotel> filteredData = new FilteredList<>(hotelList, p -> true);

        Runnable applyFilters = () -> {
            String filterText = searchField.getText().toLowerCase();
            LocalDate selectedDate = searchDatePicker.getValue();

            filteredData.setPredicate(hotel -> {
                boolean matchesText = hotel.getNom().toLowerCase().contains(filterText)
                        || hotel.getLocalisation().toLowerCase().contains(filterText)
                        || String.valueOf(hotel.getNbEtoile()).contains(filterText)
                        || String.valueOf(hotel.getTarif()).contains(filterText);

                boolean matchesDate = true;
                if (selectedDate != null && hotel.getDate() != null) {
                    LocalDate hotelDate = new java.sql.Date(hotel.getDate().getTime()).toLocalDate();
                    matchesDate = selectedDate.equals(hotelDate);
                }

                return matchesText && matchesDate;
            });
        };

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());
        searchDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> applyFilters.run());

        SortedList<Hotel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(hotelTable.comparatorProperty());
        hotelTable.setItems(sortedData);

        addActionButtonsToTable();
    }


    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());

        // Optionally, print the stack trace to the console
        e.printStackTrace();

        alert.showAndWait();
    }

    public void loadHotels() throws SQLException {
        hotelList.setAll(crudHotel.afficher());
    }

    private void setupSearchFilter() {
        FilteredList<Hotel> filteredData = new FilteredList<>(hotelList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hotel -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (hotel.getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (hotel.getLocalisation().toLowerCase().contains(lowerCaseFilter)) return true;
                if (String.valueOf(hotel.getNbEtoile()).contains(lowerCaseFilter)) return true;
                if (String.valueOf(hotel.getTarif()).contains(lowerCaseFilter)) return true;
                return false;
            });
        });

        SortedList<Hotel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(hotelTable.comparatorProperty());
        hotelTable.setItems(sortedData);
    }

    private void addActionButtonsToTable() {
        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox container = new HBox();

            {
                // Create FontAwesome icons
                FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

                // Set icon properties
                editIcon.setSize("16px");
                editIcon.setStyleClass("edit-icon");
                deleteIcon.setSize("16px");
                deleteIcon.setStyleClass("delete-icon");

                // Configure buttons with icons
                editButton.setGraphic(editIcon);
                editButton.setText("Edit"); // Optional text
                editButton.setContentDisplay(ContentDisplay.LEFT);
                editButton.setGraphicTextGap(5);

                deleteButton.setGraphic(deleteIcon);
                deleteButton.setText("Delete"); // Optional text
                deleteButton.setContentDisplay(ContentDisplay.LEFT);
                deleteButton.setGraphicTextGap(5);

                // Set button styles
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");

                // Set container properties
                container.getStyleClass().add("actions-container");
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(editButton, deleteButton);

                // Edit button action
                editButton.setOnAction(event -> {
                    Hotel hotel = getTableView().getItems().get(getIndex());
                    openEditHotelView(hotel);
                });

                deleteButton.setOnAction(event -> {
                    Hotel hotel = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to delete this hotel?");
                    alert.setContentText("Hotel: " + hotel.getNom());

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                System.out.println("Deleting hotel with ID: " + hotel.getIdHotel());  // Debug
                                crudHotel.delete(hotel.getIdHotel());
                                loadHotels();  // Refresh instead of removing manually

                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Success");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Hotel deleted successfully!");
                                successAlert.showAndWait();
                            } catch (SQLException e) {
                                showError("Error deleting hotel", e);
                            }
                        }
                    });
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        hotelTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleAddHotel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/hotel.fxml"));
            Parent root = loader.load();

            AddHotel controller = loader.getController();
            controller.setRefreshCallback(() -> {
                try {
                    loadHotels();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Add Hotel");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditHotelView(Hotel hotel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/edithotel.fxml"));
            Parent root = loader.load();

            EditHotel controller = loader.getController();
            controller.setHotel(hotel);
            controller.setRefreshCallback(() -> {
                try {
                    loadHotels();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Edit Hotel");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGeneratePdf() {
        generateHotelsPdf();
    }

    private void generateHotelsPdf() {
        try {
            // Create PDFs directory in project root if it doesn't exist
            File projectRoot = new File(System.getProperty("user.dir"));
            File pdfDir = new File(projectRoot, "/pdfs");
            if (!pdfDir.exists()) {

                pdfDir.mkdir();
            }

            // Create a unique filename with timestamp
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String filename = pdfDir.getAbsolutePath() + File.separator + "hotels-list_" + timestamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title and generation date
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            Paragraph title = new Paragraph("Hotel List", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(10);

            com.lowagie.text.Font dateFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC);
            Paragraph generationDate = new Paragraph("Generated on: " + new java.text.SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss").format(new java.util.Date()), dateFont);
            generationDate.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            generationDate.setSpacingAfter(20);

            document.add(title);
            document.add(generationDate);

            // Create table
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(6); // 6 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {2.5f, 2.5f, 1f, 1f, 1.5f, 1.5f};
            table.setWidths(columnWidths);

            // Add table headers
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD);
            String[] headers = {"Hotel Name", "Location", "Stars", "Room Type", "Price", "Date"};
            for (String header : headers) {
                com.lowagie.text.Phrase phrase = new com.lowagie.text.Phrase(header, headerFont);
                phrase.setFont(new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD, java.awt.Color.WHITE));
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(phrase);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                cell.setBackgroundColor(new java.awt.Color(51, 122, 183)); // Bootstrap primary blue
                cell.setBorderColor(new java.awt.Color(46, 109, 164)); // Slightly darker blue for border
                table.addCell(cell);
            }

            // Add hotel data
            com.lowagie.text.Font contentFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);
            boolean alternateRow = false;
            for (Hotel hotel : hotelList) {
                // Create cells with proper formatting
                com.lowagie.text.pdf.PdfPCell nameCell = createContentCell(hotel.getNom(), contentFont, alternateRow);
                com.lowagie.text.pdf.PdfPCell locationCell = createContentCell(hotel.getLocalisation(), contentFont, alternateRow);
                com.lowagie.text.pdf.PdfPCell starsCell = createContentCell(hotel.getNbEtoile() + " Stars", contentFont, alternateRow);
                com.lowagie.text.pdf.PdfPCell roomCell = createContentCell(hotel.getChambre(), contentFont, alternateRow);
                com.lowagie.text.pdf.PdfPCell priceCell = createContentCell(String.format("$%.2f", hotel.getTarif()), contentFont, alternateRow);
                com.lowagie.text.pdf.PdfPCell dateCell = createContentCell(hotel.getDate().toString(), contentFont, alternateRow);

                // Add cells to table
                table.addCell(nameCell);
                table.addCell(locationCell);
                table.addCell(starsCell);
                table.addCell(roomCell);
                table.addCell(priceCell);
                table.addCell(dateCell);

                alternateRow = !alternateRow;
            }

            document.add(table);

            // Add footer with total count
            Paragraph footer = new Paragraph(
                    String.format("Total Hotels: %d", hotelList.size()),
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC)
            );
            footer.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("PDF Generated");
            alert.setContentText("PDF has been generated successfully!\nSaved as: " + filename);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("PDF Generation Failed");
            alert.setContentText("Failed to generate PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private com.lowagie.text.pdf.PdfPCell createContentCell(String content, com.lowagie.text.Font font, boolean alternateRow) {
        com.lowagie.text.Phrase phrase = new com.lowagie.text.Phrase(content, font);
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(phrase);
        cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
        cell.setPadding(6);

        // Set background color for alternate rows
        if (alternateRow) {
            cell.setBackgroundColor(new java.awt.Color(245, 245, 245));
        } else {
            cell.setBackgroundColor(java.awt.Color.WHITE);
        }

        // Set border color
        cell.setBorderColor(new java.awt.Color(221, 221, 221));
        return cell;
    }

    @FXML
    private void handleViewPDFs() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/pdfviewer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("PDF Reports");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open PDF viewer");
            alert.setContentText("Error loading PDF viewer: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

