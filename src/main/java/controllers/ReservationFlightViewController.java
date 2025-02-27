package controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Flight;
import models.ReservationsFlights;
import models.User;
import services.ClientService;
import services.FlightService;
import services.ReservationsFlightsService;
import services.UserService;

public class ReservationFlightViewController implements Initializable {

    @FXML
    private TableView<ReservationViewModel> reservationTable;


    @FXML
    private TableColumn<ReservationViewModel, String> flightNumberColumn;

    @FXML
    private TableColumn<ReservationViewModel, String> departureColumn;

    @FXML
    private TableColumn<ReservationViewModel, String> destinationColumn;

    @FXML
    private TableColumn<ReservationViewModel, String> dateColumn;

    @FXML
    private TableColumn<ReservationViewModel, String> bookingDateColumn;

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

        // Set up table columns
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departure"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("flightDate"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));

        // Set up buttons
        cancelReservationBtn.setDisable(true);
        viewDetailsBtn.setDisable(true);

        // Add selection listener
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cancelReservationBtn.setDisable(false);
                viewDetailsBtn.setDisable(false);
            } else {
                cancelReservationBtn.setDisable(true);
                viewDetailsBtn.setDisable(true);
            }
        });

        // Load the logged-in user's reservations
        loadUserReservations();
    }

    // Load the current user's reservations
    private void loadUserReservations() {
        // Get the current logged-in user from ClientService



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
            }
        }

        reservationTable.setItems(reservationsList);

        if (reservationsList.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Reservations");
            alert.setHeaderText(null);
            alert.setContentText("You don't have any flight reservations yet.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        ReservationViewModel selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Reservation Details");
            alert.setHeaderText("Details for Reservation #" + selected.getIdResFlight());

            // Get the full flight details
            Flight flight = flightService.getFlightById(selected.getReservation().getFlight().getIdFlight());

            StringBuilder content = new StringBuilder();
            content.append("Flight Number: ").append(selected.getFlightNumber()).append("\n\n");
            content.append("Route: ").append(selected.getDeparture()).append(" to ").append(selected.getDestination()).append("\n\n");

            if (flight != null) {
                content.append("Departure Airport: ").append(flight.getDepartureAirportName()).append("\n");
                content.append("Arrival Airport: ").append(flight.getArrivalAirportName()).append("\n\n");
                content.append("Price: ").append(flight.getPrice()).append(" TND\n\n");
            }

            content.append("Flight Date: ").append(selected.getFlightDate()).append("\n");
            content.append("Booking Date: ").append(selected.getBookingDate());

            alert.setContentText(content.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelReservation(ActionEvent event) {
        ReservationViewModel selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Cancel Reservation");
            confirmation.setHeaderText("Confirm Cancellation");
            confirmation.setContentText("Are you sure you want to cancel reservation #" + selected.getIdResFlight() + "?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ReservationsFlights reservation = selected.getReservation();
                    reservationService.supprimer(reservation);

                    // Refresh the table
                    loadUserReservations();

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
        // Navigate back to the previous screen
        // This would typically be implemented by the parent controller or navigation service
        // For example:
        // mainController.showDashboard();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUserReservations();
    }
}