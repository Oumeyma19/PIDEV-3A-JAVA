package controllers;

import models.Recompense;
import models.User;
import services.RecompenseService;
import services.UserService;
import exceptions.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RecompenseController {
    @FXML
    private FlowPane recompensesFlowPane;

    @FXML
    private TextField searchField;

    @FXML
    private Label pointsLabel;

    @FXML
    private Label noResultsLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button searchButton;

    private final RecompenseService recompenseService = new RecompenseService();
    private final UserService userService = UserService.getInstance();
    private User currentUser;
    private List<Recompense> allRecompenses;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshUI();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            loadRecompenseCards();
            updatePointsDisplay();
        });
    }

    public void refreshUI() {
        Platform.runLater(() -> {
            loadRecompenseCards();
            updatePointsDisplay();
        });
    }

    private void updatePointsDisplay() {
        if (currentUser != null) {
            pointsLabel.setText("Mes points: " + currentUser.getPointsfid());
        } else {
            pointsLabel.setText("Mes points: 0");
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        filterRecompenses(searchTerm);
    }

    private void filterRecompenses(String searchTerm) {
        if (allRecompenses == null) return;

        recompensesFlowPane.getChildren().clear();

        List<Recompense> filteredRecompenses;

        if (searchTerm.isEmpty()) {
            filteredRecompenses = allRecompenses;
        } else {
            filteredRecompenses = allRecompenses.stream()
                    .filter(r -> r.getNom().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }

        if (filteredRecompenses.isEmpty()) {
            noResultsLabel.setVisible(true);
        } else {
            noResultsLabel.setVisible(false);
            for (Recompense recompense : filteredRecompenses) {
                VBox recompenseContainer = createRecompenseContainer(recompense);
                recompensesFlowPane.getChildren().add(recompenseContainer);
            }
        }
    }

    @FXML
    private void goBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();

            // Transfer the current user to the home controller
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de naviguer vers la page d'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadRecompenseCards() {
        if (recompensesFlowPane == null) return;
        recompensesFlowPane.getChildren().clear();

        try {
            allRecompenses = recompenseService.getAllRecompensesStatus();

            if (allRecompenses.isEmpty()) {
                noResultsLabel.setVisible(true);
                return;
            }

            noResultsLabel.setVisible(false);

            for (Recompense recompense : allRecompenses) {
                VBox recompenseContainer = createRecompenseContainer(recompense);
                recompensesFlowPane.getChildren().add(recompenseContainer);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les récompenses: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createRecompenseContainer(Recompense recompense) {
        // Main container
        VBox recompenseContainer = new VBox();
        recompenseContainer.getStyleClass().add("reward-card");
        recompenseContainer.setMaxWidth(300);
        recompenseContainer.setMinWidth(300);

        // Image container
        VBox imageContainer = new VBox();
        imageContainer.getStyleClass().add("reward-image-container");
        imageContainer.setPrefHeight(180);

        // Display the photo
        if (recompense.getPhoto() != null && !recompense.getPhoto().isEmpty()) {
            File imageFile = new File(recompense.getPhoto());
            Image image = imageFile.exists()
                    ? new Image(imageFile.toURI().toString(), 300, 180, true, true)
                    : new Image(getClass().getResourceAsStream("/views/placeholder.png"), 300, 180, true, true);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(true);
            imageContainer.getChildren().add(imageView);
        } else {
            // Add placeholder if no image
            Image placeholderImage = new Image(getClass().getResourceAsStream("/views/placeholder.png"), 300, 180, true, true);
            ImageView imageView = new ImageView(placeholderImage);
            imageView.setFitWidth(300);
            imageView.setFitHeight(180);
            imageContainer.getChildren().add(imageView);
        }

        // Content container
        VBox contentContainer = new VBox(10);
        contentContainer.getStyleClass().add("reward-content");

        // Display the name
        Label recompenseName = new Label(recompense.getNom());
        recompenseName.getStyleClass().add("reward-title");
        contentContainer.getChildren().add(recompenseName);

        // Display the points required
        Label pointsText = new Label("Points: " + recompense.getPointsRequis());
        pointsText.getStyleClass().add("reward-points");
        contentContainer.getChildren().add(pointsText);

        // Add Claim button
        Button claimButton = new Button("Réclamer");
        claimButton.getStyleClass().add("claim-button");

        // Disable button if user doesn't have enough points
        if (currentUser != null && currentUser.getPointsfid() < recompense.getPointsRequis()) {
            claimButton.setDisable(true);
            claimButton.setText("Points insuffisants");
        }

        claimButton.setOnAction(event -> handleClaimRecompense(recompense));
        contentContainer.getChildren().add(claimButton);

        // Add all components to the main container
        recompenseContainer.getChildren().addAll(imageContainer, contentContainer);

        return recompenseContainer;
    }

    private void handleClaimRecompense(Recompense recompense) {
        if (currentUser == null) {
            showAlert("Erreur", "Vous devez être connecté pour réclamer une récompense.", Alert.AlertType.ERROR);
            return;
        }

        if (currentUser.getPointsfid() < recompense.getPointsRequis()) {
            showAlert("Erreur", "Vous n'avez pas assez de points pour réclamer cette récompense.", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la réclamation");
        alert.setHeaderText("Voulez-vous vraiment réclamer cette récompense ?");
        alert.setContentText("Cela coûtera " + recompense.getPointsRequis() + " points.");

        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                try {
                    // Claim the recompense
                    recompenseService.claimRecompense(recompense.getId(), currentUser.getId(), recompense.getPointsRequis());

                    // Update the user's points
                    currentUser.setPointsfid(currentUser.getPointsfid() - recompense.getPointsRequis());
                    userService.updateUser(currentUser);

                    // Refresh the UI
                    refreshUI();

                    showAlert("Succès", "Récompense réclamée avec succès !", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "Impossible de réclamer la récompense: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}