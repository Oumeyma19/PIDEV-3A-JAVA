package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReservationHeberg.fxml"));
            StackPane root = loader.load();

            Scene scene = new Scene(root, 800, 800);

            primaryStage.setTitle("Hebergement Management");

            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (Exception e) {
            Logger.getLogger("Main").warning(e.getMessage());
        }
    }


    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}