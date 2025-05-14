package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Tour;
import services.TourService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateTourController {

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private DatePicker dateField;
    @FXML private TextField nbPlaceDisponibleField; // New field for available places
    @FXML private ImageView imageView;  // ImageView to display and update photo

    private TourService tourService = new TourService();
    private Tour selectedTour; // The tour being updated

    private String newImagePath = null; // Store the path of the new image

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

        // Set the number of available places
        nbPlaceDisponibleField.setText(String.valueOf(tour.getNbPlaceDisponible()));

        // Display the current image (if any)
        if (tour.getPhoto() != null && !tour.getPhoto().isEmpty()) {
            File file = new File(tour.getPhoto());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    // Handle the update button action
    @FXML
    private void handleUpdateTour() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return; // Stop if validation fails
            }

            // Get updated values from fields
            String title = titleField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String location = locationField.getText();

            // Convert DatePicker value to String
            String date = (dateField.getValue() != null) ? dateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

            // Get the number of available places
            int nbPlaceDisponible = Integer.parseInt(nbPlaceDisponibleField.getText());

            // Update the selected tour
            selectedTour.setTitle(title);
            selectedTour.setDescription(description);
            selectedTour.setPrice(price);
            selectedTour.setLocation(location);
            selectedTour.setDate(date);
            selectedTour.setNbPlaceDisponible(nbPlaceDisponible); // Update available places

            // Set the new image if provided
            if (newImagePath != null && !newImagePath.isEmpty()) {
                selectedTour.setPhoto(newImagePath); // Assuming you have a setter for image path in your Tour model
            }

            // Save the updated tour to the database
            boolean updated = tourService.modifier(selectedTour);
            if (updated) {
                showAlert("Success", "Tour updated successfully!", AlertType.INFORMATION);

                // Close the update window after successful update
                Stage stage = (Stage) titleField.getScene().getWindow();
                stage.close();
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

    // Validate user inputs
    private boolean validateInputs() {
        // Check for empty fields
        if (titleField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
                priceField.getText().isEmpty() || locationField.getText().isEmpty() ||
                dateField.getValue() == null || nbPlaceDisponibleField.getText().isEmpty()) {
            showAlert("Error", "All fields are required!", AlertType.ERROR);
            return false;
        }

        // Validate price (must be a positive number)
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showAlert("Error", "Price must be a positive number!", AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price format. Please enter a valid number!", AlertType.ERROR);
            return false;
        }

        // Validate number of available places (must be a positive integer)
        try {
            int nbPlaceDisponible = Integer.parseInt(nbPlaceDisponibleField.getText());
            if (nbPlaceDisponible < 0) {
                showAlert("Error", "Number of available places must be a positive integer!", AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid number of available places. Please enter a valid integer!", AlertType.ERROR);
            return false;
        }

        // Validate date (must not be in the past)
        LocalDate selectedDate = dateField.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert("Error", "Date cannot be in the past!", AlertType.ERROR);
            return false;
        }

        return true; // All inputs are valid
    }

    // Show an alert dialog
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Open a file chooser to select a new image for the tour
    @FXML
    private void handleImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif"));
        Stage stage = (Stage) imageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Copy the selected image to a specific directory
                String destDir = System.getProperty("user.home") + "/uploads/tour_photos/";
                File destFile = new File(destDir + selectedFile.getName());

                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }

                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update the ImageView with the selected image
                newImagePath = destFile.getAbsolutePath();
                imageView.setImage(new Image(new FileInputStream(selectedFile)));
            } catch (IOException e) {
                showAlert("Error", "Failed to load the image.", AlertType.ERROR);
            }
        }
    }
}