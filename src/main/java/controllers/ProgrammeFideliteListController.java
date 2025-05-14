package controllers;

import models.ProgrammeFidelite;
import models.User;
import services.ProgrammeFideliteService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ProgrammeFideliteListController {
    @FXML
    private FlowPane programmeContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label userPointsLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private VBox emptyStateBox;

    private User currentUser;
    private ProgrammeFideliteService programmeService;
    private ObservableList<ProgrammeFidelite> allProgrammes;
    private FilteredList<ProgrammeFidelite> filteredProgrammes;

    public ProgrammeFideliteListController() {
        programmeService = new ProgrammeFideliteService();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Update user points display
        if (userPointsLabel != null && user != null) {
            userPointsLabel.setText("Vos points: " + user.getPointsfid());
        }

        // Reload programmes after setting user
        if (programmeContainer != null) {
            loadProgrammes();
        }
    }

    @FXML
    public void initialize() {
        // Set up filter options
        filterComboBox.setItems(FXCollections.observableArrayList(
                "Tous les programmes",
                "Points croissant",
                "Points décroissant",
                "Programmes disponibles"
        ));
        filterComboBox.setValue("Tous les programmes");

        // Set up search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProgrammes();
        });

        // Set up filter functionality
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterProgrammes();
        });
    }

    private void loadProgrammes() {
        // Clear any existing cards
        programmeContainer.getChildren().clear();

        // Fetch all programmes
        List<ProgrammeFidelite> programmes = programmeService.getAllProgrammes();
        allProgrammes = FXCollections.observableArrayList(programmes);
        filteredProgrammes = new FilteredList<>(allProgrammes, p -> true);

        // Check if there are any programmes
        if (programmes.isEmpty()) {
            emptyStateBox.setVisible(true);
        } else {
            emptyStateBox.setVisible(false);
            displayProgrammes(filteredProgrammes);
        }
    }

    private void displayProgrammes(List<ProgrammeFidelite> programmes) {
        // Clear existing cards
        programmeContainer.getChildren().clear();

        // Display filtered programmes
        for (ProgrammeFidelite programme : programmes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProgrammeFideliteCard.fxml"));
                Parent cardView = loader.load();

                ProgrammeFideliteCardController cardController = loader.getController();
                cardController.setProgramme(programme);
                cardController.setCurrentUser(currentUser);

                // Add animation effect
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), cardView);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                programmeContainer.getChildren().add(cardView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Show empty state if needed
        emptyStateBox.setVisible(programmes.isEmpty());
    }

    private void filterProgrammes() {
        String searchText = searchField.getText().toLowerCase();
        String filterOption = filterComboBox.getValue();

        filteredProgrammes.setPredicate(programme -> {
            // Apply search filter
            boolean matchesSearch = programme.getNomProgramme().toLowerCase().contains(searchText);

            // Filter by availability if needed
            boolean isAvailable = true;
            if (filterOption.equals("Programmes disponibles") && currentUser != null) {
                isAvailable = currentUser.getPointsfid() >= programme.getPoints();
            }

            return matchesSearch && isAvailable;
        });

        // Apply sorting
        List<ProgrammeFidelite> sortedList = new java.util.ArrayList<>(filteredProgrammes);

        if (filterOption.equals("Points croissant")) {
            sortedList.sort((p1, p2) -> Integer.compare(p1.getPoints(), p2.getPoints()));
        } else if (filterOption.equals("Points décroissant")) {
            sortedList.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
        }

        // Display the filtered and sorted programmes
        displayProgrammes(sortedList);
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);

            // Create fade transition for smooth navigation
            Stage stage = (Stage) programmeContainer.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.3);
            fadeOut.setOnFinished(e -> {
                stage.setScene(new Scene(root));
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), root);
                fadeIn.setFromValue(0.3);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshProgrammes() {
        loadProgrammes();
    }
}