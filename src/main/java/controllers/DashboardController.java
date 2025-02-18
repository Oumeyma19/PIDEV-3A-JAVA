package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button profileButton;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize();
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getFirstname() + " " + currentUser.getLastname() + " !");
        }

        // Gérer le clic sur le bouton "Profil"
        profileButton.setOnAction(event -> {
            try {
                redirectToProfil();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void redirectToProfil() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
        Parent root = loader.load();

        // Passer les données de l'utilisateur au contrôleur ProfilController
        ProfilController profilController = loader.getController();
        profilController.setCurrentUser(currentUser);

        Stage stage = (Stage) profileButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
