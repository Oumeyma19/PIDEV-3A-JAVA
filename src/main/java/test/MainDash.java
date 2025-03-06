package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainDash extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main dashboard FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
        Parent root = loader.load();

        // Set up the primary stage
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(new Scene(root, 1047, 680));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}