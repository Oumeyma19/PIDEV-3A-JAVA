package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.NotificationService;
import services.ReservHebergService;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservController {

    @FXML
    private DatePicker dateI;

    @FXML
    private DatePicker dateO;

    @FXML
    private TextField nbPersons;

    @FXML
    private Label nomClient;

    @FXML
    private Button retour;

    @FXML
    private Button submit;

    private Hebergements hebergement;
    private User currentUser;
    private final NotificationService notificationService = NotificationService.getInstance();
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setData(Hebergements hebergement) {
        if (this.currentUser == null) {
            throw new IllegalStateException("Current user is not set. Please set the current user before calling setData.");
        }
        this.hebergement = hebergement;
        nomClient.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
    }

    @FXML
    private void initialize() {
        // Style DatePicker popup
        styleInputFields();
    }

    private void styleInputFields() {
        // Add focus effects to the input fields
        DropShadow focusEffect = new DropShadow();
        focusEffect.setBlurType(BlurType.GAUSSIAN);
        focusEffect.setColor(Color.valueOf("#FA733580"));
        focusEffect.setHeight(5);
        focusEffect.setWidth(5);
        focusEffect.setRadius(10);

        // Style TextField with focus effect
        nbPersons.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                nbPersons.setEffect(focusEffect);
                nbPersons.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 16px; " +
                                  "-fx-border-color: #FA7335; -fx-background-color: #f8f9fa; -fx-padding: 10;");
            } else {
                nbPersons.setEffect(null);
                nbPersons.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 16px; " +
                                  "-fx-border-color: #e9ecef; -fx-background-color: #f8f9fa; -fx-padding: 10;");
            }
        });

        // Apply the same effect pattern to date pickers
        dateI.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dateI.setEffect(focusEffect);
                dateI.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; " +
                              "-fx-background-color: #f8f9fa; -fx-border-color: #FA7335;");
            } else {
                dateI.setEffect(null);
                dateI.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; " +
                              "-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef;");
            }
        });

        dateO.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dateO.setEffect(focusEffect);
                dateO.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; " +
                              "-fx-background-color: #f8f9fa; -fx-border-color: #FA7335;");
            } else {
                dateO.setEffect(null);
                dateO.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; " +
                              "-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef;");
            }
        });
    }

    @FXML
    void submitReservation(ActionEvent event) {
        try {
            // Validate input fields
            if (nbPersons.getText().isEmpty() || dateI.getValue() == null || dateO.getValue() == null) {
                showStylishAlert("Champs Manquants", "Veuillez remplir tous les champs!", Alert.AlertType.ERROR);
                return;
            }

            // Validate number of persons
            int nbrClient;
            try {
                nbrClient = Integer.parseInt(nbPersons.getText());
                if (nbrClient <= 0 || nbrClient > hebergement.getNbrClient()) {
                    showStylishAlert("Nombre Invalide", 
                        "Le nombre de clients doit être supérieur à 0 et inférieur ou égal à " + 
                        hebergement.getNbrClient() + " (capacité de l'hébergement).", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showStylishAlert("Format Invalide", "Veuillez entrer un nombre valide pour les clients!", Alert.AlertType.ERROR);
                return;
            }

            // Validate dates
            Timestamp dateCheckin = Timestamp.valueOf(dateI.getValue().atStartOfDay());
            Timestamp dateCheckout = Timestamp.valueOf(dateO.getValue().atStartOfDay());

            if (dateCheckin.after(dateCheckout)) {
                showStylishAlert("Dates Invalides", "La date de sortie doit être après la date d'entrée!", Alert.AlertType.ERROR);
                return;
            }

            // Create and save the reservation
            ReservationHebergement reservation = new ReservationHebergement(dateCheckin, dateCheckout, currentUser, hebergement, nbrClient);
            ReservHebergService reservService = ReservHebergService.getInstance();
            reservService.ajouter(reservation);

            notificationService.showNotification("Réservation effectuée avec succès!", "Vous avez réservé " + hebergement.getNomHeberg() + " pour " + nbrClient + " personnes.");

            showSuccessDialog();

        } catch (Exception e) {
            showStylishAlert("Erreur de Réservation", "Impossible d'ajouter la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation Confirmée");
        alert.setHeaderText(null);
        
        // Create a styled content pane
        GridPane contentPane = new GridPane();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setHgap(20);
        contentPane.setVgap(15);
        contentPane.setPadding(new Insets(20, 20, 20, 20));
        
        // Success icon
        try {
            ImageView successIcon = new ImageView(new Image(getClass().getResourceAsStream("/logo/success.png")));
            successIcon.setFitHeight(64);
            successIcon.setFitWidth(64);
            contentPane.add(successIcon, 0, 0, 1, 2);
        } catch (Exception e) {
            // Fallback if image not found
            Label iconPlaceholder = new Label("✓");
            iconPlaceholder.setStyle("-fx-font-size: 48px; -fx-text-fill: #28a745;");
            contentPane.add(iconPlaceholder, 0, 0, 1, 2);
        }
        
        // Success message
        Label titleLabel = new Label("Réservation Réussie !");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        contentPane.add(titleLabel, 1, 0);
        
        Label messageLabel = new Label("Votre réservation a été ajoutée avec succès.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057; -fx-wrap-text: true;");
        messageLabel.setPrefWidth(300);
        messageLabel.setTextAlignment(TextAlignment.LEFT);
        contentPane.add(messageLabel, 1, 1);
        
        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(contentPane);
        dialogPane.getStyleClass().add("success-dialog");
        dialogPane.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        
        // Create custom button
        ButtonType okButton = new ButtonType("Super !", ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);
        
        // Apply custom styling to the button
        alert.setOnShown(e -> {
            Button okBtn = (Button) alert.getDialogPane().lookupButton(okButton);
            okBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                          "-fx-padding: 10px 30px; -fx-font-size: 14px;");
            
            // Add hover effect
            okBtn.setOnMouseEntered(event -> 
                okBtn.setStyle("-fx-background-color: #218838; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                              "-fx-padding: 10px 30px; -fx-font-size: 14px;"));
            
            okBtn.setOnMouseExited(event -> 
                okBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                              "-fx-padding: 10px 30px; -fx-font-size: 14px;"));
                              
            // Fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), dialogPane);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        // Remove default header and icon
        alert.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setEffect(new DropShadow(10, Color.gray(0.5, 0.5)));
        
        alert.showAndWait();
    }

    private void showStylishAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        // Determine icon and color based on alert type
        String iconText;
        String colorHex;
        
        if (alertType == Alert.AlertType.ERROR) {
            iconText = "✕";
            colorHex = "#dc3545";
        } else if (alertType == Alert.AlertType.WARNING) {
            iconText = "⚠";
            colorHex = "#ffc107";
        } else if (alertType == Alert.AlertType.INFORMATION) {
            iconText = "ℹ";
            colorHex = "#17a2b8";
        } else {
            iconText = "?";
            colorHex = "#6c757d";
        }
        
        // Create styled content
        GridPane contentPane = new GridPane();
        contentPane.setAlignment(Pos.CENTER_LEFT);
        contentPane.setHgap(15);
        contentPane.setPadding(new Insets(20, 10, 10, 10));
        
        // Icon
        Label icon = new Label(iconText);
        icon.setStyle("-fx-font-size: 28px; -fx-text-fill: " + colorHex + ";");
        contentPane.add(icon, 0, 0);
        
        // Message
        Label message = new Label(content);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #212529; -fx-wrap-text: true;");
        message.setPrefWidth(300);
        contentPane.add(message, 1, 0);
        GridPane.setHgrow(message, Priority.ALWAYS);
        
        // Style dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(contentPane);
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        
        // Style buttons based on alert type
        ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);
        
        alert.setOnShown(e -> {
            Button okBtn = (Button) alert.getDialogPane().lookupButton(okButton);
            okBtn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                          "-fx-padding: 8px 25px; -fx-font-size: 13px;");
            
            // Add hover effect
            String darkerColor = colorHex + "CC"; // Add opacity for darker effect
            okBtn.setOnMouseEntered(event -> 
                okBtn.setStyle("-fx-background-color: " + darkerColor + "; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                              "-fx-padding: 8px 25px; -fx-font-size: 13px;"));
            
            okBtn.setOnMouseExited(event -> 
                okBtn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 30px; " +
                              "-fx-padding: 8px 25px; -fx-font-size: 13px;"));
        });
        
        // Add shadow
        dialogPane.setEffect(new DropShadow(10, Color.gray(0.5, 0.5)));
        
        alert.showAndWait();
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            // Load the FXML file first
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load(); // Load the FXML file

            // Get the controller after loading the FXML
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser); // Set the current user

            // Set the new scene
            retour.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(ReservController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}