package com.example.pidev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the AddTourView UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ajouterHeberg.fxml"));
            AnchorPane root = loader.load();

            // Create the Scene from the FXML root layout
            Scene scene = new Scene(root, 800, 800);

            // Set the title of the window
            primaryStage.setTitle("Hebergement Management");

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