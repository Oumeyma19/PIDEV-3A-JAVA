package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Tour;
import models.User;
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
import util.Type;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ToursViewController {

    @FXML
    private FlowPane toursFlowPane;
    @FXML
    private Button profileButton;

    @FXML
    private Button addTourButton;

    @FXML
    private TextField searchField;
    private User currentUser;

    @FXML
    private Button tourButton;

    private final TourService tourService = new TourService();

    public void setCurrentUser(User user) throws SQLException {
        this.currentUser = user;

        // Debug: Print the current user's role
        System.out.println("Current User Role: " + (currentUser != null ? currentUser.getRoles() : "null"));

        // Set the visibility of the "Add Tour" button based on the user's role
        if (currentUser != null && Type.GUIDE.equals(currentUser.getRoles())) {
            addTourButton.setVisible(true);
        } else {
            addTourButton.setVisible(false);
        }
        // Set the visibility of the "Add Tour" button based on the user's role
        if (currentUser != null && Type.GUIDE.equals(currentUser.getRoles())) {
            addTourButton.setVisible(true);
        } else {
            addTourButton.setVisible(false);
        }


        refreshTours(); // Refresh tours after setting the current user
    }
    @FXML
    public void initialize() throws SQLException {
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleProfile());
        }

        if (tourButton != null) {
            tourButton.setOnAction(event -> handleToursButtonClick());
        }

        /// Add listener to the search field for dynamic search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(); // Trigger search automatically as the user types
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Set action for the "Ajouter Tour" button
        addTourButton.setOnAction(event -> handleAddTourButtonClick());
    }
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ProfilController
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() throws SQLException {
        String searchText = searchField.getText().trim().toLowerCase(); // Get the search text and convert to lowercase
        List<Tour> allTours = tourService.afficher(); // Fetch all tours from the database

        if (searchText.isEmpty()) {
            // If the search field is empty, display all tours
            displayTours(allTours);
        } else {
            // Filter tours based on the search text
            List<Tour> filteredTours = allTours.stream()
                    .filter(tour -> tour.getLocation().toLowerCase().contains(searchText) || // Search by location
                            tour.getTitle().toLowerCase().contains(searchText) ||    // Search by title
                            tour.getDescription().toLowerCase().contains(searchText)) // Search by description
                    .toList();

            displayTours(filteredTours); // Display the filtered tours
        }
    }

    private void displayTours(List<Tour> tours) {
        toursFlowPane.getChildren().clear();
        tours.forEach(tour -> toursFlowPane.getChildren().add(createTourContainer(tour)));
    }

    private void refreshTours() throws SQLException {
        List<Tour> tours = tourService.afficher();
        displayTours(tours);
    }

    private VBox createTourContainer(Tour tour) {

        VBox tourContainer = new VBox(10);
        tourContainer.setPadding(new Insets(10));
        tourContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); -fx-border-color: #ddd; -fx-border-radius: 5;");
        tourContainer.setMaxWidth(300);
        tourContainer.setMinWidth(300);

        // Add the availability container above the photo
        HBox availabilityContainer = new HBox();
        availabilityContainer.setAlignment(Pos.CENTER);
        availabilityContainer.setPadding(new Insets(5));
        availabilityContainer.setPrefHeight(30);

        // Check if the tour is full
        if (tour.getNbPlaceDisponible() == tour.getNbPlaceReserver()) {
            availabilityContainer.setStyle("-fx-background-color: #FF3B30; -fx-background-radius: 5;"); // Red background
            Text availabilityText = new Text("Full");
            availabilityText.setStyle("-fx-fill: white; -fx-font-weight: bold;");
            availabilityContainer.getChildren().add(availabilityText);
        } else {
            availabilityContainer.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5;"); // Green background
            Text availabilityText = new Text("Available");
            availabilityText.setStyle("-fx-fill: white; -fx-font-weight: bold;");
            availabilityContainer.getChildren().add(availabilityText);
        }

        // Add the availability container to the tour container
        tourContainer.getChildren().add(availabilityContainer);

        // Add the tour photo
        if (tour.getPhoto() != null && !tour.getPhoto().isEmpty()) {
            ImageView imageView = new ImageView(new Image("file:" + tour.getPhoto()));
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            tourContainer.getChildren().add(imageView);
        }

        // Add the location container
        HBox tourLocationContainer = new HBox(10);
        tourLocationContainer.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image("file:logo/local.svg"));
        logo.setFitHeight(20);
        logo.setPreserveRatio(true);
        tourLocationContainer.getChildren().addAll(logo, new Text(tour.getLocation()));

        // Add the tour name
        Text tourName = new Text(tour.getTitle());
        tourName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Add the price and buttons
        HBox priceAndButtons = new HBox(10);
        priceAndButtons.setPadding(new Insets(5, 0, 5, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);
        Text priceText = new Text("$" + tour.getPrice());
        priceText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Button consultButton = new Button("Consult");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> openTourDetails(tour));

        // Only show Update and Delete buttons if the current user is the creator of the tour
        if (currentUser != null && currentUser.getId() == tour.getGuideId()) {
            Button updateButton = new Button("Update");
            updateButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white; -fx-font-weight: bold;");
            updateButton.setOnAction(event -> updateTour(tour));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-weight: bold;");
            deleteButton.setOnAction(event -> deleteTour(tour, tourContainer));

            priceAndButtons.getChildren().addAll(priceText, consultButton, updateButton, deleteButton);
        } else {
            // Only show the Consult button if the user is not the creator
            priceAndButtons.getChildren().addAll(priceText, consultButton);
        }

        tourContainer.getChildren().addAll(tourLocationContainer, tourName, priceAndButtons);

        return tourContainer;
    }

    private void openTourDetails(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tour_details.fxml"));
            Parent root = loader.load();

            // Pass the tour data and current user to the TourDetailsController
            TourDetailsController detailsController = loader.getController();
            detailsController.setTourData(tour);
            detailsController.setCurrentUser(currentUser); // Pass the current user

            // Set the scene and show the stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tour Details");
            stage.show();
        } catch (IOException | SQLException e) {
            showAlert("Error", "Failed to load tour details.", Alert.AlertType.ERROR);
        }
    }

    private void deleteTour(Tour tour, VBox tourContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this tour?");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                boolean success = tourService.supprimer(tour);
                if (success) {
                    toursFlowPane.getChildren().remove(tourContainer);
                    showAlert("Success", "Tour deleted successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to delete the tour.", Alert.AlertType.ERROR);
                }
            }
        });
    }


    private void updateTour(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_tour.fxml"));
            Parent root = loader.load();
            UpdateTourController updateTourController = loader.getController();
            updateTourController.setTourData(tour);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Tour");
            stage.setOnHidden(event -> {
                try {
                    refreshTours();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load update form.", Alert.AlertType.ERROR);
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
    private void handleToursButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTourView.fxml"));
            Parent root = loader.load();

            TourController tourController = loader.getController();
            tourController.setCurrentUser(currentUser);

            Stage stage = (Stage) tourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddTourButtonClick() {
        try {
            // Load the AddTourView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddTourView.fxml"));
            Parent root = loader.load();

            // Get the controller for AddTourView
            TourController addTourController = loader.getController();
            addTourController.setCurrentUser(currentUser); // Pass the current user information

            // Set the scene and show the stage
            Stage stage = (Stage) addTourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Tour");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
