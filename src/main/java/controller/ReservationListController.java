package controller;

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
import services.ReservationOffreService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationListController {
    @FXML private GridPane gridPane;

    private final ReservationOffreService reservationService = new ReservationOffreService();
    private int loggedInUserId = 6; // Replace with actual logged-in user ID

    @FXML
    public void initialize() {
        System.out.println("Initializing controller...");
        try {
            loadReservations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        try {
            List<ReservationOffre> reservations = reservationService.recupererParUtilisateur(loggedInUserId);
            int row = 0;
            int col = 0;

            for (ReservationOffre reservation : reservations) {
                // Create a card for each reservation
                VBox card = createReservationCard(reservation);

                // Add the card to the grid
                gridPane.add(card, col, row);

                // Update row and column for the next card
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



        // Offer title
        Label titleLabel = new Label(reservation.getOffre().getTitle());
        titleLabel.getStyleClass().add("offer-title");

        // Reservation details
        Label detailsLabel = new Label(
                "Dates: " + reservation.getStartDate() + " - " + reservation.getEndDate() + "\n" +
                        "Adultes: " + reservation.getNumberOfAdults() + ", Enfants: " + reservation.getNumberOfChildren() + "\n" +
                        "Statut: " + reservation.getStatus()
        );
        detailsLabel.getStyleClass().add("reservation-details");

        // Cancel button
        Button cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(event -> {
            try {
                reservationService.annulerReservation(reservation.getId());
                gridPane.getChildren().remove(card); // Remove the card after canceling
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Add all elements to the card
        card.getChildren().addAll(titleLabel, detailsLabel, cancelButton);
        return card;
    }

    @FXML
    private void goBack() {
        // Navigate back to the main menu or previous screen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ReservationOffre.fxml"));
            Stage stage = (Stage) gridPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}