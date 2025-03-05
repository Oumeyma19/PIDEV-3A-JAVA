package services;

import exceptions.UserNotFoundException;
import models.User; // Assurez-vous d'importer la classe User
import util.Type;

import java.io.*;
import java.time.LocalDateTime;

public class SessionManager {

    private static final String SESSION_FILE = "session.txt";
    private static User currentUser; // Ajout d'un champ pour stocker l'utilisateur actuel

    // Vérifier si le fichier de session existe, sinon le créer
    private static void ensureSessionFileExists() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveSession(String email, String role) {
        ensureSessionFileExists(); // S'assurer que le fichier existe
        try (FileWriter writer = new FileWriter(SESSION_FILE)) {
            writer.write(email + "," + role + "," + LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Charger la session (appelé au démarrage de l'application)
    public static String[] loadSession() {
        ensureSessionFileExists(); // S'assurer que le fichier existe
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Effacer la session (appelé lors de la déconnexion)
    public static void clearSession() {
        ensureSessionFileExists(); // S'assurer que le fichier existe
        try (FileWriter writer = new FileWriter(SESSION_FILE)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentUser = null; // Réinitialiser l'utilisateur actuel
    }

    // Définir l'utilisateur actuel
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        String[] session = loadSession();
        if (session != null && session.length == 3) {
            String email = session[0];
            String role = session[1];
            LocalDateTime lastLogin = LocalDateTime.parse(session[2]);

            if (lastLogin.isAfter(LocalDateTime.now().minusHours(24))) {
                try {
                    switch (Type.valueOf(role)) {
                        case ADMIN:
                            return UserService.getInstance().getUserbyEmail(email);
                        case CLIENT:
                            return ClientService.getInstance().getUserbyEmail(email);
                        case GUIDE:
                            return GuideService.getInstance().getUserbyEmail(email);
                    }
                } catch (UserNotFoundException e) {
                    // Ignorer si l'utilisateur n'est pas trouvé
                }
            }
        }
        return null; // Aucun utilisateur connecté
    }
}