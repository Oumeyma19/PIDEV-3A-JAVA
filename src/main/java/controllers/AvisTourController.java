package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.AvisTour;
import models.Tour;
import models.User;
import services.AvisTourService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AvisTourController {

    @FXML private TableView<AvisTour> avisTable;
    @FXML private TableColumn<AvisTour, Integer> idColumn;
    @FXML private TableColumn<AvisTour, Integer> clientIdColumn;
    @FXML private TableColumn<AvisTour, Integer> etoileColumn;
    @FXML private TableColumn<AvisTour, String> commentaireColumn;

    @FXML private TextField updateEtoileField;
    @FXML private TextField updateCommentaireField;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button goBackButton;

    private Tour selectedTour;
    private User currentUser; // Add currentUser field
    private AvisTourService avisService = new AvisTourService();

    public void setTour(Tour tour) {
        this.selectedTour = tour;
        loadAvis();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user; // Set currentUser
    }

    @FXML
    public void initialize() {
        // Initialize TableView columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        etoileColumn.setCellValueFactory(new PropertyValueFactory<>("etoile"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        // Add listener to the TableView to populate update fields when an avis is selected
        avisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateUpdateFields(newSelection);
            }
        });
    }

    private void populateUpdateFields(AvisTour avis) {
        updateEtoileField.setText(String.valueOf(avis.getEtoile()));
        updateCommentaireField.setText(avis.getCommentaire());
    }

    private void loadAvis() {
        if (selectedTour == null) return;

        try {
            List<AvisTour> avisList = avisService.getAvisByTourId(selectedTour.getId());
            avisTable.getItems().clear();
            avisTable.getItems().addAll(avisList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateAvis() {
        AvisTour selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            try {
                // Update the selected avis with the new values
                selectedAvis.setEtoile(Integer.parseInt(updateEtoileField.getText()));
                selectedAvis.setCommentaire(updateCommentaireField.getText());

                if (avisService.modifier(selectedAvis)) {
                    showAlert("Success", "Avis updated successfully!", Alert.AlertType.INFORMATION);
                    loadAvis(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to update avis.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error", "Invalid input. Please check all fields.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "Please select an avis to update.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteAvis() {
        AvisTour selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'avis");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cet avis ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    if (avisService.supprimer(selectedAvis)) {
                        showAlert("Success", "Avis deleted successfully!", Alert.AlertType.INFORMATION);
                        loadAvis(); // Refresh the table
                    } else {
                        showAlert("Error", "Failed to delete avis.", Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("Error", "Please select an avis to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGoBack() {
        try {
            // Load the Dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard_tours_view.fxml"));
            Parent root = loader.load();

            // Pass the currentUser to the DashboardController
            DashboardToursController dashboardController = loader.getController();
            dashboardController.setCurrentUser(currentUser);

            // Switch to the Dashboard view
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Dashboard.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}