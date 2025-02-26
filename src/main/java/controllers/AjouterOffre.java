package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Flight;
import models.Hebergements;
import models.Offre;
import models.Tour;
import services.FlightService;
import services.HebergementsService;
import services.OffreService;
import services.TourService;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AjouterOffre {

    @FXML
    private TextField offreNameField, descriptionField, prixField;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private Button ajouterButton, btnChooseImage;

    @FXML
    private ImageView imageView;

    @FXML
    private ComboBox<Hebergements> hebergementsComboBox;

    @FXML
    private ComboBox<Tour> toursComboBox;

    @FXML
    private ComboBox<Flight> flightsComboBox;

    private String imagePath = null; // Store selected image path

    private final OffreService offreService = new OffreService();
    private final HebergementsService hebergementsService = new HebergementsService();
    private final TourService tourService = new TourService();
    private final FlightService flightService = new FlightService();

    private static final double DISCOUNT_PERCENTAGE = 10.0; // Configurable discount percentage

    @FXML
    public void initialize() throws SQLException {
        ajouterButton.setOnAction(event -> ajouterOffre());
        btnChooseImage.setOnAction(event -> chooseImage());

        // Load data into ComboBoxes
        loadHebergements();
        loadTours();
        loadFlights();

        // Update price when selection changes
        hebergementsComboBox.setOnAction(event -> updatePrice());
        toursComboBox.setOnAction(event -> updatePrice());
        flightsComboBox.setOnAction(event -> updatePrice());
    }

    private void updatePrice() {
        Hebergements selectedHebergement = hebergementsComboBox.getValue();
        Tour selectedTour = toursComboBox.getValue();
        Flight selectedFlight = flightsComboBox.getValue();

        double totalPrice = 0.0;

        if (selectedHebergement != null) {
            totalPrice += selectedHebergement.getPrixHeberg();
        }
        if (selectedTour != null) {
            totalPrice += selectedTour.getPrice();
        }
        if (selectedFlight != null) {
            totalPrice += selectedFlight.getPrice();
        }

        // Apply discount
        double discountedPrice = totalPrice - (totalPrice * DISCOUNT_PERCENTAGE / 100);

        // Update the price field
        prixField.setText(String.format("%.2f", discountedPrice));
    }

    private void loadHebergements() {
        try {
            List<Hebergements> hebergements = hebergementsService.recuperer();
            hebergementsComboBox.getItems().addAll(hebergements);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load Hebergements!");
            e.printStackTrace();
        }
    }

    private void loadTours() throws SQLException {
        List<Tour> tours = tourService.recuperer();
        toursComboBox.getItems().addAll(tours);
    }

    private void loadFlights() {
        List<Flight> flights = flightService.afficher();
        flightsComboBox.getItems().addAll(flights);
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
        String startDate = (startDatePicker.getValue() != null) ? startDatePicker.getValue().toString() : null;
        String endDate = (endDatePicker.getValue() != null) ? endDatePicker.getValue().toString() : null;

        // Validation
        if (title.isEmpty() || description.isEmpty() || startDate == null || endDate == null || imagePath == null) {
            showAlert("Error", "All fields are required!");
            return;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("Error", "End date must be after the start date!");
            return;
        }

        // Retrieve selected items
        Hebergements selectedHebergement = hebergementsComboBox.getValue();
        Tour selectedTour = toursComboBox.getValue();
        Flight selectedFlight = flightsComboBox.getValue();

        if (selectedHebergement == null || selectedTour == null || selectedFlight == null) {
            showAlert("Error", "Please select Hebergement, Tour, and Flight!");
            return;
        }

        // Calculate the original price
        double originalPrice = selectedHebergement.getPrixHeberg() + selectedTour.getPrice() + selectedFlight.getPrice();

        // Apply discount

        // Create Offre Object
        Offre offre = new Offre();
        offre.setTitle(title);
        offre.setDescription(description);
        offre.setPrice(originalPrice); // Store only the original price
        offre.setStartDate(startDate);
        offre.setEndDate(endDate);
        offre.setImagePath(imagePath);

// Set selected Hebergements, Tours, and Flights
        offre.setHebergements(List.of(selectedHebergement));
        offre.setTours(List.of(selectedTour));
        offre.setFlights(List.of(selectedFlight));

// Save Offer using Service
        try {
            offreService.ajouter(offre, List.of(selectedHebergement.getIdHebrg()),
                    List.of(selectedTour.getId()),
                    List.of(selectedFlight.getIdFlight()));
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
        hebergementsComboBox.getSelectionModel().clearSelection();
        toursComboBox.getSelectionModel().clearSelection();
        flightsComboBox.getSelectionModel().clearSelection();
    }
}