package controllers;

import util.Helpers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.AvisHebergement;
import models.Hebergements;
import models.User;
import org.controlsfx.control.RangeSlider;
import services.AvisService;
import services.HebergementService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListesHebergController implements Initializable {

    @FXML
    private FlowPane hebergementsFlowPane;

    @FXML
    private Button btnBack;

    @FXML
    private RangeSlider priceRangeSlider;

    @FXML
    private Label minPriceLabel;

    @FXML
    private Label maxPriceLabel;

    private HebergementService hebergementService = HebergementService.getInstance();
    private AvisService avisService = AvisService.getInstance();
    private User currentUser;

    private final ObservableList<Hebergements> hebergementList = FXCollections.observableArrayList();
    private FilteredList<Hebergements> items;

    private VBox createHebergementContainer(Hebergements hebergement) {
        // Create the main container with enhanced styling
        VBox hebergementContainer = new VBox(0); // Reduced spacing for tighter layout
        hebergementContainer.setPadding(new Insets(0, 0, 0, 0));
        hebergementContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4); " +
                "-fx-border-radius: 8; " +
                "-fx-transition: all 0.3s;");
        hebergementContainer.setMaxWidth(320);
        hebergementContainer.setMinWidth(320);

        // Add hover effect with mouse events
        hebergementContainer.setOnMouseEntered(e ->
                hebergementContainer.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 6); " +
                        "-fx-border-radius: 8; " +
                        "-fx-translate-y: -3;")
        );

        hebergementContainer.setOnMouseExited(e ->
            hebergementContainer.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4); " +
                    "-fx-border-radius: 8; " +
                    "-fx-translate-y: 0;")
        );

        // Image container with simpler styling approach
        if (hebergement.getImageHebrg() != null && !hebergement.getImageHebrg().isEmpty()) {
            try {
                ImageView imageView = new ImageView(new Image(hebergement.getImageHebrg()));
                imageView.setFitWidth(320);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                // Create container for image
                VBox imageContainer = new VBox(imageView);
                imageContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8 8 0 0; -fx-border-radius: 8 8 0 0;");
                // Apply overflow hidden style
                imageContainer.setClip(new javafx.scene.shape.Rectangle(320, 200));

                hebergementContainer.getChildren().add(imageContainer);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }

        // Content container
        VBox contentContainer = new VBox(8);
        contentContainer.setPadding(new Insets(16));

        // Name and rating in same row with enhanced styling
        HBox nameAndRating = new HBox(10);
        nameAndRating.setAlignment(Pos.CENTER_LEFT);

        Label hebergementName = new Label(hebergement.getNomHeberg());
        hebergementName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2D3748;");
        hebergementName.setWrapText(true);
        hebergementName.setMaxWidth(200);

        // Get average rating
        float averageRating = calculateAverageRating(hebergement.getIdHebrg());

        // Create enhanced rating display
        HBox ratingDisplay = createEnhancedRatingDisplay(averageRating);

        // Add elements to the HBox with name taking priority
        HBox.setHgrow(hebergementName, javafx.scene.layout.Priority.ALWAYS);
        nameAndRating.getChildren().addAll(hebergementName, ratingDisplay);

        // Add the name and rating container to the content container
        contentContainer.getChildren().add(nameAndRating);

        // Add type and capacity info
        HBox infoContainer = new HBox(10);
        infoContainer.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(hebergement.getTypeHeberg());
        typeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5568; -fx-background-color: #EDF2F7; " +
                          "-fx-background-radius: 4; -fx-padding: 3 8;");

        Label capacityLabel = new Label(hebergement.getNbrClient() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5568; -fx-background-color: #EDF2F7; " +
                             "-fx-background-radius: 4; -fx-padding: 3 8;");

        infoContainer.getChildren().addAll(typeLabel, capacityLabel);
        contentContainer.getChildren().add(infoContainer);

        // Add location info if available
        if (hebergement.getAdresse() != null && !hebergement.getAdresse().isEmpty()) {
            HBox locationContainer = new HBox(6);
            locationContainer.setAlignment(Pos.CENTER_LEFT);

            // Location icon
            Text locationIcon = new Text("📍");
            locationIcon.setStyle("-fx-fill: #A0AEC0;");

            // Location text (shortened)
            String displayAddress = hebergement.getAdresse();
            if (displayAddress.length() > 30) {
                displayAddress = displayAddress.substring(0, 27) + "...";
            }

            Label locationLabel = new Label(displayAddress);
            locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

            locationContainer.getChildren().addAll(locationIcon, locationLabel);
            contentContainer.getChildren().add(locationContainer);
        }

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #E2E8F0;");
        contentContainer.getChildren().add(separator);

        // Price and button in same row with better styling
        HBox priceAndButtons = new HBox();
        priceAndButtons.setPadding(new Insets(4, 0, 0, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);

        Label priceText = new Label(hebergement.getPrixHeberg() + " TND");
        priceText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Label nightLabel = new Label("/nuit");
        nightLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #718096;");

        HBox priceBox = new HBox(4);
        priceBox.setAlignment(Pos.BASELINE_LEFT);
        priceBox.getChildren().addAll(priceText, nightLabel);

        HBox.setHgrow(priceBox, javafx.scene.layout.Priority.ALWAYS);
        priceAndButtons.getChildren().add(priceBox);

        Button consultButton = new Button("Voir détails");
        consultButton.setStyle("-fx-background-color: #FA7335; " +
                              "-fx-text-fill: white; " +
                              "-fx-font-weight: bold; " +
                              "-fx-background-radius: 4; " +
                              "-fx-padding: 8 16; " +
                              "-fx-cursor: hand;");
        consultButton.setOnAction(event -> openHebergementDetails(hebergement));

        // Add hover effect for button
        consultButton.setOnMouseEntered(e ->
            consultButton.setStyle("-fx-background-color: #E65A1F; " +
                                  "-fx-text-fill: white; " +
                                  "-fx-font-weight: bold; " +
                                  "-fx-background-radius: 4; " +
                                  "-fx-padding: 8 16; " +
                                  "-fx-cursor: hand;")
        );

        consultButton.setOnMouseExited(e ->
            consultButton.setStyle("-fx-background-color: #FA7335; " +
                                  "-fx-text-fill: white; " +
                                  "-fx-font-weight: bold; " +
                                  "-fx-background-radius: 4; " +
                                  "-fx-padding: 8 16; " +
                                  "-fx-cursor: hand;")
        );

        priceAndButtons.getChildren().add(consultButton);
        contentContainer.getChildren().add(priceAndButtons);

        // Add the content container to the main container
        hebergementContainer.getChildren().add(contentContainer);

        return hebergementContainer;
    }

    private HBox createEnhancedRatingDisplay(float rating) {
        HBox ratingBox = new HBox(3);
        ratingBox.setAlignment(Pos.CENTER);
        ratingBox.setPadding(new Insets(2, 6, 2, 6));
        ratingBox.setStyle("-fx-background-color: #FFF5F0; -fx-background-radius: 4;");

        // Add a numeric display of the rating
        Text ratingText = new Text(String.format("%.1f", rating));
        ratingText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #FA7335;");

        // Add star icon
        Text starIcon = new Text("★");
        starIcon.setStyle("-fx-font-size: 14px; -fx-fill: #FA7335;");

        ratingBox.getChildren().addAll(ratingText, starIcon);

        return ratingBox;
    }

    private void openHebergementDetails(Hebergements hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/detailsHebergC.fxml"));
            Parent root = loader.load();
            DetailHebergCController detailsController = loader.getController();
            detailsController.setHebergementDetails(hebergement);

            detailsController.setCurrentUser(currentUser);
            Stage stage = (Stage) hebergementsFlowPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'Hébergement");
        } catch (IOException e) {
            Helpers.showAlert("Erreur", "Impossible de charger les détails.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Calculate average rating
    private float calculateAverageRating(int hebergementId) {
        try {
            List<AvisHebergement> avis = avisService.recupererParHebergement(hebergementId);

            if (avis.isEmpty()) {
                return 0.0f; // No reviews yet
            }

            float sum = 0;
            for (AvisHebergement a : avis) {
                sum += a.getReview();
            }

            return sum / avis.size();
        } catch (Exception e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
            return 0.0f;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Configure FlowPane
            hebergementsFlowPane.setHgap(20);
            hebergementsFlowPane.setVgap(20);
            hebergementsFlowPane.setPadding(new Insets(25));
            hebergementsFlowPane.setStyle("-fx-background-color: #F7FAFC;");

            // Configure sliders


            // Style labels and buttons
            minPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
            maxPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A5568;");
            
            // Configure the RangeSlider
            priceRangeSlider.setMin(0);
            priceRangeSlider.setMax(10000);
            priceRangeSlider.setLowValue(1000);
            priceRangeSlider.setHighValue(9999);
            priceRangeSlider.setShowTickLabels(true);
            priceRangeSlider.setShowTickMarks(true);
            priceRangeSlider.setMajorTickUnit(2000);
            
            // Update labels when slider values change
            priceRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> {
                minPriceLabel.setText(String.format("%.0f TND", newVal.doubleValue()));
                filterLodgingsByPrice();
            });
            
            priceRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> {
                maxPriceLabel.setText(String.format("%.0f TND", newVal.doubleValue()));
                filterLodgingsByPrice();
            });
            
            // Set initial label values
            minPriceLabel.setText(String.format("%.0f TND", priceRangeSlider.getLowValue()));
            maxPriceLabel.setText(String.format("%.0f TND", priceRangeSlider.getHighValue()));
            
            btnBack.setStyle("-fx-background-color: transparent; " +
                            "-fx-text-fill: #3b9a9a; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #3b9a9a; " +
                            "-fx-border-radius: 4; " +
                            "-fx-padding: 8 16; " +
                            "-fx-cursor: hand;");
            
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void filterLodgingsByPrice() {
        if (items != null) {
            Predicate<Hebergements> rangeBetween = h -> 
                h.getPrixHeberg() >= priceRangeSlider.getLowValue() && 
                h.getPrixHeberg() <= priceRangeSlider.getHighValue();

            items.setPredicate(rangeBetween);
            updateFlowPane();
        }
    }

    private void loadData() throws SQLException {
        hebergementList.setAll(hebergementService.recuperer());
        
        // Initialize the filtered list
        items = new FilteredList<>(hebergementList);
        
        // Show all items initially
        updateFlowPane();
    }

    private void updateFlowPane() {
        // Clear the existing items in the FlowPane
        hebergementsFlowPane.getChildren().clear();

        // If we have no results, show a message
        if (items.isEmpty()) {
            Label noResultsLabel = new Label("Aucun hébergement trouvé");
            noResultsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #4A5568;");
            hebergementsFlowPane.getChildren().add(noResultsLabel);
            return;
        }

        // Add a loading indicator
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(50, 50);
        hebergementsFlowPane.getChildren().add(progress);

        // Use a separate thread to load the containers
        new Thread(() -> {
            // Create containers for each hebergement
            final List<VBox> containers = new ArrayList<>();
            for (Hebergements hebergement : items) {
                VBox container = createHebergementContainer(hebergement);
                containers.add(container);
            }

            // Update UI on the JavaFX thread
            javafx.application.Platform.runLater(() -> {
                hebergementsFlowPane.getChildren().clear();
                hebergementsFlowPane.getChildren().addAll(containers);
            });
        }).start();
    }

    private VBox createEmptyState() {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(50));
        emptyState.setPrefWidth(hebergementsFlowPane.getWidth() - 100);
        
        // Sad face icon
        Text sadFaceIcon = new Text("😕");
        sadFaceIcon.setStyle("-fx-font-size: 48px;");
        
        Label noResultsLabel = new Label("Aucun hébergement trouvé");
        noResultsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4A5568;");
        
        Label suggestionLabel = new Label("Essayez d'ajuster les filtres de prix pour voir plus de résultats.");
        suggestionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #718096;");
        
        emptyState.getChildren().addAll(sadFaceIcon, noResultsLabel, suggestionLabel);
        
        return emptyState;
    }

    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));

            Parent root = loader.load();
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);
            btnBack.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setCurrentUser(User user) {
        this.currentUser = user;

    }
}