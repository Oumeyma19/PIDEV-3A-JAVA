package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.Activites;
import services.ActivitesService;

public class UpdateActivityController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField photoField;

    @FXML
    private TextField descriptionField;

    private Activites currentActivity;
    private final ActivitesService activitesService = new ActivitesService();

    public void setActivityData(Activites activity) {
        this.currentActivity = activity;
        nameField.setText(activity.getNomActivite());
        startDateField.setText(activity.getDateDebut());
        endDateField.setText(activity.getDateFin());
        locationField.setText(activity.getLocalisation());
        photoField.setText(activity.getPhoto());
        descriptionField.setText(activity.getDescription());
    }

    @FXML
    private void handleUpdateActivity() {
        currentActivity.setNomActivite(nameField.getText());
        currentActivity.setDateDebut(startDateField.getText());
        currentActivity.setDateFin(endDateField.getText());
        currentActivity.setLocalisation(locationField.getText());
        currentActivity.setPhoto(photoField.getText());
        currentActivity.setDescription(descriptionField.getText());

        boolean success = activitesService.modifier(currentActivity);
        if (success) {
            nameField.getScene().getWindow().hide(); // Close the window
        }
    }
}