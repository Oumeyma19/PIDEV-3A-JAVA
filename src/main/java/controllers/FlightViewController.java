package controllers;

import models.Airport;
import services.AirportService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Flight;
import services.FlightService;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class FlightViewController {

    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private TableColumn<Flight, Integer> idFlightColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> destinationColumn;
    @FXML
    private TableColumn<Flight, String> departureTimeColumn;
    @FXML
    private TableColumn<Flight, String> arrivalTimeColumn;
    @FXML
    private TableColumn<Flight, Double> priceColumn;
    @FXML
    private TableColumn<Flight, String> flightNumberColumn;
    @FXML
    private TableColumn<Flight, String> departureAirportColumn;
    @FXML
    private TableColumn<Flight, String> arrivalAirportColumn;
    @FXML
    private TableColumn<Flight, Integer> NumbrePlaceColumn;
    @FXML
    private TableColumn<Flight, Void> actionsColumn;

    private FlightService flightService;
    private AirportService airportService;  // Add AirportService to load airports

    public FlightViewController() {
        flightService = new FlightService();
        airportService = new AirportService(); // Initialize AirportService
    }


    @FXML
    public void initialize() {
        // Bind data to columns

        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureProperty());
        destinationColumn.setCellValueFactory(cellData -> cellData.getValue().destinationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty().asString());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asString());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject().map(Number::doubleValue));
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureAirportColumn.setCellValueFactory(cellData -> cellData.getValue().departureAirportProperty().get().nameProperty());
        arrivalAirportColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalAirportProperty().get().nameProperty());
        NumbrePlaceColumn.setCellValueFactory(cellData -> cellData.getValue().numbre_placeProperty().asObject());


        // Add action buttons
        addActionsColumn();

        // Load flights into the table
        loadFlights();
    }

    private void loadFlights() {
        List<Flight> flights = flightService.afficher();
        flightTable.getItems().setAll(flights);
    }

    @FXML
    private void handleAddFlight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddFlight.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadFlights(); // Refresh the table after adding a flight
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateFlight(Flight flight) {
        // Create a custom dialog
        Dialog<Flight> dialog = new Dialog<>();
        dialog.setTitle("Modifier le vol");
        dialog.setHeaderText("Modifier les détails du vol");

        // Set the buttons
        ButtonType updateButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField departureField = new TextField(flight.getDeparture());
        TextField destinationField = new TextField(flight.getDestination());
        DatePicker departureDatePicker = new DatePicker(flight.getDepartureTime().toLocalDateTime().toLocalDate());
        DatePicker arrivalDatePicker = new DatePicker(flight.getArrivalTime().toLocalDateTime().toLocalDate());
        TextField priceField = new TextField(String.valueOf(flight.getPrice()));
        TextField flightNumberField = new TextField(flight.getFlightNumber());
        TextField NumbrePlaceField = new TextField(String.valueOf(flight.getNumbre_place()));


        // ComboBoxes for departure and arrival airports
        ComboBox<String> departureAirportComboBox = new ComboBox<>();
        ComboBox<String> arrivalAirportComboBox = new ComboBox<>();

        // Populate ComboBoxes with existing airports (fetch only names of airports)
        List<Airport> airports = airportService.afficher(); // Fetch list of airports
        List<String> airportNames = airports.stream()
                .map(Airport::getNameAirport) // Extract airport names
                .collect(Collectors.toList()); // Convert to a list of strings

// Add airport names to ComboBoxes
        departureAirportComboBox.getItems().addAll(airportNames);
        arrivalAirportComboBox.getItems().addAll(airportNames);


        // Set the current airports as selected in the ComboBoxes
        departureAirportComboBox.setValue(flight.getDepartureAirport().getNameAirport());
        arrivalAirportComboBox.setValue(flight.getArrivalAirport().getNameAirport());

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Départ:"), 0, 0);
        grid.add(departureField, 1, 0);
        grid.add(new Label("Destination:"), 0, 1);
        grid.add(destinationField, 1, 1);
        grid.add(new Label("Date du départ:"), 0, 2);
        grid.add(departureDatePicker, 1, 2);
        grid.add(new Label("Date d'arrivée:"), 0, 3);
        grid.add(arrivalDatePicker, 1, 3);
        grid.add(new Label("Prix:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Nombre du vol:"), 0, 5);
        grid.add(flightNumberField, 1, 5);
        grid.add(new Label("Aéroport du départ:"), 0, 6);
        grid.add(departureAirportComboBox, 1, 6);
        grid.add(new Label("Aéroport d'arrivéet:"), 0, 7);
        grid.add(arrivalAirportComboBox, 1, 7);
        grid.add(new Label("Nombre de places:"), 0, 8);
        grid.add(NumbrePlaceField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Flight object when the Update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                // Fetch selected airports based on ComboBox values
                String departureAirportName = departureAirportComboBox.getValue();
                String arrivalAirportName = arrivalAirportComboBox.getValue();

                // Fetch Airport objects from the database
                Airport departureAirport = airportService.findAirportByName(departureAirportName);
                Airport arrivalAirport = airportService.findAirportByName(arrivalAirportName);

                // Create the updated Flight object with the new values
                return new Flight(
                        flight.getIdFlight(), // Keep the existing ID
                        departureField.getText(),
                        destinationField.getText(),
                        Timestamp.valueOf(departureDatePicker.getValue().atStartOfDay()),
                        Timestamp.valueOf(arrivalDatePicker.getValue().atStartOfDay()),
                        Integer.parseInt(priceField.getText()), // Parse the price
                        flightNumberField.getText(),
                        departureAirport, // Use the actual Airport object
                        arrivalAirport,
                        Integer.parseInt(NumbrePlaceField.getText())// Use the actual Airport object
                );
            }
            return null;
        });

        // Show and wait for user input
        dialog.showAndWait().ifPresent(updatedFlight -> {
            // Update the flight in the database using the flight service
            flightService.modifier(updatedFlight, flight.getFlightNumber());
            loadFlights(); // Refresh the table with the updated flight
        });
    }


    private void handleDeleteFlight(Flight flight) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer vol");
        alert.setHeaderText("Es-tu sur de supprimer le vol ?");
        alert.setContentText("Nombre du vol " + flight.getFlightNumber());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                flightService.supprimer(flight); // Delete the flight using the flightService
                loadFlights(); // Refresh the flight table
            }
        });
    }


    private void addActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<Flight, Void>, TableCell<Flight, Void>>() {
            @Override
            public TableCell<Flight, Void> call(final TableColumn<Flight, Void> param) {
                return new TableCell<Flight, Void>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        // Set actions for buttons
                        updateButton.setOnAction(event -> {
                            Flight flight = getTableView().getItems().get(getIndex());
                            handleUpdateFlight(flight);
                        });

                        deleteButton.setOnAction(event -> {
                            Flight flight = getTableView().getItems().get(getIndex());
                            handleDeleteFlight(flight);
                        });

                        HBox hbox = new HBox(10, updateButton, deleteButton);
                        setGraphic(hbox);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Ensures buttons are re-attached after every refresh
                            setGraphic(new HBox(10, updateButton, deleteButton));
                        }
                    }
                };
            }
        });
    }

}
