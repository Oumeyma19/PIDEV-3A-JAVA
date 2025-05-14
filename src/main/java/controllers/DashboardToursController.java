package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Tour;
import models.User;
import services.TourService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardToursController {

    @FXML private TableView<Tour> toursTable;
    @FXML private TableColumn<Tour, Integer> idColumn;
    @FXML private TableColumn<Tour, String> titleColumn;
    @FXML private TableColumn<Tour, String> descriptionColumn;
    @FXML private TableColumn<Tour, Double> priceColumn;
    @FXML private TableColumn<Tour, String> locationColumn;
    @FXML private TableColumn<Tour, String> dateColumn;
    @FXML private TableColumn<Tour, Integer> nbPlaceDisponibleColumn;
    @FXML private TableColumn<Tour, Integer> nbPlaceReserverColumn;

    @FXML private TextField searchField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private DatePicker dateField;
    @FXML private TextField nbPlaceDisponibleField;
    @FXML private ComboBox<String> typeComboBox;

    // New fields for update functionality
    @FXML private TextField updateTitleField;
    @FXML private TextField updateDescriptionField;
    @FXML private TextField updatePriceField;
    @FXML private TextField updateLocationField;
    @FXML private DatePicker updateDateField;
    @FXML private TextField updateNbPlaceDisponibleField;
    @FXML private ComboBox<String> updateTypeComboBox;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    @FXML private Button ajouterButton;
    @FXML private Button reservationsButton;
    @FXML private Button avisButton;

    private User currentUser;
    private final TourService tourService = new TourService();
    private String selectedImagePath = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        // Initialize the ComboBox
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Touristique", "Académique", "Religieux", "Esthétique");
            typeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                filterToursByType(newVal); // Filter tours when a type is selected
            });
        } else {
            System.out.println("typeComboBox is null!"); // Debugging statement
        }

        // Initialize the update ComboBox
        if (updateTypeComboBox != null) {
            updateTypeComboBox.getItems().addAll("Touristique", "Académique", "Religieux", "Esthétique");
        }

        // Initialize other components
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        nbPlaceDisponibleColumn.setCellValueFactory(new PropertyValueFactory<>("nbPlaceDisponible"));
        nbPlaceReserverColumn.setCellValueFactory(new PropertyValueFactory<>("nbPlaceReserver"));

        // Add listener to the TableView to populate update fields when a tour is selected
        toursTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateUpdateFields(newSelection);
            }
        });

        loadTours();
    }
    private void populateUpdateFields(Tour tour) {
        updateTitleField.setText(tour.getTitle());
        updateDescriptionField.setText(tour.getDescription());
        updatePriceField.setText(String.valueOf(tour.getPrice()));
        updateLocationField.setText(tour.getLocation());
        updateDateField.setValue(java.time.LocalDate.parse(tour.getDate()));
        updateNbPlaceDisponibleField.setText(String.valueOf(tour.getNbPlaceDisponible()));
        updateTypeComboBox.setValue(tour.getType().name());
    }

    @FXML
    private void handleUpdateTour() {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            try {
                // Update the selected tour with the new values
                selectedTour.setTitle(updateTitleField.getText());
                selectedTour.setDescription(updateDescriptionField.getText());
                selectedTour.setPrice(Double.parseDouble(updatePriceField.getText()));
                selectedTour.setLocation(updateLocationField.getText());
                selectedTour.setDate(updateDateField.getValue().toString());
                selectedTour.setNbPlaceDisponible(Integer.parseInt(updateNbPlaceDisponibleField.getText()));
                selectedTour.setType(Tour.TourType.valueOf(updateTypeComboBox.getValue()));

                if (tourService.modifier(selectedTour)) {
                    showAlert("Success", "Tour updated successfully!", Alert.AlertType.INFORMATION);
                    loadTours(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to update tour.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error", "Invalid input. Please check all fields.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "Please select a tour to update.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteTour() {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le tour");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce tour ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (tourService.supprimer(selectedTour)) {
                    showAlert("Success", "Tour deleted successfully!", Alert.AlertType.INFORMATION);
                    loadTours(); // Refresh the table
                } else {
                    showAlert("Error", "Failed to delete tour.", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Error", "Please select a tour to delete.", Alert.AlertType.ERROR);
        }
    }

    private void filterToursByType(String type) {
        try {
            List<Tour> allTours = tourService.afficher();
            List<Tour> filteredTours;

            if (type == null || type.isEmpty()) {
                // If no type is selected, show all tours
                filteredTours = allTours;
            } else {
                // Convert the selected type to TourType enum
                Tour.TourType tourType = Tour.TourType.valueOf(type);

                // Filter tours by the selected type
                filteredTours = allTours.stream()
                        .filter(tour -> tourType.equals(tour.getType()))
                        .collect(Collectors.toList());
            }

            // Update the TableView with the filtered tours
            toursTable.getItems().setAll(filteredTours);
        } catch (SQLException e) {
            showAlert("Error", "Failed to filter tours.", Alert.AlertType.ERROR);
        }
    }

    private void loadTours() {
        try {
            List<Tour> tours = tourService.afficher();
            toursTable.getItems().setAll(tours);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load tours from the database.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAjouterTour() {
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String location = locationField.getText();
            String date = (dateField.getValue() != null) ? dateField.getValue().toString() : null;
            int nbPlaceDisponible = Integer.parseInt(nbPlaceDisponibleField.getText());
            String type = typeComboBox.getValue();
            int guideId = currentUser.getId();

            if (selectedImagePath == null || selectedImagePath.trim().isEmpty()) {
                showAlert("Error", "Please select an image for the tour!", Alert.AlertType.ERROR);
                return;
            }

            Tour newTour = new Tour(title, description, price, location, date, guideId, nbPlaceDisponible, 0, selectedImagePath, type);
            if (tourService.ajouter(newTour)) {
                showAlert("Success", "Tour added successfully!", Alert.AlertType.INFORMATION);
                loadTours();
            } else {
                showAlert("Error", "Failed to add tour.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input. Please check all fields.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleModifierTour(ActionEvent event) {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            // Open an edit dialog window (You need to create a TourEditController)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_tour.fxml"));
            try {
                Parent root = loader.load();
                UpdateTourController editController = loader.getController();
                editController.setTourData(selectedTour);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Tour");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Refresh the table after modification
                refreshTourList();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un tour à modifier.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSupprimerTour(ActionEvent event) throws SQLException {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le tour");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce tour ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                TourService tourService = new TourService();
                tourService.supprimer(selectedTour);

                // Refresh the table after deletion
                refreshTourList();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un tour à supprimer.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVoirReservations() {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert("Error", "Please select a tour to view reservations.", Alert.AlertType.ERROR);
            return;
        }

        // Navigate to the reservations view for the selected tour
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservations_view.fxml"));
            Parent root = loader.load();

            // Pass the selected tour to the ReservationsController
            ReservationsTourController reservationsController = loader.getController();
            reservationsController.setTour(selectedTour);

            Stage stage = (Stage) reservationsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reservations view.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVoirAvis() {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert("Error", "Please select a tour to view reviews.", Alert.AlertType.ERROR);
            return;
        }

        // Navigate to the reviews view for the selected tour
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/avis_view.fxml"));
            Parent root = loader.load();

            // Pass the selected tour to the AvisController
            AvisTourController avisController = loader.getController();
            avisController.setTour(selectedTour);

            Stage stage = (Stage) avisButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reviews view.", Alert.AlertType.ERROR);
        }
    }

    private void refreshTourList() throws SQLException {
        TourService tourService = new TourService();
        List<Tour> tours = tourService.afficher();
        toursTable.getItems().setAll(tours);
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
