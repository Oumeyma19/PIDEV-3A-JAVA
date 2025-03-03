package services;

import java.io.*;
import java.time.LocalDateTime;

public class SessionManager {

    private static final String SESSION_FILE = "session.txt";

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
    }
}