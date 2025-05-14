package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Gestion des Offres");

        // Main Menu with Buttons to Navigate to CRUD Interfaces
        Button btnAjouter = new Button("Ajouter Offre");
        Button btnView = new Button("Voir Offres");
        Button btnUpdate = new Button("Modifier Offre");
        Button btnDelete = new Button("Supprimer Offre");

        btnAjouter.setOnAction(event -> openWindow("/ajoute.fxml"));
        btnView.setOnAction(event -> openWindow("/views/ViewOffres.fxml"));
        btnUpdate.setOnAction(event -> openWindow("/views/UpdateOffre.fxml"));
        btnDelete.setOnAction(event -> openWindow("/views/DeleteOffre.fxml"));

        VBox root = new VBox(10, btnAjouter, btnView, btnUpdate, btnDelete);
        root.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openWindow(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
