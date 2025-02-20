package controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.io.IOException;

public class DashboardController {

    @FXML
    private VBox content; // Main content area for loading pages
    @FXML
    private VBox offersMenu; // Dropdown menu for Offers

    private boolean isOffersMenuVisible = false;

    /**
     * Toggles the Offers menu with a smooth slide animation.
     */
    @FXML
    private void toggleOffersMenu(ActionEvent event) {
        offersMenu.setVisible(!offersMenu.isVisible());
    }


    /**
     * Loads the 'Ajouter Offres' page with a smooth fade-in animation.
     */
    @FXML
    private void handleAddOffer() {
        loadFXMLWithAnimation("/ajouter.fxml");
    }

    /**
     * Loads the 'Lister Offres' page with a smooth fade-in animation.
     */
    @FXML
    private void handleListOffers() {
        loadFXMLWithAnimation("/ViewOffres.fxml");
    }

    /**
     * Handles updating an offer (TODO: Implement functionality)
     */
    @FXML
    private void handleUpdateOffer() {
        System.out.println("Update Offer functionality to be implemented.");
        // TODO: Implement update offer logic
    }

    /**
     * Handles deleting an offer (TODO: Implement functionality)
     */
    @FXML
    private void handleDeleteOffer() {
        System.out.println("Delete Offer functionality to be implemented.");
        // TODO: Implement delete offer logic
    }

    /**
     * Loads an FXML file into the content area with a fade-in effect.
     */
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
    private void handleDashboard() {
        loadFXMLWithAnimation("/DashboardView.fxml"); // Change to the actual FXML file for the dashboard
    }


    /**
     * Applies a fade-in transition effect to the new content.
     */
    private void applyFadeTransition(Object pane) {
        FadeTransition fade = new FadeTransition(Duration.millis(400), (javafx.scene.Node) pane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void handleLogout(ActionEvent actionEvent) {
    }

    public void handleCommunity(ActionEvent actionEvent) {
    }
}
