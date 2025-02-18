package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the AddTourView UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test.fxml"));
            AnchorPane root = loader.load();

            // Create the Scene from the FXML root layout
            Scene scene = new Scene(root, 735, 600);

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
