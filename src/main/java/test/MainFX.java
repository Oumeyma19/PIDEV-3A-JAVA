package test;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


       /* FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/views/ReservationFlightView.fxml"));*/



        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/views/AirportView.fxml"));


        Parent root = loader.load(); // classe java qui charque le fichier XML
       /* Scene scene = new Scene(root);// root = acteur ( creation de scene )
        primaryStage.setScene(scene); // scene = decor
        primaryStage.setTitle("Ajouter Airport");
        primaryStage.show();*/


        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, true, true);
        decorator.setCustomMaximize(true);
        Scene scene = new Scene(decorator);
        scene.getStylesheets().add(getClass().getResource("/views/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);  // Increase width
        primaryStage.setHeight(700);  // Increase height
        primaryStage.setTitle("Airport Management");
        primaryStage.show();



    }
}
