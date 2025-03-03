package controllers;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import services.*;
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
import exceptions.*;
import util.Type;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private SessionService sessionService = SessionService.getInstance();
    private Map<String, Integer> loginAttemptsMap = new HashMap<>();
    private Timer timer;

    @FXML
    public void initialize() {
        // Vérifier la session au démarrage
        String[] session = SessionManager.loadSession();
        if (session != null && session.length == 3) {
            String email = session[0];
            String role = session[1];
            LocalDateTime lastLogin = LocalDateTime.parse(session[2]);

            if (lastLogin.isAfter(LocalDateTime.now().minusHours(24))) {
                try {
                    User user = null;
                    switch (Type.valueOf(role)) {
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
            // Vérifier si le compte est banni
            if (sessionService.isAccountLocked(email)) {
                throw new AccountLockedException("Votre compte est banni. Veuillez réinitialiser votre mot de passe.");
            }

            User user = null;
            try {
                user = userService.getUserbyEmail(email);
            } catch (UserNotFoundException e) {
                // Ignorer si l'utilisateur n'est pas trouvé
            }

            if (user == null) {
                try {
                    user = clientService.getUserbyEmail(email);
                } catch (UserNotFoundException e) {
                    // Ignorer si l'utilisateur n'est pas trouvé
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

            // Vérifier si le compte est banni (sauf pour les admins)
            if (user.getRoles() != Type.ADMIN && user.getIsBanned()) {
                throw new AccountLockedException("Votre compte est banni. Veuillez réinitialiser votre mot de passe.");
            }

            // Vérifier le mot de passe et rediriger
            if (verifyPassword(user, password)) {
                UserService.setLoggedInUser(user);
                SessionManager.saveSession(user.getEmail(), user.getRoles().toString());
                loginAttemptsMap.put(email, 0); // Réinitialiser les tentatives
                redirectToHome(user);
            } else {
                int attempts = loginAttemptsMap.getOrDefault(email, 0) + 1;
                loginAttemptsMap.put(email, attempts);

                if (attempts >= sessionService.MAX_LOGIN_ATTEMPTS) {
                    // Bannir le compte après trop de tentatives (sauf pour les admins)
                    if (user.getRoles() != Type.ADMIN) {
                        sessionService.lockAccount(email);
                        showError("Trop de tentatives infructueuses. Votre compte est banni.");
                    } else {
                        showError("Trop de tentatives infructueuses. Veuillez contacter l'administrateur.");
                    }
                } else {
                    showError("Email ou mot de passe incorrect. Tentatives restantes : " + (sessionService.MAX_LOGIN_ATTEMPTS - attempts));
                }
            }

        } catch (AccountLockedException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Une erreur s'est produite lors de la connexion.");
            e.printStackTrace();
        }
    }

    private boolean verifyPassword(User user, String password) {
        if (user.getRoles() != Type.ADMIN && user.getIsBanned()) {
            return false; // Le compte est banni, la connexion est refusée (sauf pour les admins)
        }
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
                loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            }
            Parent root = loader.load();

            if (user.getRoles() == Type.ADMIN) {
                DashboardController dashboardController = loader.getController();
                dashboardController.setCurrentUser(user);
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

        // Masquer le message après 5 secondes
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> errorLabel.setVisible(false));
        pause.play();
    }

    private void startCountdown(int seconds, String email) {
        if (timer != null) {
            timer.cancel(); // Annuler le timer existant
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int remainingSeconds = seconds;

            public void run() {
                javafx.application.Platform.runLater(() -> {
                    errorLabel.setText("Déverrouillage dans : " + remainingSeconds + "s");
                    errorLabel.setVisible(true);
                    loginButton.setDisable(true);
                });
                remainingSeconds--;
                if (remainingSeconds <= 0) {
                    timer.cancel();
                    javafx.application.Platform.runLater(() -> {
                        sessionService.unlockAccount(email);
                        errorLabel.setText("Votre compte a été déverrouillé. Vous pouvez réessayer.");
                        errorLabel.setVisible(true);
                        loginButton.setDisable(false);
                    });
                }
            }
        }, 0, 1000);
    }

    @FXML
    public void redirectToRandomCode() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Veuillez saisir votre e-mail.");
            return;
        }

        try {
            User user = clientService.getUserbyEmail(email);
            String phoneNumber = user.getPhone();
            PasswordResetService resetService = PasswordResetService.getInstance();
            resetService.sendVerificationCode(phoneNumber);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RandomCode.fxml"));
            Parent root = loader.load();
            RandomCodeController randomCodeController = loader.getController();
            randomCodeController.setPhoneNumber(phoneNumber);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (UserNotFoundException e) {
            showError("Aucun utilisateur trouvé avec cet e-mail.");
        } catch (IOException e) {
            showError("Une erreur s'est produite lors de la redirection.");
            e.printStackTrace();
        }
    }
}