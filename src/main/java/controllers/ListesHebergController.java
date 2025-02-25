package controllers;

import Util.Helpers;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Hebergements;
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

public class ListesHebergController implements Initializable {

    @FXML
    private FlowPane hebergementsFlowPane;

    private HebergementService hebergementService = HebergementService.getInstance();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/detailsHebergC.fxml"));
            Parent root = loader.load();
            DetailHebergCController detailsController = loader.getController();
            detailsController.setHebergementDetails(hebergement);
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
            List<Hebergements> hebergements = hebergementService.recuperer();
            for (Hebergements hebergement : hebergements) {
                VBox hebergementContainer = createHebergementContainer(hebergement);
                hebergementsFlowPane.getChildren().add(hebergementContainer);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}