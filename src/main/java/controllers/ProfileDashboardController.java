package controllers;

import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import services.UserService;
import services.SessionManager;

import java.io.IOException;

public class ProfileDashboardController {

    @FXML
    private Text fullnameText;
    @FXML
    private ImageView userImage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private UserService userService = UserService.getInstance();

    // Ajoutez cette méthode pour définir l'administrateur actuel
    public void setCurrentAdmin(User admin) {



        if (admin != null) {
            nameField.setText(admin.getFirstname());
            prenomField.setText(admin.getLastname());
            emailField.setText(admin.getEmail());
            phoneField.setText(admin.getPhone());
        }
    }

    @FXML
    public void initialize() {
        nameField.setFocusTraversable(true);
        prenomField.setFocusTraversable(true);
        emailField.setFocusTraversable(true);
        phoneField.setFocusTraversable(true);
        // Récupérer l'utilisateur actuel depuis SessionManager
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            // Afficher les informations de l'utilisateur
            nameField.setText(currentUser.getFirstname());
            prenomField.setText(currentUser.getLastname());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
        } else {
            showMessage("Aucun utilisateur connecté.", "red");
        }
    }

    @FXML
    private void handleUpdateInfo() {
        // Récupérer l'utilisateur actuel depuis SessionManager
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            String name = nameField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            // Mettre à jour les informations de l'utilisateur
            currentUser.setFirstname(name);
            currentUser.setLastname(prenom);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);

            try {
                userService.updateBasicUserInfo(currentUser);
                showMessage("Informations mises à jour avec succès!", "green");
            } catch (EmptyFieldException e) {
                showMessage("Veuillez remplir tous les champs obligatoires.", "red");
            } catch (InvalidEmailException e) {
                showMessage("L'adresse email est invalide.", "red");
            } catch (InvalidPhoneNumberException e) {
                showMessage("Le numéro de téléphone est invalide.", "red");
            } catch (Exception e) {
                showMessage("Une erreur s'est produite lors de la mise à jour des informations: " + e.getMessage(), "red");
            }
        } else {
            showMessage("Aucun utilisateur connecté.", "red");
        }
    }

    @FXML
    private void handleChangePassword() {
        // Récupérer l'utilisateur actuel depuis SessionManager
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!newPassword.equals(confirmPassword)) {
                showMessage("Les mots de passe ne correspondent pas.", "red");
                return;
            }

            try {
                if (userService.verifyPassword(currentPassword, currentUser.getPassword())) {
                    userService.updatePassword(currentUser.getId(), newPassword);
                    showMessage("Mot de passe changé avec succès!", "green");
                } else {
                    showMessage("Mot de passe actuel incorrect.", "red");
                }
            } catch (EmptyFieldException | IncorrectPasswordException | UserNotFoundException e) {
                showMessage(e.getMessage(), "red");
            }
        } else {
            showMessage("Aucun utilisateur connecté.", "red");
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        SessionManager.clearSession(); // Effacer la session
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClientsClick(MouseEvent event) {
        navigateTo(event, "/views/Clients.fxml");
    }

    @FXML
    private void handleGuidesClick(MouseEvent event) {
        navigateTo(event, "/views/Guides.fxml");
    }

    @FXML
    private void handleDashClick(MouseEvent event) {
        navigateTo(event, "/views/Dashboard.fxml");
    }

    private void navigateTo(MouseEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
}
