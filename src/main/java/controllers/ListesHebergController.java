package controllers;

import Util.Helpers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;
import models.Hebergements;
import models.User;
import org.controlsfx.control.RangeSlider;
import services.HebergementService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    private User currentUser;

    private final ObservableList<Hebergements> hebergementList = FXCollections.observableArrayList();

    private FilteredList<Hebergements> items;

    private VBox createHebergementContainer(Hebergements hebergement) {
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

        HBox priceAndButtons = new HBox(10);
        priceAndButtons.setPadding(new Insets(5, 0, 5, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);

        Text priceText = new Text("TND" + hebergement.getPrixHeberg() + "/nuit");
        priceText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");
        priceAndButtons.getChildren().add(priceText);

        Button consultButton = new Button("Détails");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> openHebergementDetails(hebergement));


        priceAndButtons.getChildren().addAll(consultButton);
        hebergementContainer.getChildren().add(priceAndButtons);

        return hebergementContainer;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData() throws SQLException {
        hebergementList.setAll(hebergementService.recuperer());

        priceRangeSlider.setLowValue(1000);
        priceRangeSlider.setHighValue(9999);

        // Add listeners to the RangeSlider to trigger filtering when values change
        priceRangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> filterLodgingsByPrice());
        priceRangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> filterLodgingsByPrice());


        items = new FilteredList<>(hebergementList);

        updateFlowPane();
    }

    @FXML
    public void filterLodgingsByPrice() {
        Predicate<Hebergements> rangeBetween = h -> h.getPrixHeberg() >= priceRangeSlider.getLowValue()
                && h.getPrixHeberg() <= priceRangeSlider.getHighValue();

        items.setPredicate(rangeBetween);

        updateFlowPane();
    }

    private void updateFlowPane() {
        // Clear the existing items in the FlowPane
        hebergementsFlowPane.getChildren().clear();

        // Add the filtered items to the FlowPane
        for (Hebergements hebergement : items) {
            VBox hebergementContainer = createHebergementContainer(hebergement);
            hebergementsFlowPane.getChildren().add(hebergementContainer);
        }
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