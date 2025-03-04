package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import models.Offre;
import java.util.Random;

import models.User;
import services.OffreService;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationOffersController {

    @FXML
    private FlowPane offersContainer;
    @FXML
    private VBox priceContainer;
    // Or HBox, Pane, etc.


    private final OffreService offreService = new OffreService();

    @FXML
    public void initialize() {
        loadOffers();
    }

    private void loadOffers() {
        offersContainer.getChildren().clear(); // Clear existing offers

        try {
            List<Offre> offres = offreService.Display(); // Fetch offers from the database

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



    // Method to show offer details in an alert
    private VBox createOfferCard(Offre offre) {
        VBox card = new VBox(10);
        card.getStyleClass().add("offer-card");
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ed6637; -fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        String imagePath = offre.getImagePath();

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageView.setImage(new Image(imagePath));
            } catch (Exception e) {
                System.err.println("Error loading image: " + imagePath);
                imageView.setImage(loadDefaultImage());
            }
        } else {
            imageView.setImage(loadDefaultImage());
        }

        Label title = new Label(offre.getTitle());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ed6637;");

        VBox priceContainer = new VBox(5);
        priceContainer.setAlignment(Pos.CENTER);

        double originalPriceValue = offre.getPrice();
        Random random = new Random();
        int discountPercentage = random.nextInt(51); // Generates a number from 0 to 50
        double discountedPriceValue = originalPriceValue * (1 - discountPercentage / 100.0);

        Text originalPrice = new Text("$" + String.format("%.2f", originalPriceValue));
        originalPrice.setFill(Color.GRAY);
        originalPrice.setFont(Font.font("Arial", FontPosture.REGULAR, 14));
        originalPrice.setStrikethrough(true);

        Text discountText = new Text(" (" + discountPercentage + "% OFF)");
        discountText.setFill(Color.RED);
        discountText.setFont(Font.font("Arial", FontPosture.ITALIC, 12));

        TextFlow originalPriceFlow = new TextFlow(originalPrice, discountText);

        Label discountedPrice = new Label("$" + String.format("%.2f", discountedPriceValue));
        discountedPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ed6637;");

        priceContainer.getChildren().addAll(originalPriceFlow, discountedPrice);

        Label date = new Label("From " + offre.getStartDate() + " to " + offre.getEndDate());
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label hebergementsLabel = new Label("Hebergements: " + offre.getHebergementsStr());
        Label toursLabel = new Label("Tours: " + offre.getToursStr());
        Label flightsLabel = new Label("Flights: " + offre.getFlightsStr());

        Button detailsBtn = new Button("Details");
        detailsBtn.setStyle("-fx-background-color: #ed6637; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-size: 12px; -fx-background-radius: 5;");

        Button reserveBtn = new Button("Reserve Now");
        reserveBtn.setStyle("-fx-background-color: #ed6637; -fx-text-fill: white; -fx-padding: 8 15; -fx-font-size: 14px; -fx-background-radius: 5;");
        reserveBtn.setOnAction(event -> openReservationForm(offre));

        HBox buttonContainer = new HBox(10, detailsBtn, reserveBtn);
        buttonContainer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, title, priceContainer, date, hebergementsLabel, toursLabel, flightsLabel, buttonContainer);
        return card;
    }


    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("User received: " + currentUser.getFirstname()); // Debugging
    }


    private Image loadDefaultImage() {
        return new Image(getClass().getResourceAsStream("/views/default.png")); // Default image
    }
    private void navigateTo(String fxmlPath) {
        try {
            // Create a new FXMLLoader instance
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Load the root node from the FXML file
            Parent root = loader.load();

            // Get the controller from the FXMLLoader
            Object controller = loader.getController();

            // Set the current user in the appropriate controller
            if (controller instanceof HomeController) {
                ((HomeController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof ProfilController) {
                ((ProfilController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof ReservationListController) {
                ((ReservationListController) controller).setLoggedInUser(currentUser);
            }

            // Set the scene and show the stage
            Stage stage = (Stage) offersContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the requested page.");
        }
    }


    private void openReservationForm(Offre selectedOffer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReservationForm.fxml"));
            VBox reservationForm = loader.load();

            ReservationFormController formController = loader.getController();
            formController.setSelectedOffer(selectedOffer);
            formController.setLoggedInUser(currentUser); // Make sure currentUser is set


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
        navigateTo("/views/Home.fxml");
    }

    @FXML
    private void goToReservationList() {
        navigateTo("/views/reservation_list.fxml");
    }

    @FXML
    private void goToProfile() {
        navigateTo("/views/Profil.fxml");
    }


}