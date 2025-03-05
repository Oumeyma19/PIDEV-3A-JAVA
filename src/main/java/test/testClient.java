package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class testClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the Home UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            BorderPane root = loader.load();

            // Create the Scene from the FXML root layout
            Scene scene = new Scene(root);

           /* double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();

            // Set stage size based on screen size
            primaryStage.setWidth(screenWidth * 0.8);  // 80% of screen width
            primaryStage.setHeight(screenHeight * 0.8); // 80% of screen height*/

            // Set the title of the window
            primaryStage.setTitle("Home");

            // Set the Scene to the primaryStage
            primaryStage.setScene(scene);

            // Show the Stage (window)
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
