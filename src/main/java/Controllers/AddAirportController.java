package Controllers;

import Models.Airport;
import Services.AirportService;
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
        String name = nameField.getText();
        String location = locationField.getText();
        String code = codeField.getText();

        if (name.isEmpty() || location.isEmpty() || code.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

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
