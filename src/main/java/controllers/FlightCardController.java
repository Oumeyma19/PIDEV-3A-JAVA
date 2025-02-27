package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent; // ✅ FIXED import

import models.Flight;
import models.User;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class FlightCardController {

    private Flight flight;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    @FXML
    private Label departureTimeLabel, arrivalTimeLabel, departureAirportLabel, arrivalAirportLabel, priceLabel;

    @FXML
    private Button viewDetailsButton;

    public void setFlightData(Flight flight) {
        this.flight = flight;

        departureTimeLabel.setText(sdf.format(flight.getDepartureTime()));
        arrivalTimeLabel.setText(sdf.format(flight.getArrivalTime()));
        departureAirportLabel.setText(flight.getDepartureAirport().getCode());
        arrivalAirportLabel.setText(flight.getArrivalAirport().getCode());
        priceLabel.setText(flight.getPrice() + " €");
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        if (flight == null) {
            showAlert("Error", "No flight data available.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightDetails.fxml"));
            Parent root = loader.load();

            // Get the controller and pass flight data
            FlightDetailsController detailsController = loader.getController();
            detailsController.setCurrentUser(currentUser);
            detailsController.setFlightData(flight);

            // Get current stage
            Stage stage = new Stage();

            // Create new scene and apply styles
            Scene scene = new Scene(root);
            String css = getClass().getResource("/views/style.css").toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            } else {
                System.out.println("⚠ CSS file not found!");
            }

            // Set the scene, restore window decorations and title
            stage.setTitle("Flight Details");
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Flight Details view.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("User received: " + currentUser.getFirstname()); // Debugging
    }
}
