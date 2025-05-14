package controllers;

import models.ProgrammeFidelite;
import models.User;
import services.ProgrammeFideliteService;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ProgrammeFideliteCardController {
    @FXML
    private Label nomProgrammeLabel;

    @FXML
    private Label pointsLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Button acheterButton;

    @FXML
    private HBox premiumBanner;

    @FXML
    private VBox benefitsContainer;

    private ProgrammeFidelite programme;
    private User currentUser;
    private ProgrammeFideliteService programmeService;

    public ProgrammeFideliteCardController() {
        programmeService = new ProgrammeFideliteService();
    }

    public void setProgramme(ProgrammeFidelite programme) {
        this.programme = programme;

        // Set programme details
        nomProgrammeLabel.setText(programme.getNomProgramme());
        pointsLabel.setText(programme.getPoints() + " Points");

        // Set description if available
        if (programme.getNomProgramme() != null && !programme.getNomProgramme().isEmpty()) {
            descriptionLabel.setText(programme.getNomProgramme());
        } else {
            descriptionLabel.setText("Programme de fidélité exclusif pour nos clients.");
        }

        // Show premium banner if applicable
        premiumBanner.setVisible(programme.getPoints() >= 1000);

        // Add benefits if applicable
        addBenefits(programme);

        // Load image with enhanced error handling
        try {
            File imageFile = new File(programme.getPhoto());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                photoImageView.setImage(image);
            } else {
                // Load a default placeholder image
                Image placeholderImage = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
                if (placeholderImage != null) {
                    photoImageView.setImage(placeholderImage);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            // Try to load default image from resources
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
                photoImageView.setImage(defaultImage);
            } catch (Exception ex) {
                // Do nothing, UI will show empty image space
            }
        }

        // Apply scale animation on hover
        setCardHoverEffects();
    }

    private void addBenefits(ProgrammeFidelite programme) {
        // Clear previous benefits
        benefitsContainer.getChildren().clear();

        // Sample benefits based on programme points (in a real app, these would come from the programme object)
        List<String> benefits;

        if (programme.getPoints() < 500) {
            benefits = Arrays.asList("Accès aux offres exclusives");
        } else if (programme.getPoints() < 1000) {
            benefits = Arrays.asList(
                    "Accès aux offres exclusives",
                    "Réductions spéciales"
            );
        } else {
            benefits = Arrays.asList(
                    "Accès aux offres exclusives",
                    "Réductions spéciales",
                    "Service prioritaire"
            );
        }

        // Add each benefit as a bullet point
        for (String benefit : benefits) {
            Label benefitLabel = new Label("• " + benefit);
            benefitLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #718096;");
            benefitLabel.setWrapText(true);
            benefitsContainer.getChildren().add(benefitLabel);
        }
    }

    private void setCardHoverEffects() {
        VBox cardContainer = (VBox) acheterButton.getParent().getParent();

        cardContainer.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), cardContainer);
            scaleUp.setToX(1.03);
            scaleUp.setToY(1.03);
            scaleUp.play();
        });

        cardContainer.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), cardContainer);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

        // Update button state based on user points
        updateButtonState();
    }

    private void updateButtonState() {
        if (currentUser == null) {
            acheterButton.setText("Connectez-vous");
            acheterButton.setDisable(true);
            return;
        }

        if (currentUser.getPointsfid() < programme.getPoints()) {
            acheterButton.setText("Points insuffisants");
            acheterButton.setDisable(true);
            acheterButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #718096; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 8;");
        } else {
            acheterButton.setText("Acheter");
            acheterButton.setDisable(false);
            acheterButton.setStyle("-fx-background-color: linear-gradient(to right, #ed6637, #f89263); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;");
        }
    }

    @FXML
    private void acheterProgramme() {
        if (currentUser == null) {
            showAlert(AlertType.INFORMATION, "Information", "Connexion requise", "Veuillez vous connecter pour acheter un programme.");
            return;
        }

        // Check if user has enough points
        if (currentUser.getPointsfid() >= programme.getPoints()) {
            // Confirmation dialog
            if (showConfirmationDialog()) {
                // Deduct points
                int previousPoints = currentUser.getPointsfid();
                currentUser.setPointsfid(previousPoints - programme.getPoints());
            }
        } else {
            showAlert(AlertType.WARNING, "Points insuffisants", "Points insuffisants",
                    "Vous avez besoin de " + programme.getPoints() + " points pour acheter ce programme.\n" +
                            "Votre solde actuel: " + currentUser.getPointsfid() + " points.");
        }
    }

    private boolean showConfirmationDialog() {
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer l'achat");
        confirmDialog.setHeaderText("Êtes-vous sûr de vouloir acheter ce programme?");
        confirmDialog.setContentText("Programme: " + programme.getNomProgramme() + "\n" +
                "Coût: " + programme.getPoints() + " points\n" +
                "Votre solde actuel: " + currentUser.getPointsfid() + " points");

        return confirmDialog.showAndWait()
                .filter(response -> response == javafx.scene.control.ButtonType.OK)
                .isPresent();
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        if (type == AlertType.INFORMATION) {
            // Try to use custom styling for success alerts
            try {
                alert.initStyle(StageStyle.TRANSPARENT);
            } catch (Exception e) {
                // Fallback to standard alert
            }
        }

        alert.showAndWait();
    }

    private void refreshParentView() {
        // This would refresh the parent view to update points display
        // In a real app, you might use an event system or callback
        try {
            // Find parent controller
            ProgrammeFideliteListController parentController = findParentController();
            if (parentController != null) {
                parentController.setCurrentUser(currentUser);
            }
        } catch (Exception e) {
            System.err.println("Error refreshing parent view: " + e.getMessage());
        }
    }

    private ProgrammeFideliteListController findParentController() {
        // Implementation depends on your application architecture
        // This is a placeholder
        return null;
    }
}