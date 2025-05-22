package services;

import entities.Vol;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.text.SimpleDateFormat;

public class MailService {
    // Configuration SMTP
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "arwadridi624@gmail.com";
    private static final String PASSWORD = "jooj rrls ajqe wqcz"; // Mot de passe d'application

    public void envoyerPromotionAuxClients(Vol vol) {
        try {
            CrudUtilisateur crudClient = new CrudUtilisateur();
            List<String> emails = crudClient.getAllEmails();


            if (emails.isEmpty()) {
                System.out.println("Aucun email trouvé dans la base de données");
                return;
            }

            // Configuration de la session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            // Création du message
            String dateVol = new SimpleDateFormat("dd/MM/yyyy").format(vol.getDate());
            double prixPromo = vol.getPrix() * (1 - vol.getPourcentagePromotion() / 100.0);

            String sujet = "✈ Promotion exceptionnelle vers " + vol.getDestination();

            String messageText = String.format(
                    "Bonjour,\n\n" +
                            "Une nouvelle promotion est disponible !\n\n" +
                            "Départ: %s\n" +
                            "Destination: %s\n" +
                            "Date: %s\n" +
                            "Prix initial: %.2f€\n" +
                            "Réduction: %.0f%%\n" +
                            "Prix promotionnel: %.2f€\n\n" +
                            "Ne manquez pas cette offre exceptionnelle !",
                    vol.getDepart(), vol.getDestination(), dateVol,
                    vol.getPrix(), vol.getPourcentagePromotion(), prixPromo
            );

            // Envoi à tous les destinataires
            for (String email : emails) {
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(USERNAME, "Agence de Voyage"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject(sujet);
                    message.setText(messageText);

                    Transport.send(message);
                    System.out.println("Email envoyé à: " + email);

                    // Petite pause entre les envois
                    Thread.sleep(1000);

                } catch (Exception e) {
                    System.err.println("Erreur lors de l'envoi à " + email);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur générale dans l'envoi des emails");
            e.printStackTrace();
        }
    }
}