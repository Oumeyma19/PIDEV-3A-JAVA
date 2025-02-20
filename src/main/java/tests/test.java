package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the AddTourView UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ajouter_recompense.fxml"));

            AnchorPane root = loader.load();

            // Create the Scene from the FXML root layout
            Scene scene = new Scene(root, 400, 500);

            // Set the title of the window
            primaryStage.setTitle("Tour Management");

            // Set the Scene to the primaryStage
            primaryStage.setScene(scene);

            // Show the Stage (window)
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}