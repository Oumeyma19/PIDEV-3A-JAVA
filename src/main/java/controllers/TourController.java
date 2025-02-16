package controllers;

import models.Tour;
import services.TourService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class TourController {

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private TextField dateField;
    @FXML private TextField guideIdField;
    @FXML private ImageView imageView;

    private TourService tourService = new TourService();
    private List<String> selectedImages = new ArrayList<>();

    // 📌 Handle Image Selection
    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destDir = "uploads/tour_photos/";
                File destFile = new File(destDir + selectedFile.getName());

                // Create folder if not exists
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }

                // Copy the file to destination
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update UI
                imageView.setImage(new Image(destFile.toURI().toString()));
                selectedImages.add(destFile.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Failed to upload image!", AlertType.ERROR);
            }
        }
    }

    // 📌 Handle Adding Tour
    @FXML
    private void handleAddTour() {
        try {
            // Get values from fields
            String title = titleField.getText();
            String description = descriptionField.getText();
            String priceStr = priceField.getText();
            String location = locationField.getText();
            String date = dateField.getText();
            String guideIdStr = guideIdField.getText();

            // Debugging: Print all field values
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Price: " + priceStr);
            System.out.println("Location: " + location);
            System.out.println("Date: " + date);
            System.out.println("Guide ID: " + guideIdStr);

            // Validate that none of the fields are empty
            if (title == null || title.isEmpty()) {
                showAlert("Error", "Title cannot be empty!", AlertType.ERROR);
                return;
            }
            if (description == null || description.isEmpty()) {
                showAlert("Error", "Description cannot be empty!", AlertType.ERROR);
                return;
            }
            if (priceStr == null || priceStr.isEmpty()) {
                showAlert("Error", "Price cannot be empty!", AlertType.ERROR);
                return;
            }
            if (location == null || location.isEmpty()) {
                showAlert("Error", "Location cannot be empty!", AlertType.ERROR);
                return;
            }
            if (date == null || date.isEmpty()) {
                showAlert("Error", "Date cannot be empty!", AlertType.ERROR);
                return;
            }
            if (guideIdStr == null || guideIdStr.isEmpty()) {
                showAlert("Error", "Guide ID cannot be empty!", AlertType.ERROR);
                return;
            }

            // Convert the price and guideId fields to the correct types
            double price = Double.parseDouble(priceStr);
            int guideId = Integer.parseInt(guideIdStr);

            // Create new Tour object
            Tour newTour = new Tour(title, description, price, location, date, guideId);

            // Store the tour and get the generated ID
            int tourId = tourService.addTour(newTour, selectedImages); // Pass selected images directly to the service

            // Check if the tour was added successfully
            if (tourId != -1) {
                showAlert("Success", "Tour added successfully!", AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to add tour. Please try again.", AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid number format. Please check the price and guide ID fields!", AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred. Please try again.", AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Show alert message
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
