package Controller;

import Models.ProgrammeFidelite;
import Service.ProgrammeFideliteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import Service.RecompenseService;
import Models.Recompense;

import java.io.IOException;
import java.util.List;

public class RecompenseController {

    @FXML
    private FlowPane recompensesFlowPane;  // Container for displaying the list of recompenses

    @FXML
    private Button addRecompenseButton;  // Button to open the "Add Recompense" form

    private final RecompenseService recompenseService = new RecompenseService();  // Service for managing recompenses

    @FXML
    public void initialize() {
        // Fetch all recompenses from the service
        List<Recompense> recompenses = recompenseService.getAllRecompenses();

        // Add recompenses to the FlowPane for display
        for (Recompense recompense : recompenses) {
            VBox recompenseContainer = createRecompenseContainer(recompense);
            recompensesFlowPane.getChildren().add(recompenseContainer);
        }

        // Open the "Add Recompense" view when the button is clicked
        addRecompenseButton.setOnAction(event -> openAddRecompenseView());
    }

    // Create a VBox container for each recompense to display its image, description, and buttons
    private VBox createRecompenseContainer(Recompense recompense) {
        VBox recompenseContainer = new VBox(10);
        recompenseContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); " +
                "-fx-border-color: #ddd; -fx-border-radius: 5;");
        recompenseContainer.setMaxWidth(300);
        recompenseContainer.setMinWidth(300);

        // Display the image if it exists
        if (recompense.getPhoto() != null && !recompense.getPhoto().isEmpty()) {
            ImageView imageView = new ImageView(new Image("file:" + recompense.getPhoto()));
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            recompenseContainer.getChildren().add(imageView);
        }

        // Add the name of the recompense
        Text recompenseDescription = new Text(recompense.getNom());
        recompenseDescription.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        recompenseContainer.getChildren().add(recompenseDescription);

        // Display the points required for the recompense
        Text pointsText = new Text("Points: " + recompense.getPointsRequis());
        pointsText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");
        recompenseContainer.getChildren().add(pointsText);

        // Create action buttons for Consult, Update, and Delete
        Button consultButton = new Button("Consult");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> openRecompenseDetails(recompense));

        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(event -> updateRecompense(recompense));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(event -> deleteRecompense(recompense, recompenseContainer));

        VBox buttonContainer = new VBox(10, consultButton, updateButton, deleteButton);
        recompenseContainer.getChildren().add(buttonContainer);

        return recompenseContainer;
    }

    // Display the details of the recompense (currently just a placeholder)
    private void openRecompenseDetails(Recompense recompense) {
        System.out.println("Displaying details for " + recompense.getNom());
    }

    // Open the Update Recompense form
    private void updateRecompense(Recompense recompense) {
        try {
            // Load the update form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/update_recompense.fxml"));
            VBox updateForm = loader.load();

            // Get the controller for the update form
            UpdateRecompenseController updateController = loader.getController();

            // Pass the recompense object to the controller for editing
            updateController.setRecompense(recompense);

            // Create a new stage for the update form
            Scene updateScene = new Scene(updateForm);
            Stage stage = new Stage();  // Open a new window for the update form
            stage.setScene(updateScene);
            stage.setTitle("Update Recompense");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Update Recompense view.", Alert.AlertType.ERROR);
        }
    }

    // Delete a recompense after confirmation
    private void deleteRecompense(Recompense recompense, VBox recompenseContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Recompense");
        alert.setContentText("Are you sure you want to delete this recompense?");

        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                recompenseService.deleteRecompense(recompense.getId());
                recompensesFlowPane.getChildren().remove(recompenseContainer);
                showAlert("Success", "Recompense deleted successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }
    private void loadRecompenses() {
        recompensesFlowPane.getChildren().clear();
        List<Recompense> recompenses = recompenseService.getAllRecompenses();

        for (Recompense recompense : recompenses) {
            Label label = new Label(recompense.getNom() + " - " + recompense.getPointsRequis() + " points");
            recompensesFlowPane.getChildren().add(label);
        }
    }

    public void refreshRecompenseList() {
        loadRecompenses(); // Recharge la liste depuis la base de données
    }
    @FXML
    private void openUpdateRecompenseView(Recompense recompense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UpdateRecompense.fxml"));
            Parent root = loader.load();

            UpdateRecompenseController controller = loader.getController();
            controller.setRecompense(recompense);
            // Passe ce contrôleur comme parent

            Stage stage = new Stage();
            stage.setTitle("Modifier Récompense");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Rafraîchir après fermeture de la fenêtre
            refreshRecompenseList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Show a generic alert box with a given title and message
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Open the "Add Recompense" view
    @FXML
    private void openAddRecompenseView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ajouter_recompense.fxml"));
            Scene addRecompenseScene = new Scene(loader.load());
            Stage stage = (Stage) addRecompenseButton.getScene().getWindow();
            stage.setScene(addRecompenseScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Add Recompense view.", Alert.AlertType.ERROR);
        }
    }




}
