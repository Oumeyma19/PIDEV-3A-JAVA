package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainRe extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the reservation form FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/reservation_form.fxml"));

        // Set up the primary stage
        primaryStage.setTitle("Réservation d'Offre");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}