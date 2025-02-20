package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Offre;
import services.OffreService;

import java.io.File;
import java.sql.SQLException;

public class AjouterOffre {

    @FXML
    private TextField offreNameField, descriptionField, prixField;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private Button ajouterButton, btnChooseImage;

    @FXML
    private ImageView imageView;

    private String imagePath = null; // Store selected image path

    private final OffreService offreService = new OffreService(); // Use service for database operations

    @FXML
    public void initialize() {
        ajouterButton.setOnAction(event -> ajouterOffre());
        btnChooseImage.setOnAction(event -> chooseImage());
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imagePath = file.toURI().toString();
            imageView.setImage(new Image(imagePath));
        } else {
            showAlert("Error", "No image selected.");
        }
    }

    private void ajouterOffre() {
        String title = offreNameField.getText().trim();
        String description = descriptionField.getText().trim();
        String prixText = prixField.getText().trim();
        String startDate = (startDatePicker.getValue() != null) ? startDatePicker.getValue().toString() : null;
        String endDate = (endDatePicker.getValue() != null) ? endDatePicker.getValue().toString() : null;

        // **Validation**
        if (title.isEmpty() || description.isEmpty() || prixText.isEmpty() || startDate == null || endDate == null || imagePath == null) {
            showAlert("Error", "All fields are required!");
            return;
        }

        try {
            double price = Double.parseDouble(prixText);
            if (price <= 0) {
                showAlert("Error", "Price must be a positive number!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price! Please enter a valid number.");
            return;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Error", "End date must be after the start date!");
            return;
        }

        // **Create Offre Object**
        Offre offre = new Offre();
        offre.setTitle(title);
        offre.setDescription(description);
        offre.setPrice(Double.parseDouble(prixText));
        offre.setStartDate(startDate);
        offre.setEndDate(endDate);
        offre.setImagePath(imagePath);

        // **Save Offer using Service**
        try {
            offreService.ajouter(offre);
            showAlert("Success", "Offer successfully added!");
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add offer!");
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

    private void clearFields() {
        offreNameField.clear();
        descriptionField.clear();
        prixField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        imageView.setImage(null);
        imagePath = null;
    }
}
