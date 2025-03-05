package services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private final String senderEmail = "carthagotravel@gmail.com";
    private final String senderPassword = "exsh xidf kpth puxk";

    public void sendPasswordChangeEmail(String recipientEmail) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation de changement de mot de passe");
            message.setText("Bonjour,\n\nVotre mot de passe a été changé avec succès.\n\nMerci pour votre confiance !");

            Transport.send(message);
            System.out.println("Email envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email.");
        }
    }

    public void sendVerificationEmail(String recipientEmail, String verificationCode) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Code de vérification pour votre inscription");
            message.setText("Bonjour,\n\nVotre code de vérification est : " + verificationCode + "\n\nMerci !");

            Transport.send(message);
            System.out.println("Email de vérification envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email de vérification.");
        }
    }
}
