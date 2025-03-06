package services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordResetService {

    // Informations d'identification Twilio
    private static final String ACCOUNT_SID = "AC5d6505247a4457f9deae041ec14d029e";
    private static final String AUTH_TOKEN = "719e8a040ea4334285036f24eac463ad";
    private static final String TWILIO_PHONE_NUMBER = "+19898489923";

    // Instance singleton
    private static PasswordResetService instance;

    // Map pour stocker les codes de vérification temporairement
    private Map<String, CodeWithTimestamp> verificationCodes = new HashMap<>();

    private PasswordResetService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static PasswordResetService getInstance() {
        if (instance == null) {
            instance = new PasswordResetService();
        }
        return instance;
    }

    public void sendVerificationCode(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Numéro de téléphone invalide.");
        }

        String formattedPhoneNumber = formatPhoneNumber(phoneNumber); // Formater le numéro
        String verificationCode = generateVerificationCode();

        try {
            // Envoyer le code via Twilio
            Message message = Message.creator(
                new PhoneNumber(formattedPhoneNumber), // Utiliser le numéro formaté
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Votre code de vérification est : " + verificationCode
            ).create();

            // Stocker le code en mémoire
            verificationCodes.put(formattedPhoneNumber, new CodeWithTimestamp(verificationCode, System.currentTimeMillis()));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi du SMS. Veuillez réessayer plus tard.");
        }
    }

    public boolean verifySMSCode(String phoneNumber, String code) {
        cleanExpiredCodes(); // Nettoyer les codes expirés

        // Récupérer le code stocké en mémoire
        CodeWithTimestamp codeWithTimestamp = verificationCodes.get(phoneNumber);

        if (codeWithTimestamp == null) {
            return false; // Aucun code trouvé pour ce numéro
        }

        // Comparer les codes
        return codeWithTimestamp.getCode().equals(code);
    }

    public void changePassword(String newPassword, String email) {
        try (Connection connection = MyConnection.getInstance().getConnection()) {
            String query = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, hashPassword(newPassword)); // Crypter le mot de passe
            statement.setString(2, email);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Génère un nombre entre 1000 et 9999
        return String.valueOf(code);
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private void cleanExpiredCodes() {
        long currentTime = System.currentTimeMillis();
        verificationCodes.entrySet().removeIf(entry -> {
            long codeTime = entry.getValue().getTimestamp();
            return currentTime - codeTime > 5 * 60 * 1000; // Supprimer les codes expirés
        });
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, null);
            return phoneUtil.isValidNumber(number);
        } catch (Exception e) {
            return false;
        }
    }

    public String formatPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, null);
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (Exception e) {
            throw new IllegalArgumentException("Numéro de téléphone invalide.");
        }
    }

    // Classe interne pour stocker le code et son timestamp
    private static class CodeWithTimestamp {
        private String code;
        private long timestamp;

        public CodeWithTimestamp(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
