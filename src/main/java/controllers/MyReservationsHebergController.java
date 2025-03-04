package controllers;

import Util.Helpers;
import javafx.scene.control.*;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.AvisService;
import services.ReservHebergService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import services.UserService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class MyReservationsHebergController {

    @FXML
    private FlowPane reservationsFlowPane;

    @FXML
    private Button retourr;
    
    @FXML
    private VBox emptyStateContainer;

    private User currentUser;

    private final ReservHebergService reservHebergService = ReservHebergService.getInstance();

    private final ObservableList<ReservationHebergement> reservations = FXCollections.observableArrayList();

    private void fetchData() {
        try {
            List<ReservationHebergement> myReservations = reservHebergService.getMyReservations(this.currentUser.getId());
            System.out.println(myReservations);
            reservations.setAll(myReservations);
            
            // Show empty state if present and no reservations
            if (emptyStateContainer != null) {
                emptyStateContainer.setVisible(myReservations.isEmpty());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Impossible de récupérer vos réservations: " + e.getMessage());
        }
    }

    private VBox createHebergementContainer(ReservationHebergement reservationHebergement) {

        final Hebergements hebergement = reservationHebergement.getHebergements();

        VBox hebergementContainer = new VBox(10);
        hebergementContainer.setPadding(new Insets(10));
        hebergementContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        hebergementContainer.setMaxWidth(300);
        hebergementContainer.setMinWidth(300);

        if (hebergement.getImageHebrg() != null && !hebergement.getImageHebrg().isEmpty()) {
            ImageView imageView = new ImageView(new Image(hebergement.getImageHebrg()));
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            hebergementContainer.getChildren().add(imageView);
        }

        Text hebergementName = new Text(hebergement.getNomHeberg());
        hebergementName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        hebergementContainer.getChildren().add(hebergementName);

        VBox priceAndButtons = new VBox(10);
        priceAndButtons.setPadding(new Insets(5, 0, 5, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);

        Text nbPersonnesText = new Text("Nb. Personnes: " + reservationHebergement.getNbPersonnes());
        nbPersonnesText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Text date1Text = new Text("Checkin: " + reservationHebergement.getDateCheckIn().toLocalDateTime().toLocalDate());
        date1Text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Text date2Text = new Text("Checkout: " + reservationHebergement.getDateCheckOut().toLocalDateTime().toLocalDate());
        date2Text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Button consultButton = new Button("Annuler");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> onDeleteItem(reservationHebergement));


        priceAndButtons.getChildren().addAll(nbPersonnesText, date1Text, date2Text, consultButton);

        hebergementContainer.getChildren().add(priceAndButtons);

        return hebergementContainer;
    }

    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));

            Parent root = loader.load();
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);
            retourr.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
            showErrorAlert("Impossible de retourner au profil: " + ex.getMessage());
        }
    }

    private void onDeleteItem(ReservationHebergement reservationHebergement) {
        showCancelConfirmation(reservationHebergement);
    }
    
    private void showCancelConfirmation(ReservationHebergement reservation) {
        // Create custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Confirmation d'annulation");
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(30, 30, 20, 30));
        grid.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        
        // Warning icon
        Label warningIcon = new Label("⚠");
        warningIcon.setStyle("-fx-font-size: 40px; -fx-text-fill: #ffc107;");
        grid.add(warningIcon, 0, 0, 1, 2);
        
        // Title and message
        Label headerLabel = new Label("Annuler votre réservation ?");
        headerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
        grid.add(headerLabel, 1, 0);
        
        Label contentLabel = new Label("Cette action est irréversible. Votre réservation à " + 
                                       reservation.getHebergements().getNomHeberg() + 
                                       " sera définitivement annulée.");
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d; -fx-wrap-text: true;");
        contentLabel.setPrefWidth(350);
        grid.add(contentLabel, 1, 1);
        
        // Add a separator
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #e9ecef;");
        GridPane.setColumnSpan(separator, 2);
        GridPane.setVgrow(separator, Priority.ALWAYS);
        grid.add(separator, 0, 2);
        
        // Set dialog content
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getStyleClass().add("custom-dialog");
        dialogPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 15px;");
        
        // Add buttons
        ButtonType cancelButtonType = new ButtonType("Retour", ButtonData.CANCEL_CLOSE);
        ButtonType confirmButtonType = new ButtonType("Confirmer l'annulation", ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(cancelButtonType, confirmButtonType);
        
        // Style buttons
        Button cancelBtn = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelBtn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-background-radius: 20px; " +
                          "-fx-border-color: #ced4da; -fx-border-radius: 20px; -fx-font-weight: bold; -fx-padding: 10px 16px;");
        
        Button confirmBtn = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 20px; " +
                           "-fx-font-weight: bold; -fx-padding: 10px 16px;");
        
        // Add hover effect to buttons
        cancelBtn.setOnMouseEntered(e -> 
            cancelBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #212529; -fx-background-radius: 20px; " +
                             "-fx-border-color: #ced4da; -fx-border-radius: 20px; -fx-font-weight: bold; -fx-padding: 10px 16px;")
        );
        cancelBtn.setOnMouseExited(e -> 
            cancelBtn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-background-radius: 20px; " +
                             "-fx-border-color: #ced4da; -fx-border-radius: 20px; -fx-font-weight: bold; -fx-padding: 10px 16px;")
        );
        
        confirmBtn.setOnMouseEntered(e -> 
            confirmBtn.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; -fx-background-radius: 20px; " +
                              "-fx-font-weight: bold; -fx-padding: 10px 16px;")
        );
        confirmBtn.setOnMouseExited(e -> 
            confirmBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 20px; " +
                              "-fx-font-weight: bold; -fx-padding: 10px 16px;")
        );
        
        // Add shadow effect to dialog
        DropShadow dialogShadow = new DropShadow();
        dialogShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        dialogShadow.setRadius(20);
        dialogPane.setEffect(dialogShadow);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), dialogPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        dialog.setOnShowing(e -> fadeIn.play());
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(response -> {
            if (response == confirmButtonType) {
                try {
                    AvisService avisService = AvisService.getInstance();
                    
                    if (!reservHebergService.supprimer(reservation.getReservationHeberg_id())) {
                        showSuccessAlert("Réservation annulée avec succès !");
                        reservations.removeIf(r -> r.getReservationHeberg_id() == reservation.getReservationHeberg_id());
                        reservationsFlowPane.getChildren().removeIf(vb -> vb.getId().equals("" + reservation.getReservationHeberg_id()));
                        
                        // Show empty state if no more reservations
                        if (emptyStateContainer != null && 
                            reservationsFlowPane.getChildren().size() <= 0) { 
                            emptyStateContainer.setVisible(true);
                        }
                    } else {
                        showErrorAlert("Échec de l'annulation de la réservation.");
                    }
                } catch (Exception e) {
                    showErrorAlert("Une erreur est survenue lors de l'annulation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void showSuccessAlert(String message) {
        showStylishAlert("Succès", message, Alert.AlertType.INFORMATION);
    }
    
    private void showErrorAlert(String message) {
        showStylishAlert("Erreur", message, Alert.AlertType.ERROR);
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
            iconText = "✓";
            colorHex = "#28a745";
        } else {
            iconText = "ℹ";
            colorHex = "#17a2b8";
        }
        
        // Create styled content
        GridPane contentPane = new GridPane();
        contentPane.setAlignment(Pos.CENTER_LEFT);
        contentPane.setHgap(15);
        contentPane.setPadding(new Insets(20, 20, 10, 20));
        
        // Icon
        Label icon = new Label(iconText);
        icon.setStyle("-fx-font-size: 30px; -fx-text-fill: " + colorHex + ";");
        contentPane.add(icon, 0, 0);
        
        // Message
        Label message = new Label(content);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #212529; -fx-wrap-text: true;");
        message.setPrefWidth(300);
        message.setTextAlignment(TextAlignment.LEFT);
        contentPane.add(message, 1, 0);
        GridPane.setHgrow(message, Priority.ALWAYS);
        
        // Style dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(contentPane);
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        
        // Style button
        ButtonType okButton = ButtonType.OK;
        alert.getButtonTypes().setAll(okButton);
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), dialogPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        alert.setOnShown(e -> {
            Button okBtn = (Button) alert.getDialogPane().lookupButton(okButton);
            okBtn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 20px; " +
                          "-fx-padding: 8px 25px; -fx-font-size: 13px;");
            
            // Add hover effect
            String darkerColor = colorHex + "CC"; // Add opacity for darker effect
            okBtn.setOnMouseEntered(event -> 
                okBtn.setStyle("-fx-background-color: " + darkerColor + "; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 20px; " +
                              "-fx-padding: 8px 25px; -fx-font-size: 13px;")
            );
            
            okBtn.setOnMouseExited(event -> 
                okBtn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 20px; " +
                              "-fx-padding: 8px 25px; -fx-font-size: 13px;")
            );
            
            // Play the fade-in animation
            fadeIn.play();
        });
        
        // Add shadow
        dialogPane.setEffect(new DropShadow(10, Color.gray(0.5, 0.5)));
        
        alert.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        fetchData();
        System.out.println("[MY RESERVATIONS CONTROLLER] logged in user: " + currentUser);
        
        // Clear existing content (except back button)
        reservationsFlowPane.getChildren().clear();
        
        // If there are reservations, show them
        if (!reservations.isEmpty()) {
            for (ReservationHebergement reservationHebergement : reservations) {
                VBox hebergementContainer = createHebergementContainer(reservationHebergement);
                hebergementContainer.setId(reservationHebergement.getReservationHeberg_id() + "");
                reservationsFlowPane.getChildren().add(hebergementContainer);
            }
            
            // Make sure empty state is hidden if it exists
            if (emptyStateContainer != null) {
                emptyStateContainer.setVisible(false);
            }
        } 
        // Otherwise show empty state if it exists
        else if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(true);
        }
    }
}