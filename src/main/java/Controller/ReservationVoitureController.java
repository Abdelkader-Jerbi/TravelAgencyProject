package Controller;

import entities.Reservation;
import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import services.CrudReservationVoiture;
import services.CrudVoiture;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.util.Properties;
import javafx.scene.Parent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;


public class ReservationVoitureController {

    @FXML private Label marqueLabel;
    @FXML private Label modeleLabel;
    @FXML private Label prixLabel;
    @FXML private Label matriculeCol;
    @FXML private Label disponibleLabel;
    @FXML private Label notificationLabel;
    @FXML private Label carburantLabel;
    @FXML private Label boiteVitesseLabel;

    @FXML private javafx.scene.control.TextField nomClientField;
    @FXML private javafx.scene.control.TextField prenomClientField;

    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @FXML private ComboBox<String> villeDepartCombo;
    @FXML private ComboBox<String> villeRetourCombo;

    @FXML private CheckBox chauffeurCheckBox;

    @FXML private Button btnRechercher;
    @FXML private Button annulerButton;

    @FXML private ImageView voitureImage;

    private Voiture voiture;
    private final CrudReservationVoiture crudReservationVoiture = new CrudReservationVoiture();
    private int compteurPanier = 0;

    // Ajout d'une méthode statique pour le compteur global du panier
    private static int globalCartCount = 0;
    public static void incrementGlobalCart() {
        globalCartCount++;
    }
    public static int getGlobalCartCount() {
        return globalCartCount;
    }

    @FXML
    public void initialize() {
        // Initialisation des villes
        String[] villes = {
                "Tunis", "Ariana", "Ben Arous", "La Manouba", "Nabeul", "Zaghouan", "Bizerte", "Béja",
                "Jendouba", "Le Kef", "Siliana", "Sousse", "Monastir", "Mahdia", "Sfax", "Kairouan",
                "Kasserine", "Sidi Bouzid", "Gabès", "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kebili"
        };

        villeDepartCombo.getItems().addAll(villes);
        villeRetourCombo.getItems().addAll(villes);

        // Ajouter un listener sur le CheckBox chauffeur
        chauffeurCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updatePrixTotal();
        });

        // Ajouter des listeners sur les DatePickers
        dateDebutPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePrixTotal();
        });

        dateFinPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePrixTotal();
        });

        // Listener pour appliquer le thème dynamiquement
        btnRechercher.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Parent root = newScene.getRoot();
                // Appliquer le style immédiatement selon l'état courant
                if (ThemeManager.isDarkMode()) {
                    if (!root.getStyleClass().contains("dark-root")) {
                        root.getStyleClass().add("dark-root");
                    }
                } else {
                    root.getStyleClass().remove("dark-root");
                }
                // Puis écouter les changements
                ThemeManager.darkModeProperty().addListener((o, oldVal, newVal) -> {
                    if (newVal) {
                        if (!root.getStyleClass().contains("dark-root")) {
                            root.getStyleClass().add("dark-root");
                        }
                    } else {
                        root.getStyleClass().remove("dark-root");
                    }
                });
            }
        });
    }

    private void updatePrixTotal() {
        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null && voiture != null) {
            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();
            
            if (debut.isAfter(fin)) {
                return; // Ne pas calculer si les dates sont invalides
            }

            long dureeLocation = java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;
            double prixBase = dureeLocation * voiture.getPrixParJour();
            double prixChauffeur = chauffeurCheckBox.isSelected() ? dureeLocation * 50.0 : 0;
            double fraisService = (prixBase + prixChauffeur) * 0.05;
            float prixTotal = (float) (prixBase + prixChauffeur + fraisService);

            // Mettre à jour l'affichage du prix
            prixLabel.setText(String.format("Prix total : %.2f DT", prixTotal));
            
            // Ajouter un style spécial si le chauffeur est sélectionné
            if (chauffeurCheckBox.isSelected()) {
                prixLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            } else {
                prixLabel.setStyle("");
            }
        }
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
        if (voiture != null) {
            marqueLabel.setText("Marque : " + voiture.getMarque());
            modeleLabel.setText("Modèle : " + voiture.getModele());
            prixLabel.setText("Prix : " + voiture.getPrixParJour() + " DT/Jour");
            matriculeCol.setText(voiture.getMatricule());
            carburantLabel.setText(voiture.getCarburant());
            boiteVitesseLabel.setText(voiture.getBoiteVitesse());
            disponibleLabel.setText(voiture.isDisponible() ? "Disponible" : "Non disponible");
            disponibleLabel.setStyle(voiture.isDisponible() ? "-fx-text-fill: #2e7d32;" : "-fx-text-fill: #c62828;");
            loadImage(voiture.getImagePath());
        }
    }

    private void loadImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                if (imagePath.startsWith("file:/")) {
                    // Si c'est déjà une URL file:/, utiliser directement
                    voitureImage.setImage(new Image(imagePath));
                } else {
            File file = new File(imagePath);
                    if (!file.isAbsolute()) {
                        // Si le chemin est relatif, le rendre absolu par rapport au projet
                        file = new File(System.getProperty("user.dir"), imagePath);
                    }
            if (file.exists()) {
                voitureImage.setImage(new Image(file.toURI().toString()));
            } else {
                        System.out.println("Image introuvable : " + file.getAbsolutePath());
                        loadDefaultImage();
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
                loadDefaultImage();
            }
        } else {
            System.out.println("Chemin d'image invalide.");
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            String defaultImagePath = "src/main/resources/images/default_car.png";
            File defaultFile = new File(defaultImagePath);
            if (defaultFile.exists()) {
                voitureImage.setImage(new Image(defaultFile.toURI().toString()));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        // Vérification des champs obligatoires
        if (nomClientField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom est obligatoire");
            return false;
        }
        if (prenomClientField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prénom est obligatoire");
            return false;
        }
        if (villeDepartCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La ville de départ est obligatoire");
            return false;
        }
        if (villeRetourCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La ville de retour est obligatoire");
            return false;
        }
        if (dateDebutPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début est obligatoire");
            return false;
        }
        if (dateFinPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de fin est obligatoire");
            return false;
        }

        // Vérification des dates
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        LocalDate aujourdhui = LocalDate.now();

        if (dateDebut.isBefore(aujourdhui)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de début ne peut pas être dans le passé");
            return false;
        }
        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date de fin doit être après la date de début");
            return false;
        }

        // Vérification de la durée maximale de location (30 jours)
        long dureeLocation = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (dureeLocation > 30) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La durée de location ne peut pas dépasser 30 jours");
            return false;
        }

        // Vérification de la disponibilité de la voiture
        if (voiture != null && !voiture.isDisponible()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Cette voiture n'est plus disponible");
            return false;
        }

        return true;
    }

    private float calculerPrixTotal(LocalDate debut, LocalDate fin, double prixParJour, boolean chauffeur) {
        // Calcul du nombre de jours (inclusif)
        long dureeLocation = java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;
        
        // Calcul du prix de base
        double prixBase = dureeLocation * prixParJour;
        
        // Ajout du coût du chauffeur si sélectionné
        double prixChauffeur = 0;
        if (chauffeur) {
            prixChauffeur = dureeLocation * 50.0; // 50 DT/jour pour chauffeur
        }
        
        // Calcul des frais de service (5%)
        double fraisService = (prixBase + prixChauffeur) * 0.05;
        
        // Calcul du total
        double total = prixBase + prixChauffeur + fraisService;
        
        return (float) total;
    }

    private void mettreAJourDisponibiliteVoiture(boolean disponible) {
        try {
            CrudVoiture crudVoiture = new CrudVoiture();
            voiture.setDisponible(disponible);
            crudVoiture.mettreAJourDisponibilite(voiture.getId(), disponible);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de la disponibilité : " + e.getMessage());
        }
    }

    private File genererFacturePDF(String nom, String prenom, String matricule, String modele, String marque, String carburant, String boiteVitesse,
                                  String villeDepart, String villeRetour, String dateDebut, String dateFin, float montant) {
        File pdfFile = null;
        try {
            pdfFile = File.createTempFile("facture_", ".pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Logo
            try {
                String logoPath = "src/main/resources/images/logo.png"; // adapte le chemin si besoin
                File logoFile = new File(logoPath);
                if (logoFile.exists()) {
                    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoFile.getAbsolutePath());
                    logo.scaleToFit(100, 100);
                    logo.setAlignment(Element.ALIGN_LEFT);
                    document.add(logo);
                }
            } catch (Exception e) {
                // Si le logo n'est pas trouvé, on continue sans
            }

            // Nom de l'agence et date
            Paragraph agence = new Paragraph("TravelAgency", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD));
            agence.setAlignment(Element.ALIGN_RIGHT);
            document.add(agence);

            Paragraph date = new Paragraph("Date : " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), new Font(Font.FontFamily.HELVETICA, 12));
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.add(new Paragraph(" ")); // espace

            // Titre
            Paragraph titre = new Paragraph("FACTURE", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            document.add(new Paragraph(" ")); // espace

            // Tableau des détails
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes
            table.addCell("Nom du client");
            table.addCell(nom + " " + prenom);
            table.addCell("Matricule voiture");
            table.addCell(matricule);
            table.addCell("Modèle");
            table.addCell(modele);
            table.addCell("Marque");
            table.addCell(marque);
            table.addCell("Carburant");
            table.addCell(carburant);
            table.addCell("Boîte de vitesse");
            table.addCell(boiteVitesse);
            table.addCell("Ville de départ");
            table.addCell(villeDepart);
            table.addCell("Ville de retour");
            table.addCell(villeRetour);
            table.addCell("Date début");
            table.addCell(dateDebut);
            table.addCell("Date fin");
            table.addCell(dateFin);

            document.add(table);

            // Montant total
            Paragraph montantTotal = new Paragraph("Montant total à payer : " + String.format("%.2f", montant) + " DT", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            montantTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(montantTotal);

            document.add(new Paragraph(" ")); // espace

            // Signature
            Paragraph signature = new Paragraph("Signature de l'agence :", new Font(Font.FontFamily.HELVETICA, 12));
            signature.setSpacingBefore(30f);
            document.add(signature);

            document.add(new Paragraph(" ")); // espace pour la signature
            document.add(new Paragraph("_________________________", new Font(Font.FontFamily.HELVETICA, 12)));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération de la facture : " + e.getMessage());
        }
        return pdfFile;
    }

    private void sendFactureEmailWithAttachment(String toEmail, String nom, String prenom, File pdfFile) {
        final String fromEmail = "gharbioumayma888@gmail.com";
        final String password = "tmcfmimebhukptzi"; // mot de passe d'application SANS espace

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Votre facture de réservation - TravelAgency");

            // Corps du mail
            String text = "Bonjour " + nom + " " + prenom + ",\n\nVeuillez trouver en pièce jointe votre facture de réservation.\nMerci pour votre confiance !";
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text);

            // Pièce jointe PDF
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(pdfFile);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Facture envoyée par email !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'envoi de la facture par email : " + e.getMessage());
        }
    }

    @FXML
    private void passerAPaiement(ActionEvent event) {
        if (!validerChamps()) {
            return;
        }

        try {
        java.sql.Date dateReservation = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date dateDebut = java.sql.Date.valueOf(dateDebutPicker.getValue());
        java.sql.Date dateFin = java.sql.Date.valueOf(dateFinPicker.getValue());

        String villeDepart = villeDepartCombo.getValue();
        String villeRetour = villeRetourCombo.getValue();
            String nom = nomClientField.getText().trim();
            String prenom = prenomClientField.getText().trim();

            // Calcul détaillé du prix
            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();
            long dureeLocation = java.time.temporal.ChronoUnit.DAYS.between(debut, fin) + 1;
            double prixBase = dureeLocation * voiture.getPrixParJour();
            double prixChauffeur = chauffeurCheckBox.isSelected() ? dureeLocation * 50.0 : 0;
            double fraisService = (prixBase + prixChauffeur) * 0.05;
            float prixTotal = (float) (prixBase + prixChauffeur + fraisService);

        String matricule = voiture != null ? voiture.getMatricule() : "";
        String modele = voiture != null ? voiture.getModele() : "";

            // Création de la réservation
        Reservation reservation = new Reservation(
                0, // ID auto-généré
                dateReservation,
                dateDebut,
                dateFin,
                villeRetour,
                villeDepart,
                prixTotal,
                1, // Nombre de personnes
                "En attente",
                voiture,
                nom,
                prenom
        );

            // Ajout de la réservation dans la base de données
        crudReservationVoiture.ajouterReservation(reservation);

            // Génération de la facture PDF
            File facture = genererFacturePDF(
                nom, prenom, matricule, modele, voiture.getMarque(), voiture.getCarburant(), voiture.getBoiteVitesse(),
                villeDepart, villeRetour, dateDebut.toString(), dateFin.toString(), prixTotal
            );
            if (facture != null && facture.exists()) {
                System.out.println("Facture générée : " + facture.getAbsolutePath());
                // Envoi de la facture par email en pièce jointe
                sendFactureEmailWithAttachment("gharbioumayma888@gmail.com", nom, prenom, facture); // à remplacer par l'email du client si besoin
            }

            // Envoi d'un email de confirmation simple
            sendConfirmationEmail("gharbioumayma888@gmail.com", nom, prenom); // à remplacer par l'email du client si besoin

            // Mise à jour de la disponibilité de la voiture
            mettreAJourDisponibiliteVoiture(false);

            // Mise à jour du compteur de panier
        compteurPanier++;
        incrementGlobalCart();

            // Mise à jour de l'affichage du compteur avec animation
            if (notificationLabel != null) {
                notificationLabel.setText(String.valueOf(compteurPanier));
                notificationLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px; -fx-font-weight: bold;");
                
                // Animation de notification
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), notificationLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                // Animation de rebond
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), notificationLabel);
                scaleTransition.setFromX(1.0);
                scaleTransition.setFromY(1.0);
                scaleTransition.setToX(1.2);
                scaleTransition.setToY(1.2);
                scaleTransition.setAutoReverse(true);
                scaleTransition.setCycleCount(2);
                scaleTransition.play();
            }

            // Génération du récapitulatif détaillé
            String recap = "Récapitulatif de la réservation :\n" +
                    "----------------------------------------\n" +
                    "Informations client :\n" +
                    "Nom : " + nom + "\n" +
                    "Prénom : " + prenom + "\n" +
                    "----------------------------------------\n" +
                    "Informations véhicule :\n" +
                    "Matricule : " + matricule + "\n" +
                    "Modèle : " + modele + "\n" +
                    "----------------------------------------\n" +
                    "Détails de la location :\n" +
                    "Ville de départ : " + villeDepart + "\n" +
                    "Ville de retour : " + villeRetour + "\n" +
                    "Date de début : " + dateDebut + "\n" +
                    "Date de fin : " + dateFin + "\n" +
                    "Durée : " + dureeLocation + " jour(s)\n" +
                    "Avec chauffeur : " + (chauffeurCheckBox.isSelected() ? "Oui" : "Non") + "\n" +
                    "----------------------------------------\n" +
                    "Détail du prix :\n" +
                    "Prix de base (" + dureeLocation + " jours × " + voiture.getPrixParJour() + " DT) : " + prixBase + " DT\n" +
                    (chauffeurCheckBox.isSelected() ? "Prix chauffeur (" + dureeLocation + " jours × 50 DT) : " + prixChauffeur + " DT\n" : "") +
                    "Frais de service (5%) : " + fraisService + " DT\n" +
                    "----------------------------------------\n" +
                    "Total à payer : " + prixTotal + " DT";

            // Afficher le récapitulatif
            showAlert(Alert.AlertType.INFORMATION, "Réservation confirmée", recap);

            // Fermer la fenêtre de réservation
            Stage stage = (Stage) btnRechercher.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la réservation : " + e.getMessage());
        }
    }

    @FXML
    private void closeWindow() {
        if (annulerButton != null && annulerButton.getScene() != null) {
            annulerButton.getScene().getWindow().hide();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Méthode d'envoi d'email de confirmation simple
    private void sendConfirmationEmail(String toEmail, String nom, String prenom) {
        final String fromEmail = "gharbioumayma888@gmail.com"; // ton adresse Gmail
        final String password = "tmcfmimebhukptzi"; // mot de passe d'application SANS espace

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de réservation");
            message.setText("Bonjour " + nom + " " + prenom + ",\n\nVotre réservation a bien été enregistrée.\nMerci pour votre confiance !");

            Transport.send(message);
            System.out.println("Email de confirmation envoyé !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

}
