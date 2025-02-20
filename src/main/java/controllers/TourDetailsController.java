package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.AvisTour;
import models.Reservation;
import models.Tour;
import services.AvisService;
import services.ReservationService;
import java.io.IOException;
import java.util.List;

public class TourDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label guideLabel;
    @FXML private ImageView tourImageView;

    // Review Elements
    @FXML private TextArea commentaireTextArea;
    @FXML private ChoiceBox<Integer> etoileChoiceBox;
    @FXML private ListView<AvisTour> reviewsListView;

    private Tour selectedTour;
    private ReservationService reservationService = new ReservationService();
    private AvisService avisService = new AvisService();
    private int clientId = 1;

    public void setTourData(Tour tour) {
        this.selectedTour = tour;
        titleLabel.setText("Title: " + tour.getTitle());
        descriptionLabel.setText("Description: " + tour.getDescription());
        priceLabel.setText("Price: $" + tour.getPrice());
        locationLabel.setText("Location: " + tour.getLocation());
        dateLabel.setText("Date: " + tour.getDate());
        guideLabel.setText("Guide: " + tour.getGuideId());

        // Load tour image
        if (tour.getPhotos() != null && !tour.getPhotos().isEmpty()) {
            tourImageView.setImage(new Image("file:" + tour.getPhotos().get(0)));
        }

        // Populate rating choice box (1 to 5 stars)
        etoileChoiceBox.getItems().addAll(1, 2, 3, 4, 5);
        etoileChoiceBox.setValue(5); // Default rating

        // Load reviews
        loadReviews();
    }

    @FXML
    private void handleSubmitReview() {
        if (selectedTour == null) {
            showAlert("Error", "No tour selected!", Alert.AlertType.ERROR);
            return;
        }

        String commentaire = commentaireTextArea.getText().trim();
        int etoile = etoileChoiceBox.getValue();

        if (commentaire.isEmpty()) {
            showAlert("Error", "Please enter a review.", Alert.AlertType.WARNING);
            return;
        }

        AvisTour selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            // Update existing review
            selectedReview.setCommentaire(commentaire);
            selectedReview.setEtoile(etoile);
            if (avisService.updateAvis(selectedReview)) { // Ensure this method exists
                showAlert("Success", "Review updated successfully!", Alert.AlertType.INFORMATION);
                loadReviews();
                commentaireTextArea.clear();
                etoileChoiceBox.setValue(5);
            } else {
                showAlert("Error", "Failed to update review.", Alert.AlertType.ERROR);
            }
        } else {
            // Create new review
            AvisTour avis = new AvisTour(0, clientId, selectedTour.getId(), etoile, commentaire);
            if (avisService.addAvis(avis)) {
                showAlert("Success", "Review submitted successfully!", Alert.AlertType.INFORMATION);
                loadReviews();
                commentaireTextArea.clear();
                etoileChoiceBox.setValue(5);
            } else {
                showAlert("Error", "Failed to submit review. Make sure you have reserved this tour.", Alert.AlertType.ERROR);
            }
        }
    }

    private void loadReviews() {
        if (selectedTour == null) return;

        List<AvisTour> avisList = avisService.getAllAvis();
        reviewsListView.getItems().clear();
        reviewsListView.getItems().addAll(avisList);

        reviewsListView.setCellFactory(param -> new ListCell<AvisTour>() {
            @Override
            protected void updateItem(AvisTour avis, boolean empty) {
                super.updateItem(avis, empty);
                if (empty || avis == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a card-like structure
                    Label ratingLabel = new Label("⭐".repeat(avis.getEtoile())); // Stars based on rating
                    ratingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

                    Label commentLabel = new Label(avis.getCommentaire());
                    commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

                    VBox reviewBox = new VBox(ratingLabel, commentLabel);
                    reviewBox.setStyle("-fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-color: #dddddd; -fx-border-radius: 8px;");
                    reviewBox.setSpacing(5);

                    setGraphic(reviewBox);
                }
            }
        });
    }

    @FXML
    public void handleUpdateReview() {
        AvisTour selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            // Load the selected review into the text area and choice box for editing
            commentaireTextArea.setText(selectedReview.getCommentaire());
            etoileChoiceBox.setValue(selectedReview.getEtoile());
        } else {
            showAlert("Error", "No review selected!", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleDeleteReview() {
        AvisTour selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            boolean success = avisService.deleteAvis(selectedReview.getId());
            if (success) {
                showAlert("Success", "Review deleted successfully!", Alert.AlertType.INFORMATION);
                loadReviews(); // Reload the reviews after deletion
            } else {
                showAlert("Error", "Failed to delete the review.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No review selected!", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReservePlace() {
        try {
            // For simplicity, assume the client ID is 1 (you can replace this with the actual logged-in user's ID)
            int clientId = 1;
            // Create a new reservation
            Reservation reservation = new Reservation(
                    0, // ID will be auto-generated by the database
                    clientId,
                    selectedTour.getId(),
                    "Pending", // Default status
                    new java.sql.Date(System.currentTimeMillis()) // Current date
            );
            // Add the reservation to the database
            reservationService.addReservation(reservation);
            // Show success message
            showAlert("Success", "Reservation placed successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Failed to place reservation.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIconButtonAction() {
        try {
            // Load the tours_view.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tours_view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) tourImageView.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Tours View.", Alert.AlertType.ERROR);
        }
    }
}