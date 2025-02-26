package controllers;

import models.Flight;
import models.ReservationsFlights;
import models.User;
import org.json.JSONObject;
import services.ClientService;
import services.FlightService;
import services.ReservationsFlightsService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightDetailsController {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private ClientService clientService;
    private ReservationsFlightsService reservationsFlightsService;
    private FlightService flightService;
    private static final String API_KEY = "dd0469c6902747ca8de141820252602";

    public FlightDetailsController() {
        reservationsFlightsService = new ReservationsFlightsService();
        flightService = new FlightService();
        clientService = new ClientService();
    }

    @FXML
    private Label flightNumberLabel;
    @FXML
    private Label routeLabel;
    @FXML
    private Label departureTimeLabel;
    @FXML
    private Label arrivalTimeLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label seatsLabel;
    @FXML
    private Label weatherLabelDeparture;
    @FXML
    private Label weatherLabelDestination;

    private Flight flight;
    private User user;

    public void setFlightData(Flight flight) {
        this.flight = flight;
        updateFlightDetails();
        if (flight != null) {
            fetchWeather(flight.getDeparture(), weatherLabelDeparture);
            fetchWeather(flight.getDestination(), weatherLabelDestination);

        }

    }

    private void fetchWeather(String location, Label weatherLabel) {
        try {
            String encodedCity = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());

            String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + encodedCity;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent header

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String condition = jsonResponse.getJSONObject("current").getJSONObject("condition").getString("text");
            double temp = jsonResponse.getJSONObject("current").getDouble("temp_c");

            weatherLabel.setText("Weather: " + condition + ", " + temp + "°C");
        } catch (Exception e) {
            weatherLabel.setText("Weather: Not Available");
            e.printStackTrace();
        }
    }


    private void updateFlightDetails() {
        if (flight != null) {
            flightNumberLabel.setText(flight.getFlightNumber());
            routeLabel.setText(flight.getDeparture() + " → " + flight.getDestination());
            departureTimeLabel.setText(sdf.format(flight.getDepartureTime()));
            arrivalTimeLabel.setText(sdf.format(flight.getArrivalTime()));
            priceLabel.setText(flight.getPrice() + " €");
        }
    }

    @FXML
    private void handleBookFlight() {
        try {
            // Initialize services
            ReservationsFlightsService reservationService = new ReservationsFlightsService();
            FlightService flightService = new FlightService(); // Service to handle flight updates

            // Fetch the user with id = 117
            User user = new User(117);

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
            flightService.modifier(flight, flight.getFlightNumber()); // Assuming this method updates the DB

            // Update the UI
            updateFlightDetails();

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
}