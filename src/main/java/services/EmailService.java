package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL_FROM = "fatmabarrani11@gmail.com"; // Your Gmail address
    
    // ⚠️ IMPORTANT: Follow these steps to get the correct password:
    // 1. Go to https://myaccount.google.com/security and enable 2-Step Verification
    // 2. Then go to https://myaccount.google.com/apppasswords
    // 3. Select 'Mail' as the app
    // 4. Select 'Other (Custom name)' as the device
    // 5. Name it 'Travel Agency App'
    // 6. Click 'Generate'
    // 7. Copy the 16-character password shown in the yellow box
    // 8. Paste it here (remove any spaces)
    private static final String EMAIL_PASSWORD = "bedhdvdcxwlhepte";

    public static void sendBookingConfirmation(String toEmail, String hotelName, String location, double price, String roomType, int stars) {
        // Email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Hotel Booking Confirmation - " + hotelName);

            // Email content
            String emailContent = String.format(
                "Dear Valued Customer,\n\n" +
                "Thank you for choosing to stay with us! Your booking has been confirmed.\n\n" +
                "Booking Details:\n" +
                "----------------\n" +
                "Hotel: %s\n" +
                "Location: %s\n" +
                "Room Type: %s\n" +
                "Rating: %d Stars\n" +
                "Total Price: $%.2f\n\n" +
                "We look forward to welcoming you and ensuring you have a wonderful stay!\n\n" +
                "Best regards,\n" +
                "Your Travel Agency Team",
                hotelName, location, roomType, stars, price
            );

            message.setText(emailContent);

            // Send email
            Transport.send(message);
            System.out.println("Booking confirmation email sent successfully to " + toEmail);

        } catch (AuthenticationFailedException e) {
            System.err.println("\n⚠️ AUTHENTICATION ERROR ⚠️");
            System.err.println("Please follow these steps:");
            System.err.println("1. Go to https://myaccount.google.com/security");
            System.err.println("   - Enable 2-Step Verification if not already enabled");
            System.err.println("2. Go to https://myaccount.google.com/apppasswords");
            System.err.println("   - Select 'Mail' as the app");
            System.err.println("   - Select 'Other (Custom name)' as the device");
            System.err.println("   - Name it 'Travel Agency App'");
            System.err.println("   - Click 'Generate'");
            System.err.println("   - Copy the 16-character password");
            System.err.println("3. Replace EMAIL_PASSWORD in EmailService.java with the copied password");
            System.err.println("   - Remove any spaces from the password");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 