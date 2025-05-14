package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import services.*;

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
        if (currentUser == null) {
            System.err.println("Tentative de définition d'un utilisateur nul dans ReservationFlightViewController");
            return;
        }

        this.currentUser = currentUser;
        SessionManager.setCurrentUser(currentUser);

        System.out.println("User set in ReservationFlightViewController : " + currentUser.getFirstname());
        System.out.println("User ID: " + currentUser.getId());

        // Trigger reservation loading
        Platform.runLater(() -> {
            loadUserReservations();
        });
    }

    public String getCurrentUser() {
        return currentUser != null ? currentUser.getFirstname() : "null";
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
        System.out.println("initialize() method called");

        // Initialize services
        reservationService = new ReservationsFlightsService();
        flightService = new FlightService();
        userService = new UserService();

        // Set up buttons
        cancelReservationBtn.setDisable(true);
        viewDetailsBtn.setDisable(true);

        // Check if user was set before or during initialization
        if (currentUser == null) {
            currentUser = SessionManager.getCurrentUser();
        }

        // Load reservations if user is available
        if (currentUser != null) {
            loadUserReservations();
        }
        FlightReminderNotification reminderNotification = new FlightReminderNotification();
        reminderNotification.checkUpcomingFlightReminder(reservationsList);
    }

    // Load the current user's reservations
    private void loadUserReservations() {
        // Ensure this method runs on the JavaFX Application Thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::loadUserReservations);
            return;
        }

        // Existing null check
        if (currentUser == null) {
            System.err.println("CRITIQUE : Tentative de chargement de réservations avec un utilisateur null");
            showAlert(AlertType.WARNING,
                "Non connecté",
                "Utilisateur non connecté",
                "Veuillez vous connecter pour consulter vos réservations.");
            return;
        }

        try {
            // Fetch reservations specific to the current user
            List<ReservationsFlights> userReservations = reservationService.getReservationsByUserId(currentUser.getId());

            // Clear existing data
            reservationsList.clear();
            reservationsFlowPane.getChildren().clear();

            // Process reservations
            if (userReservations.isEmpty()) {
                showAlert(AlertType.INFORMATION,
                    "Pas de réservation",
                    null,
                    "Vous n'avez pas encore de réservation de vol.");
                return;
            }

            // Process and display each reservation
            for (ReservationsFlights res : userReservations) {
                // Fetch detailed flight information (if needed)
                Flight flight = res.getFlight();
                if (flight != null) {
                    flight = flightService.getFlightById(flight.getIdFlight());
                    // Fallback if flight details not found
                    if (flight == null) {
                        flight = new Flight(res.getFlight().getIdFlight());
                    }
                }

                // Create view model and add to list
                ReservationViewModel viewModel = new ReservationViewModel(res, flight);
                reservationsList.add(viewModel);

                // Create and add reservation card to flow pane
                VBox card = createReservationCard(viewModel);
                reservationsFlowPane.getChildren().add(card);
            }

            // Ensure the flow pane is visible and populated
            reservationsFlowPane.setVisible(true);
        } catch (Exception e) {
            showAlert(AlertType.ERROR,
                "Erreur de réservation",
                "Échec du chargement des réservations",
                "Une erreur s'est produite lors de la recherche de vos réservations : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Utility method to show alerts with less repetitive code
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
            alert.setTitle("Détails de la réservation");
            alert.setHeaderText("Détails de la réservation #" + selectedReservation.getIdResFlight());

            // Get the full flight details
            Flight flight = flightService.getFlightById(selectedReservation.getReservation().getFlight().getIdFlight());

            StringBuilder content = new StringBuilder();
            content.append("Numéro de vol : ").append(selectedReservation.getFlightNumber()).append("\n\n");
            content.append("Itinéraire : ").append(selectedReservation.getDeparture()).append(" to ").append(selectedReservation.getDestination()).append("\n\n");

            if (flight != null) {
                content.append("Aéroport de départ : ").append(flight.getDepartureAirportName()).append("\n");
                content.append("Aéroport d'arrivée : ").append(flight.getArrivalAirportName()).append("\n\n");
                content.append("Prix: ").append(flight.getPrice()).append(" TND\n\n");
            }

            content.append("Date du vol : ").append(selectedReservation.getFlightDate()).append("\n");
            content.append("Date de réservation : ").append(selectedReservation.getBookingDate());

            alert.setContentText(content.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        if (selectedReservation != null) {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Annuler la réservation");
            confirmation.setHeaderText("Confirmer l'annulation");
            confirmation.setContentText("Êtes-vous sûr de vouloir annuler la réservation ?#" + selectedReservation.getIdResFlight() + "?");

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
                    success.setTitle("Annulation réussie");
                    success.setHeaderText(null);
                    success.setContentText("Votre réservation a été annulée avec succès.");
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
            System.err.println("Erreur de chargement de la vue Profil : " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible de revenir au profil");
            alert.setContentText("Une erreur s'est produite lors de la navigation vers la page de profil. Veuillez réessayer.");
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
