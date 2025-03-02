package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Flight;
import models.ReservationsFlights;
import models.User;
import services.ClientService;
import services.FlightService;
import services.ReservationsFlightsService;
import services.UserService;

public class ReservationFlightViewController implements Initializable {

    @FXML
    private FlowPane reservationsFlowPane;

    @FXML
    private Button viewDetailsBtn;

    @FXML
    private Button cancelReservationBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Button refreshBtn;

    private ReservationsFlightsService reservationService;
    private FlightService flightService;
    private UserService userService;
    private ObservableList<ReservationViewModel> reservationsList = FXCollections.observableArrayList();
    private User currentUser;
    private ReservationViewModel selectedReservation;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    // View model class to display reservation data in the table
    public static class ReservationViewModel {
        private int idResFlight;
        private String flightNumber;
        private String departure;
        private String destination;
        private String flightDate;
        private String bookingDate;
        private ReservationsFlights reservation;

        public ReservationViewModel(ReservationsFlights reservation, Flight flight) {
            this.idResFlight = reservation.getIdResFlight();
            this.flightNumber = flight != null ? flight.getFlightNumber() : "Unknown";
            this.departure = flight != null ? flight.getDeparture() : "Unknown";
            this.destination = flight != null ? flight.getDestination() : "Unknown";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Timestamp departureTime = flight != null ? flight.getDepartureTime() : null;
            this.flightDate = departureTime != null ? dateFormat.format(departureTime) : "Unknown";

            java.util.Date bookingDate = reservation.getBooking_date();
            this.bookingDate = bookingDate != null ? dateFormat.format(bookingDate) : "Unknown";

            this.reservation = reservation;
        }

        public int getIdResFlight() { return idResFlight; }
        public String getFlightNumber() { return flightNumber; }
        public String getDeparture() { return departure; }
        public String getDestination() { return destination; }
        public String getFlightDate() { return flightDate; }
        public String getBookingDate() { return bookingDate; }
        public ReservationsFlights getReservation() { return reservation; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize services
        reservationService = new ReservationsFlightsService();
        flightService = new FlightService();
        userService = new UserService();

        // Set up buttons
        cancelReservationBtn.setDisable(true);
        viewDetailsBtn.setDisable(true);

        // Load the logged-in user's reservations
        loadUserReservations();
    }

    // Load the current user's reservations
    private void loadUserReservations() {
        // Get the current logged-in user from UserService
        currentUser = userService.getLoggedInUser();
        System.out.println("Current user: " + currentUser);

        if (currentUser == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Not Logged In");
            alert.setHeaderText("User not logged in");
            alert.setContentText("Please log in to view your reservations.");
            alert.showAndWait();
            return;
        }

        List<ReservationsFlights> allReservations = reservationService.afficher();
        reservationsList.clear();

        // Clear the flow pane
        reservationsFlowPane.getChildren().clear();

        for (ReservationsFlights res : allReservations) {
            if (res.getUser().getId() == currentUser.getId()) {
                // Get flight details for this reservation
                Flight flight = null;
                if (res.getFlight() != null) {
                    flight = flightService.getFlightById(res.getFlight().getIdFlight());
                    if (flight == null) {
                        flight = new Flight(res.getFlight().getIdFlight());
                    }
                }

                ReservationViewModel viewModel = new ReservationViewModel(res, flight);
                reservationsList.add(viewModel);

                // Create and add a card for this reservation
                VBox card = createReservationCard(viewModel);
                reservationsFlowPane.getChildren().add(card);
            }
        }

        if (reservationsList.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Reservations");
            alert.setHeaderText(null);
            alert.setContentText("You don't have any flight reservations yet.");
            alert.showAndWait();
        }
    }

    private VBox createReservationCard(ReservationViewModel reservation) {
        // Create card container
        VBox card = new VBox();
        card.setPrefWidth(230);
        card.setPrefHeight(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        card.setPadding(new Insets(15));
        card.setSpacing(8);

        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.gray(0.4));
        shadow.setRadius(5);
        card.setEffect(shadow);

        // Flight number with larger font
        Label flightNumberLabel = new Label(reservation.getFlightNumber());
        flightNumberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        // Route information
        Label routeLabel = new Label(reservation.getDeparture() + " → " + reservation.getDestination());
        routeLabel.setStyle("-fx-font-size: 14;");

        // Flight date information
        Label flightDateLabel = new Label("Flight: " + reservation.getFlightDate());
        flightDateLabel.setStyle("-fx-font-size: 12;");

        // Booking date information
        Label bookingDateLabel = new Label("Booked: " + reservation.getBookingDate());
        bookingDateLabel.setStyle("-fx-font-size: 12;");

        // Reservation ID information



        // Add all elements to card
        card.getChildren().addAll(
                flightNumberLabel,
                routeLabel,
                flightDateLabel,
                bookingDateLabel

        );

        // Handle card selection
        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(event -> {
            // Deselect all cards first
            for (int i = 0; i < reservationsFlowPane.getChildren().size(); i++) {
                reservationsFlowPane.getChildren().get(i).setStyle(
                        "-fx-background-color: white; -fx-background-radius: 10;"
                );
            }

            // Select this card
            card.setStyle(
                    "-fx-background-color: #e6f2ff; -fx-background-radius: 10; -fx-border-color: #0078d7; -fx-border-width: 2; -fx-border-radius: 10;"
            );

            // Update selected reservation
            selectedReservation = reservation;

            // Enable action buttons
            viewDetailsBtn.setDisable(false);
            cancelReservationBtn.setDisable(false);
        });

        return card;
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        if (selectedReservation != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Reservation Details");
            alert.setHeaderText("Details for Reservation #" + selectedReservation.getIdResFlight());

            // Get the full flight details
            Flight flight = flightService.getFlightById(selectedReservation.getReservation().getFlight().getIdFlight());

            StringBuilder content = new StringBuilder();
            content.append("Flight Number: ").append(selectedReservation.getFlightNumber()).append("\n\n");
            content.append("Route: ").append(selectedReservation.getDeparture()).append(" to ").append(selectedReservation.getDestination()).append("\n\n");

            if (flight != null) {
                content.append("Departure Airport: ").append(flight.getDepartureAirportName()).append("\n");
                content.append("Arrival Airport: ").append(flight.getArrivalAirportName()).append("\n\n");
                content.append("Price: ").append(flight.getPrice()).append(" TND\n\n");
            }

            content.append("Flight Date: ").append(selectedReservation.getFlightDate()).append("\n");
            content.append("Booking Date: ").append(selectedReservation.getBookingDate());

            alert.setContentText(content.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        if (selectedReservation != null) {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Cancel Reservation");
            confirmation.setHeaderText("Confirm Cancellation");
            confirmation.setContentText("Are you sure you want to cancel reservation #" + selectedReservation.getIdResFlight() + "?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ReservationsFlights reservation = selectedReservation.getReservation();
                    reservationService.supprimer(reservation);

                    // Refresh the view
                    loadUserReservations();

                    // Reset selection state
                    selectedReservation = null;
                    viewDetailsBtn.setDisable(true);
                    cancelReservationBtn.setDisable(true);

                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Cancellation Successful");
                    success.setHeaderText(null);
                    success.setContentText("Your reservation has been successfully cancelled.");
                    success.showAndWait();
                }
            });
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // Better error handling with user feedback
            System.err.println("Error loading Profil view: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Cannot Return to Profile");
            alert.setContentText("An error occurred while trying to navigate back to the profile page. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUserReservations();
        // Reset selection state
        selectedReservation = null;
        viewDetailsBtn.setDisable(true);
        cancelReservationBtn.setDisable(true);
    }
}