package controllers;

import exceptions.EmptyFieldException;
import exceptions.IncorrectPasswordException;
import exceptions.UserNotFoundException;
import models.User;
import services.ClientService;
import services.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeMdpController {

    @FXML
    private PasswordField newPasswordField, confirmPasswordField;

    @FXML
    private Label errorLabel;

    private ClientService clientService = ClientService.getInstance();
    private EmailService emailService = new EmailService();

    @FXML
    void changePassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.equals(confirmPassword)) {
            User loggedInUser = ClientService.getLoggedInUser();
            if (loggedInUser == null) {
                errorLabel.setText("Aucun utilisateur connecté.");
                errorLabel.setTextFill(Color.RED);
                errorLabel.setVisible(true);
                return;
            }

            try {
                clientService.updatePassword(loggedInUser.getId(), newPassword);
                errorLabel.setText("Mot de passe changé avec succès.");
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setVisible(true);

                // Send email notification
                emailService.sendPasswordChangeEmail(loggedInUser.getEmail());

                // Redirect to Home.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setCurrentUser(loggedInUser);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (UserNotFoundException | IncorrectPasswordException | EmptyFieldException | IOException e) {
                errorLabel.setText("Erreur lors de la mise à jour du mot de passe.");
                errorLabel.setTextFill(Color.RED);
                errorLabel.setVisible(true);
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            errorLabel.setVisible(true);
        }
    }
}
