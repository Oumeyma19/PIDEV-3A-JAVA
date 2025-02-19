package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import Models.Flight;
import Services.FlightService;
import Services.AirportService;
import Models.Airport;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class AddFlightController {
    @FXML
    private TextField departureField;
    @FXML
    private TextField destinationField;
    @FXML
    private ComboBox<String> departureComboBox;
    @FXML
    private ComboBox<String> arrivalComboBox;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private TextField priceField;
    @FXML
    private TextField flightNumberField;
    @FXML
    private TextField numberPlaceField;

    private FlightService flightService;
    private AirportService airportService;  // Add AirportService to load airports
    private Flight flightForUpdate;

    public AddFlightController() {
        flightService = new FlightService();
        airportService = new AirportService(); // Initialize AirportService
    }

    public void setFlightForUpdate(Flight flight) {
        this.flightForUpdate = flight;
        populateFields(flight);
    }

    private void populateFields(Flight flight) {
        departureComboBox.setValue(flight.getDeparture());
        arrivalComboBox.setValue(flight.getDestination());

        departureDatePicker.setValue(flight.getDepartureTime().toLocalDateTime().toLocalDate());
        arrivalDatePicker.setValue(flight.getArrivalTime().toLocalDateTime().toLocalDate());

        priceField.setText(String.valueOf(flight.getPrice()));
        flightNumberField.setText(flight.getFlightNumber());

        numberPlaceField.setText(String.valueOf(flight.getNumbre_place()));



    }

    @FXML
    private void initialize() {
        loadAirports();  // Load airports when the controller is initialized
    }

    private void loadAirports() {
        List<Airport> airports = airportService.afficher();  // Fetch all airports from the database

        // Clear previous items, just in case
        departureComboBox.getItems().clear();
        arrivalComboBox.getItems().clear();

        // Populate ComboBoxes with airport names
        for (Airport airport : airports) {
            departureComboBox.getItems().add(airport.getNameAirport());
            arrivalComboBox.getItems().add(airport.getNameAirport());
        }
    }

    @FXML
    private void handleSave() {
        // Get selected date from DatePicker
        LocalDate departureDate = departureDatePicker.getValue();
        LocalDate arrivalDate = arrivalDatePicker.getValue();

        // Create a Timestamp object for departure and arrival (with time set to midnight)
        Timestamp departureTimestamp = Timestamp.valueOf(departureDate.atStartOfDay());
        Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDate.atStartOfDay());
        String departure = departureField.getText();
        String destination = destinationField.getText();

        // Get selected airports by name from the ComboBox
        String departureAirportName = departureComboBox.getValue();
        String arrivalAirportName = arrivalComboBox.getValue();

        // Fetch airport details from the database based on selected airport names
        Airport departureAirport = airportService.findAirportByName(departureAirportName);
        Airport arrivalAirport = airportService.findAirportByName(arrivalAirportName);

        // Create the Flight object without an ID (assuming the ID is auto-generated)
        Flight flight = new Flight(
                0,// ID will be auto-generated in the database
                departure,
                destination,
                departureTimestamp,
                arrivalTimestamp,
                Integer.parseInt(priceField.getText()), // Convert price to integer
                flightNumberField.getText(),
                departureAirport, // Use the actual Airport object
                arrivalAirport,// Use the actual Airport object
                Integer.parseInt(numberPlaceField.getText())
        );

        // Add new flight to the database
        try {
            flightService.ajouter(flight);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the window
        priceField.getScene().getWindow().hide();
    }

    public void handleCancel(ActionEvent actionEvent) {
        priceField.getScene().getWindow().hide(); // Close the window
    }
}
