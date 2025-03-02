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

    @FXML
    private Button clientsButton; // Bouton Clients

    @FXML
    private Button guidesButton; // Bouton Guides

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

        // Gestionnaire d'événements pour le bouton Clients
        clientsButton.setOnAction(event -> loadPage("/views/Clients.fxml"));

        // Gestionnaire d'événements pour le bouton Guides
        guidesButton.setOnAction(event -> loadPage("/views/Guides.fxml"));
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
}
