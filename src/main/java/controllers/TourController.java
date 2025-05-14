package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Activites;
import models.Tour;
import models.User;
import org.json.JSONObject;
import services.TourService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class TourController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField locationField;
    @FXML private DatePicker dateField;
    @FXML private ImageView imageView;
    @FXML private Spinner<Integer> nbPlaceDisponibleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField activityNameField;
    @FXML private DatePicker activityStartDateField;
    @FXML private DatePicker activityEndDateField;
    @FXML private TextField activityLocationField;
    @FXML private TextArea activityDescriptionField;
    @FXML private ImageView activityImageView;
    @FXML private WebView streetViewWebView;

    private User currentUser;
    private final TourService tourService = new TourService();
    private String selectedImagePath = null;
    private String selectedActivityImagePath = null;
    private List<Activites> activities = new ArrayList<>();
    @FXML private ListView<String> activityListView; // ListView to display activities
    private ObservableList<String> activityDescriptions = FXCollections.observableArrayList();
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    private static final String GOOGLE_MAPS_API_KEY = "https://maps.gomaps.pro/maps/api/js?key=AlzaSyGYJ8EowF46cIL5fGxWmv6QDQ5JUtwngsb"; // Replace with your API key
    private static final String OPENTRIPMAP_API_KEY = "5ae2e3f221c38a28845f05b6f8a352f49fed950480afe93f8e3724ad";

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("Touristique", "Académique", "Religieux", "Esthétique");
        activityListView.setItems(activityDescriptions);

        // Initialize the Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 10);
        nbPlaceDisponibleField.setValueFactory(valueFactory);

        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                updateStreetView(newValue);
            }
        });
    }
    private void updateStreetView(String location) {
        // Construct the URL for OpenTripMap API
        String openTripMapUrl = "https://api.opentripmap.com/0.1/en/places/geoname?name=" + location + "&apikey=" + OPENTRIPMAP_API_KEY;

        // Fetch data from OpenTripMap API
        try {
            URL url = new URL(openTripMapUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showAlert("Error", "Failed to fetch location data from OpenTripMap! Response code: " + responseCode, Alert.AlertType.ERROR);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // Print the API response for debugging
            System.out.println("API Response: " + content.toString());

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(content.toString());

            // Check if the location was found
            if (!jsonResponse.has("status") || !jsonResponse.getString("status").equals("OK")) {
                showAlert("Error", "Location not found!", Alert.AlertType.ERROR);
                return;
            }

            // Check if the response contains the expected keys
            if (!jsonResponse.has("lat") || !jsonResponse.has("lon")) {
                showAlert("Error", "Location data not found in the API response!", Alert.AlertType.ERROR);
                return;
            }

            double lat = jsonResponse.getDouble("lat");
            double lon = jsonResponse.getDouble("lon");

            // Display the location on a map (you can use a map library like Leaflet or OpenLayers)
            String mapUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=15/" + lat + "/" + lon;
            streetViewWebView.getEngine().load(mapUrl);

        } catch (Exception e) {
            showAlert("Error", "Failed to fetch location data from OpenTripMap!", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destDir = System.getProperty("user.home") + "/uploads/tour_photos/";
                File destFile = new File(destDir + selectedFile.getName());

                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }

                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imageView.setImage(new Image(destFile.toURI().toString()));
                selectedImagePath = destFile.getAbsolutePath(); // Set the selected image path
            } catch (Exception e) {
                showAlert("Error", "Failed to upload image!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleSelectActivityImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destDir = System.getProperty("user.home") + "/uploads/activity_photos/";
                File destFile = new File(destDir + selectedFile.getName());

                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }

                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                activityImageView.setImage(new Image(destFile.toURI().toString()));
                selectedActivityImagePath = destFile.getAbsolutePath(); // Set the selected activity image path
            } catch (Exception e) {
                showAlert("Error", "Failed to upload image!", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleAddActivity() {
        String name = activityNameField.getText();
        String startDate = (activityStartDateField.getValue() != null) ? activityStartDateField.getValue().toString() : null;
        String endDate = (activityEndDateField.getValue() != null) ? activityEndDateField.getValue().toString() : null;
        String location = activityLocationField.getText();
        String description = activityDescriptionField.getText();
        String photo = selectedActivityImagePath;

        // Validate all fields
        if (name.isEmpty() || startDate == null || endDate == null || location.isEmpty() || description.isEmpty()) {
            showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
            return;
        }

        // Validate that an image is selected
        if (photo == null || photo.trim().isEmpty()) {
            showAlert("Error", "Please select an image for the activity!", Alert.AlertType.ERROR);
            return;
        }

        // Create the activity
        Activites activity = new Activites();
        activity.setNomActivite(name);
        activity.setDateDebut(startDate);
        activity.setDateFin(endDate);
        activity.setLocalisation(location);
        activity.setDescription(description);
        activity.setPhoto(photo);

        // Add the activity to the list
        activities.add(activity);

        // Add the activity description to the ListView
        activityDescriptions.add(description); // You can customize this to show more details

        // Clear the input fields
        activityNameField.clear();
        activityStartDateField.setValue(null);
        activityEndDateField.setValue(null);
        activityLocationField.clear();
        activityDescriptionField.clear();
        activityImageView.setImage(null);
        selectedActivityImagePath = null;

        showAlert("Success", "Activity added successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleAddTour() {
        try {
            // Validate and create the tour
            String title = titleField.getText(); // Use getText() for TextField
            String description = descriptionField.getText(); // Use getText() for TextArea
            String priceStr = priceField.getText(); // Use getText() for TextField
            String location = locationField.getText(); // Use getText() for TextField
            String date = (dateField.getValue() != null) ? dateField.getValue().toString() : null; // Use getValue() for DatePicker
            int nbPlaceDisponible = nbPlaceDisponibleField.getValue(); // Use getValue() for Spinner<Integer>
            String type = typeComboBox.getValue(); // Use getValue() for ComboBox

            // Auto-set guide ID from authenticated user
            String guideIdStr = String.valueOf(currentUser.getId());

            // Validate all fields
            if (title == null || title.trim().isEmpty()) {
                showAlert("Error", "Title cannot be empty!", Alert.AlertType.ERROR);
                return;
            }

            if (description.isEmpty() || priceStr.isEmpty() || location.isEmpty() || date == null || type == null) {
                showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
                return;
            }

            // Validate that an image is selected
            if (selectedImagePath == null || selectedImagePath.trim().isEmpty()) {
                showAlert("Error", "Please select an image for the tour!", Alert.AlertType.ERROR);
                return;
            }

            // Parse price and validate
            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid price format. Please enter a valid number.", Alert.AlertType.ERROR);
                return;
            }

            int guideId = Integer.parseInt(guideIdStr);
            int nbPlaceReserver = 0; // Default value for reserved places

            // Create a new Tour with the selected image path
            Tour newTour = new Tour(
                    title,                // String
                    description,          // String
                    price,                // double
                    location,             // String
                    date,                 // String
                    guideId,              // int
                    nbPlaceDisponible,    // int
                    nbPlaceReserver,      // int
                    selectedImagePath,    // String (photo)
                    type                  // String
            );

            // Set the activities for the tour
            newTour.setActivities(activities);

            // Add the tour and its activities to the database
            boolean isAdded = tourService.ajouter(newTour);

            if (isAdded) {
                showAlert("Success", "Tour and activities added successfully!", Alert.AlertType.INFORMATION);
                navigateToToursView();
            } else {
                showAlert("Error", "Failed to add tour. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid number format. Check price and places fields!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBackButtonClick() {
        navigateToToursView();
    }

    private void navigateToToursView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tours_view.fxml"));
            Parent root = loader.load();

            // Pass the current user to the ToursViewController
            ToursViewController toursViewController = loader.getController();
            toursViewController.setCurrentUser(currentUser);

            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tours View");
        } catch (Exception e) {
            showAlert("Error", "Failed to load Tours View.", Alert.AlertType.ERROR);
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
    private void showStreetView(String location) {
        String apiKey = "YOUR_GOOGLE_MAPS_API_KEY";
        String streetViewUrl = "https://www.google.com/maps/embed/v1/streetview?key=" + apiKey + "&location=" + location + "&heading=210&pitch=10&fov=90";
        streetViewWebView.getEngine().load(streetViewUrl);
    }
}