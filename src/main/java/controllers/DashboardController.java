package controllers;

import javafx.animation.FadeTransition;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.User;
import services.SessionManager;

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

    @FXML
    private BorderPane borderPane;





    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize();
    }

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur actuel depuis SessionManager
        User currentUser = SessionManager.getCurrentUser();


        //loadStatsView();

        // Gestionnaires d'événements pour les boutons
        clientsButton.setOnAction(event -> loadPage("/views/Clients.fxml"));
        guidesButton.setOnAction(event -> loadPage("/views/Guides.fxml"));
        VolsButton.setOnAction(event -> handleAddVol());
        AirportsButton.setOnAction(event -> handleAddAirport());
        AjouterButton.setOnAction(event -> handleAddOffre());
        OffresButton.setOnAction(event -> HandloViewOffres());
        //loadStatsView();

    }

    @FXML
    private void handleTableauDeBord(ActionEvent event) {
        loadFXMLWithAnimation("/views/StatsView.fxml") ;// Recharge la vue des statistiques
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




    private void loadFXMLWithAnimation(String fxmlFile) {
        try {
            System.out.println("Loading FXML: " + fxmlFile); // Log pour vérifier le chemin
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent pane = loader.load();
            System.out.println("FXML loaded successfully: " + fxmlFile); // Log pour vérifier le chargement
            applyFadeTransition(pane);
            content.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlFile);
        }
    }

    @FXML
    private void handleAddVol() {
        System.out.println("handleAddVol called"); // Log pour vérifier l'appel
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
        System.out.println("Applying fade transition to: " + pane); // Log pour vérifier le nœud
        FadeTransition fade = new FadeTransition(Duration.millis(400), (javafx.scene.Node) pane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    public void handleHeberg() {loadFXMLWithAnimation("/views/ajouterHeberg.fxml");}

    public void handelProgFid(MouseEvent mouseEvent) {
        loadFXMLWithAnimation("/views/Progfid.fxml" );}

    public void handelRecomp(MouseEvent mouseEvent) {
        loadFXMLWithAnimation("/views/recomp.fxml");
    }
}
