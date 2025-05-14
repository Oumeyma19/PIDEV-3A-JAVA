package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.ReservationOffre;
import models.User;
import services.ReservationOffreService;
import services.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationListController {
    @FXML private GridPane gridPane;

    private final ReservationOffreService reservationService = new ReservationOffreService();

    @FXML
    public void initialize() {
        System.out.println("Initializing controller...");

        // Retrieve logged-in user from SessionManager
        loggedInUser = SessionManager.getCurrentUser();

        if (loggedInUser == null) {
            System.out.println("No user is logged in!");
            return; // Stop execution if no user is found
        }

        System.out.println("Logged-in User: " + loggedInUser.getFirstname());

        try {
            loadReservations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadReservations() {
        if (loggedInUser == null) {
            System.out.println("Error: No logged-in user found.");
            return;
        }

        try {
            // Clear the GridPane before reloading reservations
            gridPane.getChildren().clear();

            // Retrieve reservations for the logged-in user
            List<ReservationOffre> reservations = reservationService.recupererParUtilisateur(loggedInUser.getId());

            int row = 0;
            int col = 0;

            for (ReservationOffre reservation : reservations) {
                VBox card = createReservationCard(reservation);
                gridPane.add(card, col, row);

                col++;
                if (col == 3) { // 3 cards per row
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private VBox createReservationCard(ReservationOffre reservation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reservation-card");

        // Image (if available)
        ImageView offerImage = new ImageView();
        offerImage.setFitWidth(150);
        offerImage.setFitHeight(100);
        if (reservation.getOffre().getImagePath() != null) {
            offerImage.setImage(new Image(reservation.getOffre().getImagePath()));
        } else {
            offerImage.setImage(new Image("/views/default.png"));
        }

        // Offer title
        Label titleLabel = new Label(reservation.getOffre().getTitle());
        titleLabel.getStyleClass().add("offer-title");

        // Reservation details
        Label detailsLabel = new Label(
                "Dates: " + reservation.getstart_date() + " - " + reservation.getend_date() + "\n" +
                        "Adultes: " + reservation.getNumberOfAdults() + ", Enfants: " + reservation.getNumberOfChildren() + "\n" +
                        "Statut: " + reservation.getStatus()
        );
        detailsLabel.getStyleClass().add("reservation-details");


        Button cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(event -> {
            try {
                reservationService.annulerReservation(reservation.getId());

                loadReservations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button modifyButton = new Button("Modifier");
        modifyButton.getStyleClass().add("modify-button");
        modifyButton.setOnAction(event -> openUpdateReservationForm(reservation));

        card.getChildren().addAll(offerImage, titleLabel, detailsLabel, modifyButton, cancelButton);
        return card;

    }
    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        System.out.println("User set in ReservationOffersListController: " + user.getFirstname()); // Debugging
        loadReservations();
    }

    private void loadUserReservations() {
        if (loggedInUser == null) {
            System.out.println("No user found!");
            return;
        }

        try {
            ReservationOffreService reservationService = new ReservationOffreService();
            List<ReservationOffre> userReservations = reservationService.getReservationsByUser(loggedInUser.getId());

            // Display the reservations in the UI
            System.out.println("User has " + userReservations.size() + " reservations.");

            // TODO: Update the UI (TableView/ListView) with `userReservations`

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openUpdateReservationForm(ReservationOffre reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_reservation.fxml"));
            Parent root = loader.load();

            UpdateReservationController controller = loader.getController();
            controller.setReservation(reservation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Get the controller
            ProfilController profilController = loader.getController();

            // Pass the current user
            profilController.setCurrentUser(loggedInUser); // Use loggedInUser instead of currentUser

            // Update the scene
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("User received: " + currentUser.getFirstname()); // Debugging
    }
}