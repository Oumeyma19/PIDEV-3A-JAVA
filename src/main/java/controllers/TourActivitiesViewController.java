package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Activites;
import models.Tour;
import services.ActivitesService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TourActivitiesViewController {

    @FXML
    private Label tourTitleLabel;

    @FXML
    private FlowPane activitiesFlowPane;

    private Tour currentTour;
    private final ActivitesService activitesService = new ActivitesService();

    public void setCurrentTour(Tour tour) throws SQLException {
        this.currentTour = tour;
        tourTitleLabel.setText("Activities for Tour: " + tour.getTitle());
        refreshActivities();
    }

    private void refreshActivities() throws SQLException {
        List<Activites> activities = activitesService.getActivitiesForTour(currentTour.getId());
        displayActivities(activities);
    }

    private void displayActivities(List<Activites> activities) {
        activitiesFlowPane.getChildren().clear();
        activities.forEach(activity -> activitiesFlowPane.getChildren().add(createActivityContainer(activity)));
    }

    private VBox createActivityContainer(Activites activity) {
        VBox activityContainer = new VBox(10);
        activityContainer.setPadding(new Insets(10));
        activityContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); -fx-border-color: #ddd; -fx-border-radius: 5;");
        activityContainer.setMaxWidth(300);
        activityContainer.setMinWidth(300);

        // Add activity details to the container
        Label nameLabel = new Label("Name: " + activity.getNomActivite());
        Label startDateLabel = new Label("Start Date: " + activity.getDateDebut());
        Label endDateLabel = new Label("End Date: " + activity.getDateFin());
        Label locationLabel = new Label("Location: " + activity.getLocalisation());

        // Add Update and Delete buttons
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(event -> updateActivity(activity));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(event -> deleteActivity(activity, activityContainer));

        // Add all elements to the activity container
        activityContainer.getChildren().addAll(nameLabel, startDateLabel, endDateLabel, locationLabel, updateButton, deleteButton);

        return activityContainer;
    }

    @FXML
    private void handleAddActivity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddActivityView.fxml"));
            Parent root = loader.load();
            AddActivityController addActivityController = loader.getController();
            addActivityController.setCurrentTour(currentTour);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Activity");
            stage.setOnHidden(event -> {
                try {
                    refreshActivities();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load add activity form.", Alert.AlertType.ERROR);
        }
    }

    private void updateActivity(Activites activity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateActivityView.fxml"));
            Parent root = loader.load();
            UpdateActivityController updateActivityController = loader.getController();
            updateActivityController.setActivityData(activity);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Activity");
            stage.setOnHidden(event -> {
                try {
                    refreshActivities();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load update form.", Alert.AlertType.ERROR);
        }
    }

    private void deleteActivity(Activites activity, VBox activityContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this activity?");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                boolean success = activitesService.supprimer(activity);
                if (success) {
                    activitiesFlowPane.getChildren().remove(activityContainer);
                    showAlert("Success", "Activity deleted successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to delete the activity.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}