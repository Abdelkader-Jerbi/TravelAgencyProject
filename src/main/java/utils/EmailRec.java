package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailRec {
    private static final String FROM_EMAIL = "ghozlene.nezhi@gmail.com";
    private static final String APP_PASSWORD = "xmkl lntu agnj iyck";

    public static void sendEmail(String to, String subject, String content) {
        System.out.println("Début de l'envoi d'email à : " + to);
        
        // Configuration des propriétés
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true"); // Activer le mode debug

        System.out.println("Configuration SMTP terminée");

        // Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("Authentification avec : " + FROM_EMAIL);
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            System.out.println("Création du message...");
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            System.out.println("Envoi du message...");
            // Envoi du message
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + to);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            if (e instanceof AuthenticationFailedException) {
                System.err.println("ERREUR D'AUTHENTIFICATION: Veuillez vérifier que le mot de passe d'application est correct");
            }
            e.printStackTrace();
        }
    }
}