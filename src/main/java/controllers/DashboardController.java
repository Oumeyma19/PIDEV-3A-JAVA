package controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.User;

import java.io.IOException;

public class DashboardController {

    public Button AjouterButton;
    public Button OffresButton;
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button profileButton;

    @FXML
    private Button clientsButton; // Bouton Clients

    @FXML
    private Button guidesButton; // Bouton Guides

     @FXML
    private Button VolsButton;

     @FXML
    private Button AirportsButton;

    @FXML
    private VBox content;


    private User currentUser;



    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize();
    }

    @FXML
    public void initialize() {
       /* if (currentUser != null) {
            welcomeLabel.setText("Bienvenue, " + currentUser.getFirstname() + " " + currentUser.getLastname() + " !");
        }*/

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




    private void loadFXMLWithAnimation(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent pane = loader.load(); // ✅ Load as Parent

            applyFadeTransition(pane); // ✅ Apply animation to any layout
            content.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlFile);
        }
    }

    @FXML
    private void handleAddVol() {
        loadFXMLWithAnimation("/views/FlightView.fxml");
    }


    @FXML
    private void handleAddAirport() {
        loadFXMLWithAnimation("/views/AirportView.fxml");
    }
    @FXML
    private void handleAddOffre() {
        loadFXMLWithAnimation("/views/ajouter.fxml");
    }


    @FXML
    private void HandloViewOffres() {
        loadFXMLWithAnimation("/views/ViewOffres.fxml");
    }



    private void applyFadeTransition(Object pane) {
        FadeTransition fade = new FadeTransition(Duration.millis(400), (javafx.scene.Node) pane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    public void handleHeberg() {loadFXMLWithAnimation("/views/ajouterHeberg.fxml");}

}