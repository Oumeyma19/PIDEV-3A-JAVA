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
import exceptions.UserNotFoundException;
import util.Type;

import java.io.IOException;

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
            if (user.getRoles() == Type.ADMIN) {
                redirectToDashboard(user); // Rediriger l'admin vers Dashboard.fxml
            } else {
                redirectToProfil(user); // Rediriger les autres rôles vers Profil.fxml
            }
        } catch (Exception e) {
            showError("Impossible de charger la page d'accueil.");
            e.printStackTrace();
        }
    }

    private void redirectToDashboard(User user) throws IOException {
        System.out.println("Redirection vers Dashboard.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
        Parent root = loader.load();

        // Passer les données de l'utilisateur au contrôleur DashboardController
        DashboardController dashboardController = loader.getController();
        dashboardController.setCurrentUser(user);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void redirectToProfil(User user) throws IOException {
        System.out.println("Redirection vers Profil.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
        Parent root = loader.load();

        // Passer les données de l'utilisateur au contrôleur ProfilController
        ProfilController profilController = loader.getController();
        profilController.setCurrentUser(user);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
}
