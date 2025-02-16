package controllers;

import models.Tour;
import services.TourService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.List;

public class ToursViewController {

    @FXML
    private ListView<Tour> toursListView;

    private TourService tourService = new TourService();

    @FXML
    public void initialize() {
        // Fetch all tours with one photo and guide's name
        List<Tour> tours = tourService.getAllToursWithOnePhoto();

        // Set the tours in the ListView
        toursListView.getItems().addAll(tours);

        // Set the custom cell factory
        toursListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Tour> call(ListView<Tour> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Tour tour, boolean empty) {
                        super.updateItem(tour, empty);

                        if (empty || tour == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Create an HBox to hold the tour details, photo, and buttons
                            HBox hbox = new HBox(10);
                            hbox.setPadding(new Insets(10));

                            // Add tour details
                            Text details = new Text(
                                    "Title: " + tour.getTitle() + "\n" +
                                            "Description: " + tour.getDescription() + "\n" +
                                            "Price: $" + tour.getPrice() + "\n" +
                                            "Location: " + tour.getLocation() + "\n" +
                                            "Date: " + tour.getDate() + "\n" +
                                            "Guide: " + tour.getGuideId()
                            );

                            // Add photo (if available)
                            if (tour.getPhotos() != null && !tour.getPhotos().isEmpty()) {
                                ImageView imageView = new ImageView(new Image("file:" + tour.getPhotos().get(0)));
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                hbox.getChildren().add(imageView);
                            }

                            // Add buttons for delete and update
                            Button deleteButton = new Button("Delete");
                            Button updateButton = new Button("Update");

                            // Handle delete button action
                            deleteButton.setOnAction(event -> {
                                boolean deleted = tourService.deleteTour(tour.getId());
                                if (deleted) {
                                    showAlert("Success", "Tour deleted successfully!", Alert.AlertType.INFORMATION);
                                    refreshToursList(); // Refresh the list after deletion
                                } else {
                                    showAlert("Error", "Failed to delete tour.", Alert.AlertType.ERROR);
                                }
                            });

                            // Handle update button action
                            updateButton.setOnAction(event -> {
                                // Open a new window or dialog to update the tour
                                // For now, just show a message
                                showAlert("Update", "Update functionality not implemented yet.", Alert.AlertType.INFORMATION);
                            });

                            // Add details and buttons to the HBox
                            hbox.getChildren().addAll(details, deleteButton, updateButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    // Refresh the tours list
    private void refreshToursList() {
        toursListView.getItems().clear();
        List<Tour> tours = tourService.getAllToursWithOnePhoto();
        toursListView.getItems().addAll(tours);
    }

    // Show an alert dialog
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}