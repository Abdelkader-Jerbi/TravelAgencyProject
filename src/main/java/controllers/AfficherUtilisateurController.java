package controllers;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.CrudUtilisateur;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public class AfficherUtilisateurController {


    @javafx.fxml.FXML
    private TableColumn coltel;
    @javafx.fxml.FXML
    private TableView tvpersonne;
    @javafx.fxml.FXML
    private TableColumn colnom;
    @javafx.fxml.FXML
    private TableColumn colprenom;
    @javafx.fxml.FXML
    private TableColumn colemail;
    @javafx.fxml.FXML
    private TableColumn colrole;

    @FXML
    private TextField telTF;

    @FXML
    private TextField nomTF;

    @FXML
    private TextField prenomTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField roleTF;

    @FXML
    private TextField searchField;

    private ObservableList<Utilisateur> observableList = FXCollections.observableArrayList();


    @javafx.fxml.FXML
    void initialize() {

        CrudUtilisateur CrudUtilisateur =new CrudUtilisateur();
        try {

            observableList = FXCollections.observableArrayList(CrudUtilisateur.afficher());

            tvpersonne.setItems(observableList);
            colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            coltel.setCellValueFactory(new PropertyValueFactory<>("tel"));
            colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colrole.setCellValueFactory(new PropertyValueFactory<>("role"));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    public void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            tvpersonne.setItems(observableList);
            return;
        }

        ObservableList<Utilisateur> filteredList = FXCollections.observableArrayList();

        for (Utilisateur user : observableList) {
            String role = user.getRole() == null ? "" : user.getRole().toString().toLowerCase();
            if (user.getNom().toLowerCase().contains(searchText) ||
                    user.getPrenom().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText) ||
                    String.valueOf(user.getTel()).contains(searchText) ||
                    role.contains(searchText)) {
                filteredList.add(user);
            }
        }
        tvpersonne.setItems(filteredList);
    }

    @FXML
    public void supprimerUtilisateur(ActionEvent actionEvent) {
        int utilisateurSelectionne = tvpersonne.getSelectionModel().getSelectedIndex();
        Utilisateur utilisateur = (Utilisateur) tvpersonne.getSelectionModel().getSelectedItem();

        if (utilisateur == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
            return;
        }

        CrudUtilisateur crudUtilisateur = new CrudUtilisateur();
        int idUser = utilisateur.getId();
        tvpersonne.getItems().remove(utilisateurSelectionne);
        try {
            crudUtilisateur.supprimer(idUser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void afficherUtilisateur(ActionEvent actionEvent) {
        Utilisateur utilisateurSelectionne = (Utilisateur) tvpersonne.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UtilisateurInfo.fxml"));
                Parent root = loader.load();

                // Get the controller of the new scene
                UtilisateurInfo controller = loader.getController();

                // Send the selected user to the controller
                controller.setUtilisateur(utilisateurSelectionne);

                // Switch scenes
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Optional: Alert if nothing is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("utilisateur");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to view.");
            alert.showAndWait();
        }
    }

    public void modifierUtilisateur(ActionEvent actionEvent) {
        Utilisateur utilisateurSelectionne = (Utilisateur) tvpersonne.getSelectionModel().getSelectedItem();

        if (utilisateurSelectionne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUtilisateurAdmin.fxml"));
                Parent root = loader.load();

                // Get the controller of the new scene
                ModifierUtilisateurAdmin controller = loader.getController();

                // Send the selected user to the controller
                controller.setUtilisateur(utilisateurSelectionne);

                // Switch scenes
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Optional: Alert if nothing is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to update.");
            alert.showAndWait();
        }
    }

    public void RetourAjoutUtilisateur(ActionEvent Event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterUtilisateurAdmin.fxml"));
            Stage stage = (Stage)((Node) Event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    public void exportToPdf(ActionEvent event) {
        try {
            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            // Set default file name with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("users_list_" + timestamp + ".pdf");
            
            // Show save file dialog
            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            
            if (file != null) {
                // Create Document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                // Add title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph title = new Paragraph("Last Minute Travel - User List", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                
                // Add date
                Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                Paragraph date = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), dateFont);
                date.setAlignment(Element.ALIGN_CENTER);
                document.add(date);
                
                document.add(new Paragraph(" ")); // Add some space
                
                // Create table with 5 columns
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                
                // Set column widths
                float[] columnWidths = {20f, 20f, 15f, 30f, 15f};
                table.setWidths(columnWidths);
                
                // Add table headers
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                Stream.of("Name", "First Name", "Phone", "Email", "Role")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(new com.itextpdf.text.BaseColor(244, 102, 36)); // #f46624
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, headerFont));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPadding(8);
                        table.addCell(header);
                    });
                
                // Add data rows
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                
                ObservableList<Utilisateur> users = tvpersonne.getItems();
                for (Utilisateur user : users) {
                    // Name
                    PdfPCell cell = new PdfPCell(new Phrase(user.getNom(), dataFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    // First Name
                    cell = new PdfPCell(new Phrase(user.getPrenom(), dataFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    // Phone
                    cell = new PdfPCell(new Phrase(String.valueOf(user.getTel()), dataFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    // Email
                    cell = new PdfPCell(new Phrase(user.getEmail(), dataFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                    
                    // Role
                    String role = user.getRole() == null ? "" : user.getRole().toString();
                    cell = new PdfPCell(new Phrase(role, dataFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
                
                document.add(table);
                document.close();
                
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Export");
                alert.setHeaderText(null);
                alert.setContentText("PDF file has been successfully created at:\n" + file.getAbsolutePath());
                alert.showAndWait();
                
            }
        } catch (DocumentException | IOException e) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PDF Export Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while creating the PDF file:\n" + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}
