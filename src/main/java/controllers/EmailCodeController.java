package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.SessionManager;

import java.io.IOException;

public class EmailCodeController {
    @FXML
    private TextField digit1, digit2, digit3, digit4;

    @FXML
    private Label InvalidCode;

    @FXML
    private Label emailLabel;

    private User user;
    private String verificationCode;

    public void setUserAndCode(User user, String verificationCode) {
        this.user = user;
        this.verificationCode = verificationCode;

        emailLabel.setText("Code envoyé à : " + user.getEmail()); // <-- Affichage de l'email

    }

    @FXML
    private void handleSubmit() throws IOException {
        String enteredCode = digit1.getText() + digit2.getText() + digit3.getText() + digit4.getText();

        if (enteredCode.equals(verificationCode)) {
            SessionManager.saveSession(user.getEmail(), user.getRoles().toString());

            // Code correct, rediriger vers SignIn.fxml
            redirectToHome();
        } else {
            // Code incorrect, afficher un message d'erreur
            InvalidCode.setText("Code de vérification invalide.");
            InvalidCode.setVisible(true);
        }
    }

    private void redirectToHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
        Parent root = loader.load();

        // Passer l'utilisateur connecté au contrôleur HomeController
        HomeController homeController = loader.getController();
        homeController.setCurrentUser(user);

        Stage stage = (Stage) digit1.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();

    }
}
