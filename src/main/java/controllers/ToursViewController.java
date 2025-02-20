package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Tour;
import services.TourService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class ToursViewController {

    @FXML
    private FlowPane toursFlowPane;

    @FXML
    private Button addTourButton;

    @FXML
    private TextField searchField; // Search field

    private TourService tourService = new TourService();

    @FXML
    public void initialize() {
        // Load all tours initially
        refreshTours();
        addTourButton.setOnAction(event -> openAddTourView());
    }

    // Handle search button action
    @FXML
    private void handleSearch() {
        String location = searchField.getText().trim();
        if (location.isEmpty()) {
            // If the search field is empty, show all tours
            refreshTours();
        } else {
            // Filter tours by location
            List<Tour> filteredTours = tourService.getToursByLocation(location);
            displayTours(filteredTours);
        }
    }

    // Display tours in the FlowPane
    private void displayTours(List<Tour> tours) {
        toursFlowPane.getChildren().clear(); // Clear existing tours
        for (Tour tour : tours) {
            VBox tourContainer = createTourContainer(tour);
            toursFlowPane.getChildren().add(tourContainer);
        }
    }

    // Refresh tours (load all tours)
    private void refreshTours() {
        List<Tour> tours = tourService.getAllToursWithOnePhoto();
        displayTours(tours);
    }

    // Create a tour container (VBox) for each tour
    private VBox createTourContainer(Tour tour) {
        VBox tourContainer = new VBox(10);
        tourContainer.setPadding(new Insets(10));
        tourContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-border-color: #ddd; -fx-border-radius: 5;");

        tourContainer.setMaxWidth(300);
        tourContainer.setMinWidth(300);

        // Image
        if (tour.getPhotos() != null && !tour.getPhotos().isEmpty()) {
            ImageView imageView = new ImageView(new Image("file:" + tour.getPhotos().get(0)));
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            tourContainer.getChildren().add(imageView);
        }

        // Location
        Text tourLocal = new Text(tour.getLocation());
        tourLocal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        ImageView logo = new ImageView(new Image("logo/local.svg"));
        logo.setFitHeight(20);
        logo.setPreserveRatio(true);

        HBox tourLocationContainer = new HBox(10);
        tourLocationContainer.getChildren().addAll(logo, tourLocal);
        tourContainer.getChildren().add(tourLocationContainer);

        // Tour Name
        Text tourName = new Text(tour.getTitle());
        tourName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        tourContainer.getChildren().add(tourName);

        // Price and Buttons
        HBox priceAndButtons = new HBox(10);
        priceAndButtons.setPadding(new Insets(5, 0, 5, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);

        Text priceText = new Text("$" + tour.getPrice());
        priceText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");
        priceAndButtons.getChildren().add(priceText);

        // Consult Button
        Button consultButton = new Button("Consult");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> openTourDetails(tour));

        // Update Button
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(event -> updateTour(tour));

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(event -> deleteTour(tour, tourContainer));

        // Add buttons
        priceAndButtons.getChildren().addAll(consultButton, updateButton, deleteButton);
        tourContainer.getChildren().add(priceAndButtons);

        return tourContainer;
    }

    // Open tour details
    private void openTourDetails(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour_details.fxml"));
            Parent root = loader.load();

            TourDetailsController detailsController = loader.getController();
            detailsController.setTourData(tour);

            Stage stage = (Stage) toursFlowPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tour Details");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load tour details.", Alert.AlertType.ERROR);
        }
    }

    // Delete a tour
    private void deleteTour(Tour tour, VBox tourContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Tour");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this tour?");

        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                tourService.deleteTour(tour.getId());
                toursFlowPane.getChildren().remove(tourContainer);
                showAlert("Success", "Tour deleted successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }

    // Update a tour
    private void updateTour(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_tour.fxml"));
            Parent root = loader.load();

            UpdateTourController updateTourController = loader.getController();
            updateTourController.setTourData(tour);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Tour");
            stage.setOnHidden(event -> refreshTours());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load update form.", Alert.AlertType.ERROR);
        }
    }

    // Show an alert dialog
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Open the add tour view
    @FXML
    private void openAddTourView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTourView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addTourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Tour");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}