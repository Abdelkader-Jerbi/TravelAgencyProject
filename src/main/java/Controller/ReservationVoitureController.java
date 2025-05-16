package Controller;

import entities.Reservation;
import entities.Voiture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import services.CrudReservation;
import services.CrudVoiture;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileOutputStream;
import java.io.File;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Properties;

public class ReservationVoitureController {

    @FXML private Label marqueLabel;
    @FXML private Label modeleLabel;
    @FXML private Label prixLabel;
    @FXML private Label matriculeCol;
    @FXML private Label disponibleLabel;
    @FXML private Label notificationLabel;

    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @FXML private ComboBox<String> villeDepartCombo;
    @FXML private ComboBox<String> villeRetourCombo;

    @FXML private CheckBox chauffeurCheckBox;

    @FXML private Button reserverButton;
    @FXML private Button annulerButton;

    @FXML private ImageView imageView;

    private Voiture voiture;
    private final CrudReservation crudReservation = new CrudReservation();
    private int compteurPanier = 0;

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

        if (reserverButton != null) reserverButton.setOnAction(e -> handleReservation());
        if (annulerButton != null) annulerButton.setOnAction(e -> closeWindow());
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
        if (voiture != null) {
            marqueLabel.setText("Marque : " + voiture.getMarque());
            modeleLabel.setText("Modèle : " + voiture.getModele());
            prixLabel.setText("Prix : " + voiture.getPrixParJour() + " DT/Jour");
            loadImage(voiture.getImagePath());
        }
    }

    private void loadImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            } else {
                System.out.println("Image introuvable : " + imagePath);
            }
        } else {
            System.out.println("Chemin d’image invalide.");
        }
    }

    private void handleReservation() {
        if (voiture == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune voiture sélectionnée.");
            return;
        }

        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (dateDebut == null || dateFin == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner les deux dates.");
            return;
        }

        if (!dateDebut.isBefore(dateFin)) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "La date de fin doit être après la date de début.");
            return;
        }

        if (!voiture.isDisponible()) {
            showAlert(Alert.AlertType.WARNING, "Indisponible", "Cette voiture n'est pas disponible actuellement.");
            return;
        }

        if (crudReservation.reserverVoiture(voiture.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation réussie !");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Réservation échouée.");
        }
    }

    @FXML
    private void passerAPaiement(ActionEvent event) {
        if (!validerChamps()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs correctement.");
            return;
        }

        Date dateReservation = new Date(System.currentTimeMillis());
        Date dateDebut = Date.valueOf(dateDebutPicker.getValue());
        Date dateFin = Date.valueOf(dateFinPicker.getValue());

        String villeDepart = villeDepartCombo.getValue();
        String villeRetour = villeRetourCombo.getValue();

        float prixTotal = calculerPrixTotal(
                dateDebutPicker.getValue(),
                dateFinPicker.getValue(),
                voiture.getPrixParJour(),
                chauffeurCheckBox.isSelected()
        );

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
                voiture
        );

        crudReservation.ajouterReservation(reservation);
        compteurPanier++;
        notificationLabel.setText(String.valueOf(compteurPanier));

        ouvrirPagePaiement();
        sendReservationDetailsEmail(); // Envoi d'un e-mail de confirmation après la réservation
    }

    private boolean validerChamps() {
        return dateDebutPicker.getValue() != null &&
                dateFinPicker.getValue() != null &&
                villeDepartCombo.getValue() != null &&
                villeRetourCombo.getValue() != null;
    }

    private float calculerPrixTotal(LocalDate debut, LocalDate fin, double prixParJour, boolean chauffeur) {
        long diff = java.sql.Date.valueOf(fin).getTime() - java.sql.Date.valueOf(debut).getTime();
        int nbJours = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

        double total = nbJours * prixParJour;
        if (chauffeur) total += nbJours * 30.0; // 30 dt/jour pour chauffeur

        return (float) total;
    }

    private void ouvrirPagePaiement() {
        System.out.println("Redirection vers la page de paiement...");
    }

    private void closeWindow() {
        if (annulerButton != null && annulerButton.getScene() != null) {
            Stage stage = (Stage) annulerButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Nouvelle méthode pour envoyer un e-mail de confirmation
    private void sendReservationDetailsEmail() {
        final String fromEmail = "gharbioumayma888@gmail.com"; // Email de l'expéditeur
        final String password = "000"; // Remplacer par votre mot de passe d'application ou mot de passe réel (NON recommandé de coder en dur)

        // Propriétés de configuration pour l'authentification SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Crée une session de messagerie
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("gharbioumayma888@gmail.com")); // Remplacer par l'email du client
            message.setSubject("Détails de votre réservation de voiture");

            // Contenu du message
            String messageContent = generateReservationMessage();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(messageContent); // Corps du message avec détails de la réservation

            // Envoi de l'email sans pièce jointe
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            message.setContent(multipart);

            // Envoi de l'email
            Transport.send(message);
            System.out.println("E-mail avec détails de réservation envoyé avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Envoi de l'e-mail de réservation échoué.");
        }
    }

    // Générer le contenu de l'email avec les détails de la réservation
    private String generateReservationMessage() {
        float totalPrice = calculerPrixTotal(
                dateDebutPicker.getValue(),
                dateFinPicker.getValue(),
                voiture.getPrixParJour(),
                chauffeurCheckBox.isSelected()
        );

        return "Votre réservation a été confirmée ! Voici les détails :\n\n" +
                "Voiture réservée : " + voiture.getMarque() + " " + voiture.getModele() + "\n" +
                "Prix par jour : " + voiture.getPrixParJour() + " DT\n" +
                "Date de début : " + dateDebutPicker.getValue() + "\n" +
                "Date de fin : " + dateFinPicker.getValue() + "\n" +
                "Ville de départ : " + villeDepartCombo.getValue() + "\n" +
                "Ville de retour : " + villeRetourCombo.getValue() + "\n" +
                "Chauffeur inclus : " + (chauffeurCheckBox.isSelected() ? "Oui" : "Non") + "\n" +
                "Prix total : " + totalPrice + " DT\n\n" +
                "Merci pour votre réservation !\n" +
                "L'équipe de location de voiture";
    }

}
