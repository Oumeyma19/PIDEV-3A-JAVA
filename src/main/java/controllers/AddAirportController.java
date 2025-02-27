package controllers;

import models.Airport;
import services.AirportService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddAirportController {
    @FXML private TextField nameField;
    @FXML private TextField locationField;
    @FXML private TextField codeField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final AirportService airportService = new AirportService();

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String code = codeField.getText().trim();

        // Check if fields are empty
        if (name.isEmpty() || location.isEmpty() || code.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }



        // Validate airport name (allows letters, hyphens (-), spaces, but no leading/trailing spaces)
        if (!name.matches("^[A-Za-z][A-Za-z\\-]*( [A-Za-z\\-]+)*$")) {
            showAlert("Validation Error", "Airport name can only contain letters, hyphens (-), and spaces (but no leading spaces).");
            return;
        }



        // Validate location (only letters and spaces)
        if (!location.matches("^[A-Za-z\\s]+$")) {
            showAlert("Validation Error", "Location must contain only letters and spaces.");
            return;
        }

        // Validate airport code (3 uppercase letters)
        if (!code.matches("^[A-Z]{3}$")) {
            showAlert("Validation Error", "Airport code must be exactly 3 uppercase letters (e.g., JFK, LAX).");
            return;
        }

        // Create and save the airport
        Airport newAirport = new Airport(0, name, location, code); // ID is auto-generated
        airportService.ajouter(newAirport);

        showAlert("Success", "Airport added successfully!");
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}