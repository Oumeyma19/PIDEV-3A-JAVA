package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Flight;
import org.controlsfx.control.textfield.TextFields;
import services.FlightService;
import services.AirportService;
import models.Airport;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private ComboBox<String> departureHourComboBox;
    @FXML
    private ComboBox<String> departureMinuteComboBox;
    @FXML
    private ComboBox<String> arrivalHourComboBox;
    @FXML
    private ComboBox<String> arrivalMinuteComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private TextField flightNumberField;
    @FXML
    private TextField numberPlaceField;

    private FlightService flightService = new FlightService();
    private AirportService airportService = new AirportService();
    private Flight flightForUpdate;

    private ObservableList<String> cityList = FXCollections.observableArrayList(flightService.getCities());

    public void setFlightForUpdate(Flight flight) {
        this.flightForUpdate = flight;
        populateFields(flight);
    }

    private void populateFields(Flight flight) {
        departureComboBox.setValue(flight.getDeparture());
        arrivalComboBox.setValue(flight.getDestination());

        // Set date values
        departureDatePicker.setValue(flight.getDepartureTime().toLocalDateTime().toLocalDate());
        arrivalDatePicker.setValue(flight.getArrivalTime().toLocalDateTime().toLocalDate());

        // Set time values
        LocalTime departureTime = flight.getDepartureTime().toLocalDateTime().toLocalTime();
        departureHourComboBox.setValue(String.format("%02d", departureTime.getHour()));
        departureMinuteComboBox.setValue(String.format("%02d", departureTime.getMinute()));

        LocalTime arrivalTime = flight.getArrivalTime().toLocalDateTime().toLocalTime();
        arrivalHourComboBox.setValue(String.format("%02d", arrivalTime.getHour()));
        arrivalMinuteComboBox.setValue(String.format("%02d", arrivalTime.getMinute()));

        priceField.setText(String.valueOf(flight.getPrice()));
        flightNumberField.setText(flight.getFlightNumber());
        numberPlaceField.setText(String.valueOf(flight.getNumbre_place()));
    }

    @FXML
    private void initialize() {
        loadAirports();
        setupTimeComboBoxes();

        cityList.removeIf(city -> city == null || city.trim().isEmpty());
        TextFields.bindAutoCompletion(departureField, cityList).onAutoCompletedProperty();
        TextFields.bindAutoCompletion(destinationField, cityList);
    }

    private void setupTimeComboBoxes() {
        if (departureHourComboBox == null || departureMinuteComboBox == null ||
                arrivalHourComboBox == null || arrivalMinuteComboBox == null) {
            System.err.println("One or more ComboBoxes are null. Check fx:id in FXML.");
            return;
        }

        // Setup hours (00-23)
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        departureHourComboBox.setItems(hours);
        arrivalHourComboBox.setItems(hours);

        // Set default values
        departureHourComboBox.setValue("12");
        arrivalHourComboBox.setValue("14");

        // Setup minutes (00-59)
        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            minutes.add(String.format("%02d", i));
        }
        departureMinuteComboBox.setItems(minutes);
        arrivalMinuteComboBox.setItems(minutes);

        // Set default values
        departureMinuteComboBox.setValue("00");
        arrivalMinuteComboBox.setValue("00");
    }

    private void loadAirports() {
        List<Airport> airports = airportService.afficher();

        departureComboBox.getItems().clear();
        arrivalComboBox.getItems().clear();

        for (Airport airport : airports) {
            departureComboBox.getItems().add(airport.getNameAirport());
            arrivalComboBox.getItems().add(airport.getNameAirport());
        }
    }

    @FXML
    private void handleSave() {
        // Get values from input fields
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        String priceText = priceField.getText().trim();
        String flightNumber = flightNumberField.getText().trim();
        String numberPlaceText = numberPlaceField.getText().trim();
        String departureAirportName = departureComboBox.getValue();
        String arrivalAirportName = arrivalComboBox.getValue();

        // Get date values from DatePicker
        LocalDate departureDate = departureDatePicker.getValue();
        LocalDate arrivalDate = arrivalDatePicker.getValue();

        // Get time values from ComboBox
        String departureHour = departureHourComboBox.getValue();
        String departureMinute = departureMinuteComboBox.getValue();
        String arrivalHour = arrivalHourComboBox.getValue();
        String arrivalMinute = arrivalMinuteComboBox.getValue();

        // Validate empty fields
        if (departure.isEmpty() || destination.isEmpty() || priceText.isEmpty() || flightNumber.isEmpty()
                || numberPlaceText.isEmpty() || departureAirportName == null || arrivalAirportName == null
                || departureDate == null || arrivalDate == null
                || departureHour == null || departureMinute == null
                || arrivalHour == null || arrivalMinute == null) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

        // Validate that departure and destination are different
        if (departure.equalsIgnoreCase(destination)) {
            showAlert("Validation Error", "Departure and destination cannot be the same.");
            return;
        }

        // Create LocalDateTime objects with both date and time
        LocalDateTime departureDateTime = LocalDateTime.of(
                departureDate,
                LocalTime.of(Integer.parseInt(departureHour), Integer.parseInt(departureMinute))
        );

        LocalDateTime arrivalDateTime = LocalDateTime.of(
                arrivalDate,
                LocalTime.of(Integer.parseInt(arrivalHour), Integer.parseInt(arrivalMinute))
        );

        // Validate date and time (departure must be before arrival)
        if (!departureDateTime.isBefore(arrivalDateTime)) {
            showAlert("Validation Error", "Departure date and time must be before arrival date and time.");
            return;
        }

        // Convert to Timestamp
        Timestamp departureTimestamp = Timestamp.valueOf(departureDateTime);
        Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDateTime);

        // Validate price (must be a positive number)
        int price;
        try {
            price = Integer.parseInt(priceText);
            if (price <= 0) {
                showAlert("Validation Error", "Price must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid price. Please enter a valid number.");
            return;
        }

        // Validate number of places (must be a positive integer)
        int numberPlace;
        try {
            numberPlace = Integer.parseInt(numberPlaceText);
            if (numberPlace <= 0) {
                showAlert("Validation Error", "Number of places must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid number of places. Please enter a valid number.");
            return;
        }

        // Validate flight number format (Example: "AA123", 2 letters followed by digits)
        if (!flightNumber.matches("^[A-Z]{2}\\d{2,4}$")) {
            showAlert("Validation Error", "Invalid flight number format. Example: AA123");
            return;
        }

        // Fetch airport details from the database
        Airport departureAirport = airportService.findAirportByName(departureAirportName);
        Airport arrivalAirport = airportService.findAirportByName(arrivalAirportName);

        if (departureAirport == null || arrivalAirport == null) {
            showAlert("Validation Error", "Invalid airport selection.");
            return;
        }

        // Create the Flight object
        Flight flight = new Flight(
                0, // ID is auto-generated
                departure,
                destination,
                departureTimestamp,
                arrivalTimestamp,
                price,
                flightNumber,
                departureAirport,
                arrivalAirport,
                numberPlace
        );

        // Add new flight to the database
        try {
            flightService.ajouter(flight);
            showAlert("Success", "Flight added successfully!");
            priceField.getScene().getWindow().hide(); // Close the window
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while saving the flight.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent actionEvent) {
        priceField.getScene().getWindow().hide(); // Close the window
    }
}