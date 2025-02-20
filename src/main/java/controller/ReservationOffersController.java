package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import models.Offre;
import services.OffreService;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationOffersController {

    @FXML
    private FlowPane offersContainer;

    private final OffreService offreService = new OffreService();

    @FXML
    public void initialize() {
        loadOffers();
    }

    private void loadOffers() {
        offersContainer.getChildren().clear(); // Clear existing offers

        try {
            List<Offre> offres = offreService.recuperer(); // Fetch offers from the database

            if (offres.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Offers Available", "There are no travel offers at the moment.");
                return;
            }

            for (Offre offre : offres) {
                VBox offerCard = createOfferCard(offre);
                offersContainer.getChildren().add(offerCard);
                animateOfferCard(offerCard); // Add fade-in effect
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load offers. Please try again later.");
        }
    }

    private VBox createOfferCard(Offre offre) {
        VBox card = new VBox(10);
        card.getStyleClass().add("offer-card");
        card.setPrefWidth(250);

        // Offer Image
        ImageView imageView = new ImageView();
        imageView.getStyleClass().add("offer-image");
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);

        String imagePath = offre.getImagePath();

        // Load image
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageView.setImage(new Image(imagePath));
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath);
                imageView.setImage(loadDefaultImage()); // Load default image on error
            }
        } else {
            imageView.setImage(loadDefaultImage()); // Load default image if path is empty
        }

        // Offer Details
        Label title = new Label(offre.getTitle());
        title.getStyleClass().add("offer-title");

        Label price = new Label("Price: $" + offre.getPrice());
        price.getStyleClass().add("offer-price");

        Label date = new Label("From " + offre.getStartDate() + " to " + offre.getEndDate());
        date.getStyleClass().add("offer-date");

        Button reserveBtn = new Button("Reserve Now");
        reserveBtn.getStyleClass().add("reserve-button");
        reserveBtn.setOnAction(event -> openReservationForm(offre));

        HBox buttonContainer = new HBox(reserveBtn);
        buttonContainer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, title, price, date, buttonContainer);

        return card;
    }

    private Image loadDefaultImage() {
        return new Image(getClass().getResourceAsStream("/default.png")); // Default image
    }

    private void openReservationForm(Offre selectedOffer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReservationForm.fxml"));
            VBox reservationForm = loader.load();

            ReservationFormController formController = loader.getController();
            formController.setSelectedOffer(selectedOffer);

            Stage stage = new Stage();
            stage.setTitle("Reservation Form");
            stage.setScene(new Scene(reservationForm));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Form Error", "Failed to load the reservation form.");
        }
    }

    private void animateOfferCard(VBox card) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation Methods
    @FXML
    private void goToHome() {
        // Reload the home page
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/ReservationOffers.fxml"));
            Stage stage = (Stage) offersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToReservationList() {
        // Load the reservation list page
        try {
            StackPane root = FXMLLoader.load(getClass().getResource("/reservation_list.fxml"));
            Stage stage = (Stage) offersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToProfile() {
        // Load the profile page
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/Profile.fxml"));
            Stage stage = (Stage) offersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}