package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.User;
import services.SessionManager;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button profileButton;

    @FXML
    private Button clientsButton; // Bouton Clients

    @FXML
    private Button guidesButton; // Bouton Guides

    private User currentUser;

    @FXML
    private BorderPane borderPane;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur actuel depuis SessionManager
        User currentUser = SessionManager.getCurrentUser();


        // Gestionnaire d'événements pour le bouton Clients
        clientsButton.setOnAction(event -> loadPage("/views/Clients.fxml"));

        // Gestionnaire d'événements pour le bouton Guides
        guidesButton.setOnAction(event -> loadPage("/views/Guides.fxml"));

        loadStatsView();

    }

    private void loadStatsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StatsView.fxml"));
            Parent statsView = loader.load();
            borderPane.setCenter(statsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Méthode pour charger une page FXML.
     *
     * @param fxmlFile Chemin du fichier FXML à charger.
     */
    private void loadPage(String fxmlFile) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) clientsButton.getScene().getWindow();

            // Changer la scène
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page : " + fxmlFile);
        }
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        navigateToProfile(event);
    }

    private void navigateToProfile(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProfileDashboard.fxml"));
            Parent root = loader.load();

            ProfileDashboardController profileController = loader.getController();
            profileController.setCurrentAdmin(SessionManager.getCurrentUser()); // Utiliser SessionManager

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout(MouseEvent event) {
        SessionManager.clearSession(); // Clear the session
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
    private void handleReturnToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCurrentUser(SessionManager.getCurrentUser());

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page Home.fxml");
        }
    }


}
