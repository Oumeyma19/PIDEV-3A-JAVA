package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import Models.Flight;
import Services.FlightService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class FlightSearchController {

    @FXML
    private TextField departureField;

    @FXML
    private TextField arrivalField;

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private DatePicker arrivalDatePicker;

    @FXML
    private VBox flightResultsContainer;

    private FlightService flightService = new FlightService();

    @FXML
    private void handleSearch() {
        // Validate input fields
        if (departureField.getText().isEmpty() || arrivalField.getText().isEmpty()) {
            showAlert("Error", "Departure and Arrival locations are required.");
            return;
        }

        LocalDate departureDate = departureDatePicker.getValue();
        LocalDate arrivalDate = arrivalDatePicker.getValue();

        // Validate date pickers
        if (departureDate == null || arrivalDate == null) {
            showAlert("Error", "Please select both departure and arrival dates.");
            return;
        }

        // Convert LocalDate to Timestamp
        Timestamp departureTimestamp = Timestamp.valueOf(departureDate.atStartOfDay());
        Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDate.atStartOfDay());

        // Fetch flights from the database
        List<Flight> flights = flightService.searchFlights(
                departureField.getText(),
                arrivalField.getText(),
                departureTimestamp,
                arrivalTimestamp
        );

        // Clear previous results
        flightResultsContainer.getChildren().clear();

        // Add flight cards to the container
        for (Flight flight : flights) {
            try {
                // Load the FlightCard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FlightCard.fxml"));
                VBox flightCard = loader.load();

                // Get the controller and set the flight data
                FlightCardController cardController = loader.getController();
                cardController.setFlightData(flight);

                // Add the flight card to the results container
                flightResultsContainer.getChildren().add(flightCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load flight card: " + e.getMessage());
            }
        }
    }
    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}