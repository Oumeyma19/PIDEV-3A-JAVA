package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.User;
import java.io.IOException;
import java.sql.SQLException;

public class HomeController {

    @FXML
    private Button profileButton;
    @FXML
    private Button tourButton;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleProfile());
        }
        if (tourButton != null) {
            tourButton.setOnAction(event -> handleToursButtonClick());
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

    @FXML
    private void handleToursButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tours_view.fxml"));
            Parent root = loader.load();

            ToursViewController ToursViewController = loader.getController();
            ToursViewController.setCurrentUser(currentUser);

            Stage stage = (Stage) tourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
