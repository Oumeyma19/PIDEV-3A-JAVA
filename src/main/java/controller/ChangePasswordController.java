package controller;

import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;
import services.ValidationService;
import javafx.scene.image.ImageView;
import java.io.IOException;

public class ChangePasswordController {
    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private ImageView homeImage;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label nomUserLabel;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView logoutImage;

    private UserService userService = UserService.getInstance();
    private ClientService clientService = ClientService.getInstance();
    private GuideService guideService = GuideService.getInstance();
    private ValidationService validationService = new ValidationService();

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Mettre à jour le label immédiatement après avoir défini l'utilisateur
        if (nomUserLabel != null && currentUser != null) {
            nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
        }
    }

    @FXML
    public void initialize() {
        // Vérifier que l'utilisateur courant est défini
        if (currentUser != null) {
            nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Rediriger vers la page de connexion (SignIn.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            String oldPassword = oldPasswordField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Vérifier que l'ancien mot de passe est correct
            if (!verifyOldPassword(oldPassword)) {
                throw new IncorrectPasswordException("L'ancien mot de passe est incorrect.");
            }

            // Valider que le nouveau mot de passe n'est pas vide
            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new EmptyFieldException("Le nouveau mot de passe ne peut pas être vide.");
            }

            // Valider que le nouveau mot de passe et la confirmation correspondent
            if (!newPassword.equals(confirmPassword)) {
                throw new IncorrectPasswordException("Les mots de passe ne correspondent pas.");
            }

            // Mettre à jour le mot de passe dans la base de données
            updatePassword(newPassword);
            nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());

            errorLabel.setText("Mot de passe mis à jour avec succès !");
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setVisible(true);
        } catch (IncorrectPasswordException | EmptyFieldException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Une erreur s'est produite lors de la mise à jour du mot de passe.");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    private boolean verifyOldPassword(String oldPassword) {
        switch (currentUser.getRoles()) {
            case ADMIN:
                return userService.verifyPassword(oldPassword, currentUser.getPassword());
            case CLIENT:
                return clientService.verifyPassword(oldPassword, currentUser.getPassword());
            case GUIDE:
                return guideService.verifyPassword(oldPassword, currentUser.getPassword());
            default:
                throw new IllegalStateException("Rôle utilisateur non reconnu.");
        }
    }

    @FXML
    private void handleProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de Profil
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser); // Passer l'utilisateur connecté

            // Changer de scène
            Stage stage = (Stage) nomUserLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du profil : " + e.getMessage());
        }
    }

    private void updatePassword(String newPassword) throws UserNotFoundException, IncorrectPasswordException, EmptyFieldException {
        switch (currentUser.getRoles()) {
            case ADMIN:
                userService.updatePassword(currentUser.getId(), newPassword);
                break;
            case CLIENT:
                clientService.updatePassword(currentUser.getId(), newPassword);
                break;
            case GUIDE:
                guideService.updatePassword(currentUser.getId(), newPassword);
                break;
            default:
                throw new IllegalStateException("Rôle utilisateur non reconnu.");
        }
    }

    @FXML
    private void handleProfileClick(javafx.scene.input.MouseEvent event) {
        try {
            // Load the Profil.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Profil.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();

            // Pass the user data to the HomeController
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
