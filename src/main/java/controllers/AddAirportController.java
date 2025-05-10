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
            showAlert("Erreur de validation", "Tous les champs doivent être remplis.");
            return;
        }



        // Validate airport name (allows letters, hyphens (-), spaces, but no leading/trailing spaces)
        if (!name.matches("^[A-Za-z][A-Za-z\\-]*( [A-Za-z\\-]+)*$")) {
            showAlert("Erreur de validation", "Le nom de l'aéroport ne peut contenir que des lettres, des traits d'union (-) et des espaces (mais pas d'espaces en tête).");
            return;
        }



        // Validate location (only letters and spaces)
        if (!location.matches("^[A-Za-z\\s]+$")) {
            showAlert("Erreur de validation", "L'emplacement ne doit contenir que des lettres et des espaces.");
            return;
        }

        // Validate airport code (3 uppercase letters)
        if (!code.matches("^[A-Z]{3}$")) {
            showAlert("Erreur de validation", "Le code de l'aéroport doit comporter exactement 3 lettres majuscules (par exemple, JFK, LAX).");
            return;
        }

        // Create and save the airport
        Airport newAirport = new Airport(0, name, location, code); // ID is auto-generated
        airportService.ajouter(newAirport);

        showAlert("Succès", "Aéroport ajouté avec succès !");
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
