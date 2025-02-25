package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;
import services.SessionManager; // Importer SessionManager
import exceptions.UserNotFoundException;
import util.Type;

import java.io.IOException;
import java.time.LocalDateTime;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Label errorLabel;

    private UserService userService = UserService.getInstance();
    private ClientService clientService = ClientService.getInstance();
    private GuideService guideService = GuideService.getInstance();

    @FXML
    public void initialize() {
        // Vérifier la session au démarrage
        String[] session = SessionManager.loadSession();
        if (session != null && session.length == 3) { // Maintenant, la session contient email, rôle et timestamp
            String email = session[0];
            String role = session[1];
            LocalDateTime lastLogin = LocalDateTime.parse(session[2]);

            // Vérifier si la session est encore valide (exemple : moins de 24 heures)
            if (lastLogin.isAfter(LocalDateTime.now().minusHours(24))) {
                try {
                    User user = null;
                    switch (Type.valueOf(role)) { // Convertir le rôle en enum
                        case ADMIN:
                            user = userService.getUserbyEmail(email);
                            break;
                        case CLIENT:
                            user = clientService.getUserbyEmail(email);
                            break;
                        case GUIDE:
                            user = guideService.getUserbyEmail(email);
                            break;
                    }
                    if (user != null) {
                        final User finalUser = user;
                        javafx.application.Platform.runLater(() -> redirectToHome(finalUser));
                        return;
                    }
                } catch (UserNotFoundException e) {
                    // Ignorer si l'utilisateur n'est pas trouvé
                }
            }
        }

        // Initialisation normale si aucune session valide n'est trouvée
        if (loginButton == null) {
            System.out.println("ERREUR : loginButton est NULL !");
        } else {
            loginButton.getStyleClass().add("login_button");
            loginButton.setOnAction(event -> handleLogin());
        }

        if (signUpButton != null) {
            signUpButton.setOnAction(event -> handleSignUp());
        }
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        try {
            User user = null;

            try {
                user = userService.getUserbyEmail(email);
            } catch (UserNotFoundException e) {
                // Handle exception if ADMIN user is not found, continue to next service
            }

            if (user == null) {
                try {
                    user = clientService.getUserbyEmail(email);
                } catch (UserNotFoundException e) {
                    // Continue to next service
                }
            }

            if (user == null) {
                try {
                    user = guideService.getUserbyEmail(email);
                } catch (UserNotFoundException e) {
                    showError("Aucun utilisateur trouvé avec cet email.");
                    return;
                }
            }

            if (user != null && verifyPassword(user, password)) {
                UserService.setLoggedInUser(user);
                SessionManager.saveSession(user.getEmail(), user.getRoles().toString()); // Sauvegarder la session avec le rôle
                redirectToHome(user);
            } else {
                showError("Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            showError("Une erreur s'est produite lors de la connexion.");
            e.printStackTrace();
        }
    }

    private boolean verifyPassword(User user, String password) {
        switch (user.getRoles()) {
            case ADMIN:
                return userService.verifyPassword(password, user.getPassword());
            case CLIENT:
                return clientService.verifyPassword(password, user.getPassword());
            case GUIDE:
                return guideService.verifyPassword(password, user.getPassword());
            default:
                return false;
        }
    }

    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Impossible de charger la page d'inscription.");
            e.printStackTrace();
        }
    }

    private void redirectToHome(User user) {
        try {
            System.out.println("Tentative de redirection pour l'utilisateur : " + user.getEmail() + " (Rôle : " + user.getRoles() + ")");
            FXMLLoader loader;
            if (user.getRoles() == Type.ADMIN) {
                loader = new FXMLLoader(getClass().getResource("/views/Clients.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            }
            Parent root = loader.load();

            if (user.getRoles() == Type.ADMIN) {
                ClientsController clientsController = loader.getController();
                clientsController.setCurrentUser(user);
            } else {
                HomeController homeController = loader.getController();
                homeController.setCurrentUser(user);
            }

            Scene currentScene = emailField.getScene();
            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.err.println("Erreur : Impossible d'accéder à la scène actuelle.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}
