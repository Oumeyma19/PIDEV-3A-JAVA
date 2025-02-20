package Controller;

import Models.Recompense;
import Service.RecompenseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AjouterRecompenseController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField pointsField;

    @FXML
    private Button uploadButton;

    @FXML
    private ImageView imageView; // ImageView to display the selected image

    private String photoPath;

    private final RecompenseService recompenseService = new RecompenseService();

    @FXML
    private void handleAddRecompense() {
        String nom = nomField.getText();
        String pointsText = pointsField.getText();

        // Vérification des entrées
        if (nom.isEmpty() || pointsText.isEmpty() || photoPath == null || photoPath.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs et sélectionner une image !");
            return;
        }
        int pointsRequis;
        try {
            pointsRequis = Integer.parseInt(pointsText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les points requis doivent être un nombre !");
            return;
        }
        // Création et ajout de la récompense
        Recompense recompense = new Recompense(0, 34, nom, pointsRequis, photoPath);
        recompenseService.addRecompense(recompense);

        // Confirmation
        showAlert("Succès", "Récompense ajoutée avec succès !");
        clearFields();

        // Change scene to RecompenseList.fxml
        changeSceneToRecompenseList();
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            photoPath = selectedFile.getAbsolutePath();
            uploadButton.setText("Image sélectionnée");

            // Load and display the selected image in the ImageView
            Image image = new Image("file:" + selectedFile.getAbsolutePath()); // Correct way to load the image
            imageView.setImage(image); // Set the image to the ImageView
        }
    }

    private void clearFields() {
        nomField.clear();
        pointsField.clear();
        photoPath = null;
        uploadButton.setText("Télécharger une image");
        imageView.setImage(null); // Clear the ImageView when resetting the form
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void changeSceneToRecompenseList() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/RecompenseList.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page des récompenses.");
        }
    }
}
