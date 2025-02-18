package controllers;

import models.Tour;
import services.TourService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateTourController {

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private DatePicker dateField;  // Changed from TextField to DatePicker
    @FXML private TextField guideIdField;

    private TourService tourService = new TourService();
    private Tour selectedTour; // The tour being updated

    // Set the selected tour data in the form
    public void setTourData(Tour tour) {
        this.selectedTour = tour;
        titleField.setText(tour.getTitle());
        descriptionField.setText(tour.getDescription());
        priceField.setText(String.valueOf(tour.getPrice()));
        locationField.setText(tour.getLocation());

        // Convert date from String to LocalDate and set it in DatePicker
        if (tour.getDate() != null && !tour.getDate().isEmpty()) {
            dateField.setValue(LocalDate.parse(tour.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        guideIdField.setText(String.valueOf(tour.getGuideId()));
    }

    // Handle the update button action
    @FXML
    private void handleUpdateTour() {
        try {
            // Get updated values from fields
            String title = titleField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String location = locationField.getText();

            // Convert DatePicker value to String
            String date = (dateField.getValue() != null) ? dateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

            int guideId = Integer.parseInt(guideIdField.getText());

            // Update the selected tour
            selectedTour.setTitle(title);
            selectedTour.setDescription(description);
            selectedTour.setPrice(price);
            selectedTour.setLocation(location);
            selectedTour.setDate(date);
            selectedTour.setGuideId(guideId);

            // Save the updated tour to the database
            boolean updated = tourService.updateTour(selectedTour);
            if (updated) {
                showAlert("Success", "Tour updated successfully!", AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update tour.", AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input. Please check the fields!", AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred.", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Show an alert dialog
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
