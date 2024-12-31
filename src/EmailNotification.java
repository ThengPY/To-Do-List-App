import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailNotification {

    public static void sendEmail(String recipient, String subject, String content) {
        // Email configuration
        final String senderEmail = "thengpy05@gmail.com"; // Replace with your email
        final String senderAppPassword = "piei vvru jfxr rkhj";  // Replace with your app password

        // SMTP server settings for Gmail
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587"); // Port for TLS

        // Set up the session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderAppPassword);
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));  // Sender email address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));  // Recipient email address
            message.setSubject(subject);  // Email subject
            message.setText(content);  // Email body content

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }

    public static void main(String[] args) {
        // Test email details
        String testRecipient = "thengpy05@gmail.com"; // Replace with a valid recipient email
        String testSubject = "Test Email Subject";
        String testContent = "This is a test email sent from the Java program.";

        System.out.println("Starting email test...");

        try {
            // Call the sendEmail method from EmailNotification class
            EmailNotification.sendEmail(testRecipient, testSubject, testContent);
            System.out.println("Test email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send test email.");
            e.printStackTrace();
        }
    }
}

