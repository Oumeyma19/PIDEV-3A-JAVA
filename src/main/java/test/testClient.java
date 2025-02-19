package test;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane; // Import BorderPane instead of AnchorPane

public class testClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the SignIn UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Clients.fxml"));
            AnchorPane root = loader.load(); // Change AnchorPane to BorderPane

            // Create the Scene from the FXML root layout
            Scene scene = new Scene(root, 1450 , 750);

            // Set the title of the window
            primaryStage.setTitle("SignUp");

            // Set the Scene to the primaryStage
            primaryStage.setScene(scene);

            // Show the Stage (window)
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
