package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class AdminDashboardController {

    @FXML private StackPane contentPane;
    @FXML private Button btnAjouter;
    @FXML private Button btnView;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    public void initialize() {
        btnAjouter.setOnAction(event -> loadPage("/ajouter.fxml"));
        btnView.setOnAction(event -> loadPage("/ViewOffres.fxml"));
        btnUpdate.setOnAction(event -> loadPage("/UpdateOffre.fxml"));
        btnDelete.setOnAction(event -> loadPage("/DeleteOffre.fxml"));
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
