package Controllers;

import Models.Flight;
import Models.ReservationsFlights;
import Models.User;
import Services.ClientService;
import Services.FlightService;
import Services.ReservationsFlightsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent; // ✅ FIXED import

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightDetailsController {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private ClientService clientService;
    private ReservationsFlightsService reservationsFlightsService;
    private FlightService flightService;
    public FlightDetailsController() {

        reservationsFlightsService = new ReservationsFlightsService();
        flightService = new FlightService();
        clientService = new ClientService();
    }

    @FXML
    private Label flightDetailsLabel;

    @FXML
    private ComboBox<String> seatComboBox;

    private Flight flight;
    private User user;

    public void setFlightData(Flight flight) {
        this.flight = flight;
        flightDetailsLabel.setText("Flight: " + flight.getDeparture() + " → " + flight.getDestination() +
                " | " + sdf.format(flight.getDepartureTime()) + " → " + sdf.format(flight.getArrivalTime()) +
                " | Price: " + flight.getPrice() + " €");
    }



    @FXML
    private void handleBookFlight() {
        try {
            // Initialize services
            ReservationsFlightsService reservationService = new ReservationsFlightsService();
            FlightService flightService = new FlightService(); // Service to handle flight updates

            // Fetch the user with id = 6
            User user = new User(6);

            // Ensure that the selected flight is not null
            if (flight == null) {
                System.out.println("No flight selected!");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please select a flight before booking.");
                alert.showAndWait();
                return;
            }

            // Check available seats (numbre_place)
            int availableSeats = flight.getNumbre_place(); // Assuming this method exists in Flight class
            if (availableSeats <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Flight Full");
                alert.setHeaderText(null);
                alert.setContentText("Sorry, this flight is fully booked!");
                alert.showAndWait();
                return;
            }

            // Create a reservation with the current date
            Date bookingDate = new Date();
            ReservationsFlights reservation = new ReservationsFlights(user, flight, bookingDate);

            // Save reservation
            reservationService.ajouter(reservation);

            // Update the flight's available seats (decrease by 1)
            flight.setNumbre_place(availableSeats - 1);
            flightService.modifier(flight,flight.getFlightNumber()); // Assuming this method updates the DB

            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Confirmed");
            alert.setHeaderText(null);
            alert.setContentText("Your flight has been booked successfully!");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while booking the flight.");
            alert.showAndWait();
        }
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
