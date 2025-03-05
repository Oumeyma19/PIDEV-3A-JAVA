    package controllers;

    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.geometry.Pos;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
    import javafx.stage.Screen;
    import javafx.stage.Stage;
    import javafx.util.StringConverter;
    import models.*;
    import services.AvisTourService;
    import services.ReservationTourService;
    import services.TourService;

    import java.sql.Date;
    import java.sql.SQLException;
    import java.util.List;
    
    public class TourDetailsController {
    
        @FXML private Label titleLabel;
        @FXML private Label descriptionLabel;
        @FXML private Label priceLabel;
        @FXML private Label locationLabel;
        @FXML private Label dateLabel;
        @FXML private Label guideLabel;
        @FXML private Label availablePlacesLabel;
        @FXML private ImageView tourImageView;
        @FXML private User currentUser;
    
        // Review Elements
        @FXML private TextArea commentaireTextArea;
        @FXML private ChoiceBox<Integer> etoileChoiceBox;
        @FXML private ListView<AvisTour> reviewsListView;
    
        // Activities ListView
        @FXML private ListView<Activites> activitiesListView;
    
        private Tour selectedTour;
        private ReservationTourService reservationService = new ReservationTourService();
        private AvisTourService avisService = new AvisTourService();
        private TourService tourService = new TourService();
    
        public void setCurrentUser(User user) throws SQLException {
            this.currentUser = user;
            System.out.println("Current User id: " + (currentUser != null ? currentUser.getId() : "null"));
        }
    
        public void setTourData(Tour tour) throws SQLException {
            this.selectedTour = tour;
    
            // Set tour details
            titleLabel.setText("Title: " + tour.getTitle());
            descriptionLabel.setText("Description: " + tour.getDescription());
            priceLabel.setText("Price: $" + tour.getPrice());
            locationLabel.setText("Location: " + tour.getLocation());
            dateLabel.setText("Date: " + tour.getDate());
    
            // Calculate and display available places
            int availablePlaces = tour.getNbPlaceDisponible() - tour.getNbPlaceReserver();
            availablePlacesLabel.setText("Available Places: " + availablePlaces);
    
            // Load tour image
            if (tour.getPhoto() != null && !tour.getPhoto().isEmpty()) {
                tourImageView.setImage(new Image("file:" + tour.getPhoto()));
            }
    
            // Populate rating choice box (1 to 5 stars)
            etoileChoiceBox.getItems().addAll(1, 2, 3, 4, 5);
            etoileChoiceBox.setValue(5); // Default rating
    
            // Set up a custom converter to display stars instead of numbers
            etoileChoiceBox.setConverter(new StringConverter<Integer>() {
                @Override
                public String toString(Integer number) {
                    return "★".repeat(number);
                }
    
                @Override
                public Integer fromString(String string) {
                    return string.length(); // Count stars to get the rating
                }
            });
    
            // Load reviews
            loadReviews();
    
            // Load activities
            loadActivities();
        }
    
        private void loadActivities() throws SQLException {
            if (selectedTour == null) {
                System.out.println("No tour selected. Skipping activities.");
                return;
            }
    
            List<Activites> activities = tourService.getActivitiesForTour(selectedTour.getId());
            activitiesListView.getItems().clear();
            activitiesListView.getItems().addAll(activities);
    
            // Customize the ListView to display activities in a beautiful container
            activitiesListView.setCellFactory(param -> new ListCell<Activites>() {
                @Override
                protected void updateItem(Activites activity, boolean empty) {
                    super.updateItem(activity, empty);
                    if (empty || activity == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Create a card-like structure
                        Label nameLabel = new Label("📌 " + activity.getNomActivite());
                        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #012e41;");
    
                        Label dateLabel = new Label("📅 " + activity.getDateDebut() + " - " + activity.getDateFin());
                        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #345867;");
    
                        Label locationLabel = new Label("📍 " + activity.getLocalisation());
                        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #345867;");
    
                        Label descriptionLabel = new Label(activity.getDescription());
                        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                        descriptionLabel.setWrapText(true);
    
                        ImageView activityImageView = new ImageView();
                        if (activity.getPhoto() != null && !activity.getPhoto().isEmpty()) {
                            activityImageView.setImage(new Image("file:" + activity.getPhoto()));
                            activityImageView.setFitHeight(100);
                            activityImageView.setFitWidth(150);
                            activityImageView.setPreserveRatio(true);
                        }
    
                        VBox activityBox = new VBox(nameLabel, dateLabel, locationLabel, descriptionLabel, activityImageView);
                        activityBox.setStyle("-fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-color: #dddddd; -fx-border-radius: 8px;");
                        activityBox.setSpacing(5);
    
                        setGraphic(activityBox);
                    }
                }
            });
        }
    
        @FXML
        private void handleSubmitReview() throws SQLException {
            if (selectedTour == null) {
                showAlert("Error", "No tour selected!", Alert.AlertType.ERROR);
                return;
            }
    
            if (currentUser == null) {
                showAlert("Error", "You must be logged in to submit a review.", Alert.AlertType.ERROR);
                return;
            }
    
            String commentaire = commentaireTextArea.getText().trim();
            int etoile = etoileChoiceBox.getValue();
    
            if (commentaire.isEmpty()) {
                showAlert("Error", "Please enter a review.", Alert.AlertType.WARNING);
                return;
            }
    
            // Create new review using the current user's ID
            AvisTour avis = new AvisTour(
                    0, // ID will be auto-generated
                    currentUser, // Use the current user
                    selectedTour.getId(),
                    etoile,
                    commentaire
            );
    
            if (avisService.ajouter(avis)) {
                showAlert("Success", "Review submitted successfully!", Alert.AlertType.INFORMATION);
                loadReviews();
                commentaireTextArea.clear();
                etoileChoiceBox.setValue(5);
            } else {
                showAlert("Error", "Failed to submit review.", Alert.AlertType.ERROR);
            }
        }
    
        private void loadReviews() throws SQLException {
            if (selectedTour == null || currentUser == null) {
                System.out.println("No tour selected or user not logged in. Skipping reviews.");
                return;
            }
    
            List<AvisTour> avisList = avisService.afficher();
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
                        // Create a visually appealing star rating display
                        HBox starsBox = createStarRating(avis.getEtoile());
                        starsBox.setSpacing(2);

                        // User information
                        Label userLabel = new Label(avis.getUser().getFirstname());
                        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #012e41;");

                        // Comment text
                        Label commentLabel = new Label(avis.getCommentaire());
                        commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                        commentLabel.setWrapText(true);

                        VBox reviewBox = new VBox(userLabel, starsBox, commentLabel);
                        reviewBox.setStyle("-fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-color: #dddddd; " +
                                "-fx-border-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                        reviewBox.setSpacing(5);

                        setGraphic(reviewBox);
                    }
                }
            });
        }
    
        // Helper method to create a visual star rating display
        private HBox createStarRating(int rating) {
            HBox starsBox = new HBox();
            starsBox.setAlignment(Pos.CENTER_LEFT);
    
            // Add filled stars based on rating
            for (int i = 0; i < rating; i++) {
                Label filledStar = new Label("★");
                filledStar.setStyle("-fx-font-size: 20px; -fx-text-fill: #f39c12;");
                starsBox.getChildren().add(filledStar);
            }
    
            // Add empty stars for remaining spots
            for (int i = rating; i < 5; i++) {
                Label emptyStar = new Label("☆");
                emptyStar.setStyle("-fx-font-size: 20px; -fx-text-fill: #f39c12;");
                starsBox.getChildren().add(emptyStar);
            }
    
            return starsBox;
        }
    
        @FXML
        private void handleReservePlace() {
            try {
                if (currentUser == null) {
                    showAlert("Error", "You must be logged in to reserve a place.", Alert.AlertType.ERROR);
                    return;
                }
    
                int availablePlaces = selectedTour.getNbPlaceDisponible() - selectedTour.getNbPlaceReserver();
                if (availablePlaces <= 0) {
                    showAlert("Error", "No available places left!", Alert.AlertType.ERROR);
                    return;
                }
    
                // Create a new reservation using the current user's ID and the selected tour
                ReservationTour reservation = new ReservationTour(
                        0, // ID will be auto-generated by the database
                        currentUser, // Pass the entire User object
                        selectedTour, // Pass the entire Tour object
                        "Pending", // Default status
                        new Date(System.currentTimeMillis()) // Current date
                );
    
                // Add the reservation to the database
                reservationService.addReservation(reservation);
    
                // Update the number of reserved places
                selectedTour.setNbPlaceReserver(selectedTour.getNbPlaceReserver() + 1);
    
                // Refresh the available places label
                availablePlacesLabel.setText("Available Places: " + (selectedTour.getNbPlaceDisponible() - selectedTour.getNbPlaceReserver()));
    
                showAlert("Success", "Reservation placed successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to place reservation.", Alert.AlertType.ERROR);
                e.printStackTrace();
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
        private void handleIconButtonAction() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tours_view.fxml"));
                Parent root = loader.load();

                // Pass the current user to the ToursViewController
                ToursViewController toursViewController = loader.getController();
                toursViewController.setCurrentUser(currentUser);

                Stage stage = (Stage) tourImageView.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Tours View");
            } catch (Exception e) {
                showAlert("Error", "Failed to load Tours View.", Alert.AlertType.ERROR);
            }
        }

        @FXML
        public void handleUpdateReview() {
            AvisTour selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                // Check if the current user is the owner of the review
                if (selectedReview.getUser().getId() == currentUser.getId()) {
                    // Load the selected review into the text area and choice box for editing
                    commentaireTextArea.setText(selectedReview.getCommentaire());
                    etoileChoiceBox.setValue(selectedReview.getEtoile());
                } else {
                    showAlert("Error", "You can only update your own reviews.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Error", "No review selected!", Alert.AlertType.WARNING);
            }
        }
    
        @FXML
        public void handleDeleteReview() throws SQLException {
            AvisTour selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                // Check if the current user is the owner of the review
                if (selectedReview.getUser().getId() == currentUser.getId()) {
                    // Pass the entire AvisTour object, not just the ID
                    boolean success = avisService.supprimer(selectedReview);
                    if (success) {
                        showAlert("Success", "Review deleted successfully!", Alert.AlertType.INFORMATION);
                        loadReviews(); // Reload the reviews after deletion
                    } else {
                        showAlert("Error", "Failed to delete the review.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Error", "You can only delete your own reviews.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Error", "No review selected!", Alert.AlertType.WARNING);
            }
        }
    }