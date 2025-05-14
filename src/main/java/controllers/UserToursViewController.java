package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Tour;
import models.User;
import services.SessionManager;
import services.TourService;
import util.Type;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserToursViewController {

    @FXML
    private FlowPane toursFlowPane;
    @FXML
    private Button profileButton;
    @FXML
    private Button newTourButton;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private Button resetFilterButton;
    @FXML
    private Button applyFilterButton;

    @FXML
    public void initialize() {
        // Set up profile button click handler
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleBackToProfile());
        }

        // Set up new tour button click handler
        if (newTourButton != null) {
            newTourButton.setOnAction(event -> handleAddTourButtonClick());
        }

        // Set up filter button click handlers
        if (applyFilterButton != null) {
            applyFilterButton.setOnAction(event -> applyFilters());
        }

        if (resetFilterButton != null) {
            resetFilterButton.setOnAction(event -> resetFilters());
        }
    }

    private User currentUser;
    private final TourService tourService = new TourService();

    public void setCurrentUser(User user) throws SQLException {
        currentUser = SessionManager.getCurrentUser();

        // Set the visibility of the "Add Tour" button ONLY for GUIDE role
        if (currentUser != null && Type.GUIDE.equals(currentUser.getRoles())) {
            newTourButton.setVisible(true);
        } else {
            newTourButton.setVisible(false);
        }

        refreshTours(); // Refresh tours after setting the current user
    }

    private void refreshTours() throws SQLException {
        List<Tour> tours = tourService.getToursByGuideId(currentUser.getId());
        displayTours(tours);
    }

    private void displayTours(List<Tour> tours) {
        toursFlowPane.getChildren().clear();
        tours.forEach(tour -> toursFlowPane.getChildren().add(createTourContainer(tour)));
    }

    private VBox createTourContainer(Tour tour) {
        VBox tourContainer = new VBox(10);
        tourContainer.setPadding(new Insets(10));
        tourContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 5); -fx-border-color: #eaeaea; -fx-border-radius: 8;");
        tourContainer.setMaxWidth(300);
        tourContainer.setMinWidth(300);

        // Add photo if available
        try {
            java.lang.reflect.Field photoField = tour.getClass().getDeclaredField("photo");
            photoField.setAccessible(true);
            String photoPath = (String) photoField.get(tour);

            if (photoPath != null && !photoPath.isEmpty()) {
                try {
                    ImageView imageView = new ImageView(new Image("file:" + photoPath));
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(180);
                    imageView.setStyle("-fx-background-radius: 8 8 0 0;");
                    tourContainer.getChildren().add(imageView);
                } catch (Exception e) {
                    // If image loading fails, continue without adding an image
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Photo field doesn't exist, continue without adding an image
        }

        // Container for tour details
        VBox detailsContainer = new VBox(8);
        detailsContainer.setPadding(new Insets(10, 10, 5, 10));

        // Title with elegant styling
        Label titleLabel = new Label(tour.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Location with icon
        HBox locationBox = new HBox(8);
        locationBox.setAlignment(Pos.CENTER_LEFT);

        // Try to load location icon
        try {
            ImageView locationIcon = new ImageView(new Image("file:logo/local.svg"));
            locationIcon.setFitHeight(16);
            locationIcon.setFitWidth(16);
            locationBox.getChildren().add(locationIcon);
        } catch (Exception e) {
            // If icon loading fails, use a text prefix
            Label locationPrefix = new Label("📍");
            locationBox.getChildren().add(locationPrefix);
        }

        Label locationLabel = new Label(tour.getLocation());
        locationLabel.setStyle("-fx-text-fill: #666666;");
        locationBox.getChildren().add(locationLabel);

        // Price with styling
        Label priceLabel = new Label("€" + tour.getPrice());
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3A86FF;");

        // Separator line
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #eeeeee;");

        // Add details to the container
        detailsContainer.getChildren().addAll(titleLabel, locationBox, priceLabel, separator);
        tourContainer.getChildren().add(detailsContainer);

        // Button container
        HBox buttonContainer = new HBox(8);
        buttonContainer.setPadding(new Insets(0, 10, 5, 10));
        buttonContainer.setAlignment(Pos.CENTER);

        // View Activities Button with icon styling
        Button viewActivitiesButton = new Button("Voir les Activités");
        viewActivitiesButton.setMaxWidth(Double.MAX_VALUE);
        viewActivitiesButton.setPrefHeight(35);
        viewActivitiesButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        viewActivitiesButton.setOnAction(event -> viewActivities(tour));
        HBox.setHgrow(viewActivitiesButton, javafx.scene.layout.Priority.ALWAYS);

        // Button container for update and delete
        HBox actionButtonsContainer = new HBox(8);
        actionButtonsContainer.setPadding(new Insets(5, 10, 10, 10));
        actionButtonsContainer.setAlignment(Pos.CENTER);

        // Update Button
        Button updateButton = new Button("Mettre à Jour");
        updateButton.setPrefWidth(140);
        updateButton.setPrefHeight(35);
        updateButton.setStyle("-fx-background-color: #FFB703; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        updateButton.setOnAction(event -> updateTour(tour));
        HBox.setHgrow(updateButton, javafx.scene.layout.Priority.ALWAYS);

        // Delete Button
        Button deleteButton = new Button("Supprimer");
        deleteButton.setPrefWidth(140);
        deleteButton.setPrefHeight(35);
        deleteButton.setStyle("-fx-background-color: #E63946; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        deleteButton.setOnAction(event -> deleteTour(tour, tourContainer));
        HBox.setHgrow(deleteButton, javafx.scene.layout.Priority.ALWAYS);

        buttonContainer.getChildren().add(viewActivitiesButton);
        actionButtonsContainer.getChildren().addAll(updateButton, deleteButton);

        // Add button containers to the tour container
        tourContainer.getChildren().addAll(buttonContainer, actionButtonsContainer);

        // Add hover effect
        tourContainer.setOnMouseEntered(e ->
                tourContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(58, 134, 255, 0.3), 15, 0, 0, 8); -fx-border-color: #3A86FF; -fx-border-radius: 8;")
        );

        tourContainer.setOnMouseExited(e ->
                tourContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 5); -fx-border-color: #eaeaea; -fx-border-radius: 8;")
        );

        return tourContainer;
    }

    private void applyFilters() {
        try {
            double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
            double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

            // Fetch all tours for the current user
            List<Tour> allTours = tourService.getToursByGuideId(currentUser.getId());

            // Filter tours based on the price range
            List<Tour> filteredTours = allTours.stream()
                    .filter(tour -> tour.getPrice() >= minPrice && tour.getPrice() <= maxPrice)
                    .collect(Collectors.toList());

            // Display the filtered tours
            displayTours(filteredTours);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs valides pour les prix.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de la récupération des tours.", Alert.AlertType.ERROR);
        }
    }
    private void resetFilters() {
        minPriceField.clear();
        maxPriceField.clear();

        try {
            refreshTours();
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de la réinitialisation des filtres.", Alert.AlertType.ERROR);
        }
    }

    private void viewActivities(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TourActivitiesView.fxml"));
            Parent root = loader.load();
            TourActivitiesViewController activitiesController = loader.getController();
            activitiesController.setCurrentTour(tour);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Activités du Tour");
            stage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Print the stack trace for more details
            showAlert("Erreur", "Échec du chargement de la vue des activités.", Alert.AlertType.ERROR);
        }
    }

    private void deleteTour(Tour tour, VBox tourContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer ce tour ?");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                boolean success = tourService.supprimer(tour);
                if (success) {
                    toursFlowPane.getChildren().remove(tourContainer);
                    showAlert("Succès", "Tour supprimé avec succès !", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de la suppression du tour.", Alert.AlertType.ERROR);
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
            stage.setTitle("Mettre à Jour le Tour");
            stage.setOnHidden(event -> {
                try {
                    refreshTours();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Échec du chargement du formulaire de mise à jour.", Alert.AlertType.ERROR);
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
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ProfilController
            ProfilController profilController = loader.getController();
            currentUser = SessionManager.getCurrentUser();

            profilController.setCurrentUser(currentUser);

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement de la vue du profil.", Alert.AlertType.ERROR);
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
            Stage stage = (Stage) newTourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Tour");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement de la vue d'ajout de tour.", Alert.AlertType.ERROR);
        }
    }
}