package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.ReservationTour;
import models.Tour;
import models.User;
import services.ReservationTourService;
import services.TourService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationsViewController implements Initializable {
    @FXML
    private TableView<ReservationDisplay> reservationsTable;
    @FXML
    private VBox reservationsContainer;
    @FXML
    private Button profileButton;

    @FXML
    private TableColumn<ReservationDisplay, String> tourNameColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> statusColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> reservationDateColumn;
    @FXML
    private TableColumn<ReservationDisplay, Void> actionsColumn;
    private User currentUser;
    private final ReservationTourService reservationService = new ReservationTourService();
    private final TourService tourService = new TourService();

    public static class ReservationDisplay {
        private final ReservationTour reservation;
        private final String tourName;

        public ReservationDisplay(ReservationTour reservation, String tourName) {
            this.reservation = reservation;
            this.tourName = tourName;
        }

        public int getId() {
            return reservation.getId();
        }

        public String getTourName() {
            return tourName;
        }

        public String getStatus() {
            return reservation.getStatus();
        }

        public String getReservationDate() {
            return reservation.getReservationDate().toString();
        }

        public ReservationTour getReservation() {
            return reservation;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleBackToProfile());
        }
    }

    public void setCurrentUser(User user) throws SQLException {
        this.currentUser = user;
        loadReservations();
    }

    private void loadReservations() throws SQLException {
        if (currentUser != null) {
            // Clear existing reservations
            reservationsContainer.getChildren().clear();

            // Fetch user's reservations
            List<ReservationTour> reservations = reservationService.getReservationsByUserId(currentUser.getId());

            // Create a card for each reservation
            for (ReservationTour reservation : reservations) {
                reservationsContainer.getChildren().add(createReservationCard(reservation));
            }
        }
    }

    private VBox createReservationCard(ReservationTour reservation) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                        "-fx-padding: 15;"
        );

        try {
            // Fetch tour details
            Tour tour = tourService.getTourById(reservation.getTourId());
            String tourName = (tour != null) ? tour.getTitle() : "Tour Supprimé";

            // Tour Name
            Label tourNameLabel = new Label(tourName);
            tourNameLabel.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #2c3e50;"
            );

            // Reservation Details
            Label detailsLabel = new Label(
                    "Date de Réservation: " + reservation.getReservationDate() + "\n" +
                            "Statut: " + reservation.getStatus()
            );
            detailsLabel.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #7f8c8d;"
            );

            // Cancel Button
            Button cancelButton = new Button("Annuler la Réservation");
            cancelButton.setStyle(
                    "-fx-background-color: #e74c3c;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-padding: 8 15;"
            );
            cancelButton.setOnAction(event -> {
                try {
                    deleteReservation(reservation.getId());
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible d'annuler la réservation.");
                }
            });

            // Add components to card
            card.getChildren().addAll(tourNameLabel, detailsLabel, cancelButton);
        } catch (SQLException e) {
            // Handle potential SQL exceptions
            Label errorLabel = new Label("Erreur de chargement de la réservation");
            card.getChildren().add(errorLabel);
        }

        return card;
    }

    private void deleteReservation(int reservationId) throws SQLException {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer l'annulation");
        confirmAlert.setHeaderText("Voulez-vous vraiment annuler cette réservation ?");
        confirmAlert.setContentText("Cette action est irréversible.");

        if (confirmAlert.showAndWait().orElse(null) == ButtonType.OK) {
            reservationService.deleteReservation(reservationId);
            loadReservations(); // Refresh the view
            showAlert("Succès", "Réservation annulée avec succès.");
        }
    }

    @FXML
    private void refreshReservations() {
        try {
            loadReservations();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'actualiser les réservations.");
        }
    }

    @FXML
    private void handleBackToProfile() {
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
            showAlert("Error", "Failed to load Profile View.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}