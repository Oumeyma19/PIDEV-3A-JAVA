package controllers;

import Util.Helpers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.User;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button profileButton;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleProfile());
        }
    }

    @FXML
    private Button navlistes;

    @FXML
    void afficherListeHebergements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/listesHeberg.fxml"));
            Parent root = loader.load();
            ListesHebergController listesHebergController = loader.getController();
            listesHebergController.setCurrentUser(currentUser);
            navlistes.getScene().setRoot(root);

        } catch (IOException e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des hébergements.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ProfilController
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
