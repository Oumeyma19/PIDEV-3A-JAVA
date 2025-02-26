package controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.Flight;
import services.FlightService;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FlightSearchController implements Initializable {

    /*@FXML
    private ComboBox<String> departureField;*/

    @FXML
    private TextField departureField;

    @FXML
    private TextField arrivalField;

  /*  @FXML
    private ComboBox<String> arrivalField;*/

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private DatePicker arrivalDatePicker;

    @FXML
    private VBox flightResultsContainer;

    private FlightService flightService = new FlightService();
    private ObservableList<String> cityList = FXCollections.observableArrayList(flightService.getCities());


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Fetch cities from DB and filter out null or empty values

        cityList.removeIf(city -> city == null || city.trim().isEmpty());
       /* departureField.setItems(cityList);
        arrivalField.setItems(cityList);*/

       /* setupAutoComplete(departureField, cityList);
        setupAutoComplete(arrivalField, cityList);*/

        TextFields.bindAutoCompletion(departureField, cityList).onAutoCompletedProperty();
        TextFields.bindAutoCompletion(arrivalField, cityList);


    }

    private void setupAutoComplete(ComboBox<String> comboBox, ObservableList<String> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            System.out.println("Data list is empty or null.");
            return;
        }

        // Create a filtered list based on the data list
        FilteredList<String> filteredItems = new FilteredList<>(dataList, p -> true);
        comboBox.setItems(filteredItems);
        comboBox.setEditable(true);

        // Store the text property listener in a variable
        ChangeListener<String> textPropertyListener = (observable, oldValue, newValue) -> {
            System.out.println("Text changed: " + newValue);

            // Only update the filter if the text actually changed
            if (!newValue.equals(oldValue)) {
                // Filter the items based on the new value
                filteredItems.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true; // Show all items if the input is empty
                    }
                    return item.toLowerCase().contains(newValue.toLowerCase());
                });

                // Debug: Print the filtered items
                System.out.println("Filtered Items: " + filteredItems);

                // Show the dropdown if it's not already showing and there are filtered items
                if (!comboBox.isShowing() && !filteredItems.isEmpty()) {
                    comboBox.show();
                }
            }
        };

        // Add the text property listener
        comboBox.getEditor().textProperty().addListener(textPropertyListener);

        // Handle selection from the dropdown
        comboBox.setOnAction(event -> {
            String selectedItem = comboBox.getSelectionModel().getSelectedItem();
            System.out.println("Selected Item: " + selectedItem);

            if (selectedItem != null) {
                // Temporarily remove the text property listener to avoid infinite loops
                comboBox.getEditor().textProperty().removeListener(textPropertyListener);

                // Set the selected item in the editor
                comboBox.getEditor().setText(selectedItem);

                // Re-add the text property listener
                comboBox.getEditor().textProperty().addListener(textPropertyListener);

                // Manually update the selection model to avoid triggering the textProperty listener
                comboBox.getSelectionModel().select(selectedItem);
            }
        });

        // Ensure the dropdown is shown when the ComboBox gains focus
        comboBox.getEditor().setOnMouseClicked(event -> {
            System.out.println("ComboBox clicked, showing dropdown.");
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
        });

        // Debug: Add a listener to the selection model to track changes
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selection Model - Selected Item: " + newValue);
        });
    }

    @FXML
    private void handleSearch() {
        if (departureField.getText() == null || arrivalField.getText() == null ||
                departureField.getText().isEmpty() || arrivalField.getText().isEmpty()) {
            showAlert("Error", "Departure and Arrival locations are required.");
            return;
        }

        LocalDate departureDate = departureDatePicker.getValue();
        LocalDate arrivalDate = arrivalDatePicker.getValue();

        if (departureDate == null || arrivalDate == null) {
            showAlert("Error", "Please select both departure and arrival dates.");
            return;
        }

        if (arrivalDate.isBefore(departureDate)) {
            showAlert("Error", "Arrival date cannot be before departure date.");
            return;
        }

        Timestamp departureTimestamp = Timestamp.valueOf(departureDate.atStartOfDay());
        Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalDate.atStartOfDay());

        List<Flight> flights = flightService.searchFlights(
                departureField.getText(),
                arrivalField.getText(),
                departureTimestamp,
                arrivalTimestamp
        );

        flightResultsContainer.getChildren().clear();

        for (Flight flight : flights) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightCard.fxml"));
                VBox flightCard = loader.load();

                FlightCardController cardController = loader.getController();
                cardController.setFlightData(flight);

                flightResultsContainer.getChildren().add(flightCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load flight card: " + e.getMessage());
            }
        }
    }
    @FXML
    private void handleSwap() {
        String departure = departureField.getText();
        String arrival = arrivalField.getText();

        departureField.setText(arrival);
        arrivalField.setText(departure);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

