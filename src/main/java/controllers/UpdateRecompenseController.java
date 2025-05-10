package controllers;

import models.Recompense;
import services.RecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UpdateRecompenseController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField pointsField;

    @FXML
    private ImageView imageView;

    @FXML
    private Button changePhotoButton;

    private Recompense recompense;
    private final RecompenseService recompenseService = new RecompenseService();
    private String selectedImagePath; // Stocke le chemin de l'image sélectionnée
    private RewardsManagementController parentController; // Changed to RewardsManagementController

    // Méthode pour définir le controller parent
    public void setParentController(RewardsManagementController controller) {
        this.parentController = controller;
    }

    // Méthode pour initialiser la récompense à modifier
    public void setRecompense(Recompense recompense) {
        this.recompense = recompense;
        if (recompense != null) {
            nomField.setText(recompense.getNom());
            pointsField.setText(String.valueOf(recompense.getPointsRequis())); // Changed to getPointsRequired to match model field

            // Charger l'image existante s'il y en a une
            if (recompense.getPhoto() != null && !recompense.getPhoto().isEmpty()) {
                Image image = new Image(new File(recompense.getPhoto()).toURI().toString());
                imageView.setImage(image);
                selectedImagePath = recompense.getPhoto();
            }
        }
    }

    @FXML
    private void handleUpdate() {
        if (recompense == null) {
            showAlert("Erreur", "Aucune récompense sélectionnée.", Alert.AlertType.ERROR);
            return;
        }

        String nom = nomField.getText().trim();
        String pointsText = pointsField.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || pointsText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du nom (seules les lettres et espaces sont autorisés)
        if (!nom.matches("^[A-Za-zÀ-ÖØ-öø-ÿ ]+$")) {
            showAlert("Erreur", "Le nom ne doit contenir que des lettres et des espaces !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification des points requis (doit être un nombre positif)
        int pointsRequis;
        try {
            pointsRequis = Integer.parseInt(pointsText);
            if (pointsRequis <= 0) {
                showAlert("Erreur", "Les points requis doivent être un nombre positif !", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les points doivent être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Mise à jour de la récompense
        recompense.setNom(nom);
        recompense.setPointsRequis(pointsRequis); // Changed to setPointsRequired to match model field

        // Vérifier si une nouvelle image a été sélectionnée
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            recompense.setPhoto(selectedImagePath);
        }

        // Enregistrement des modifications
        recompenseService.updateRecompense(recompense);
        showAlert("Succès", "Récompense mise à jour avec succès.", Alert.AlertType.INFORMATION);

        // Rafraîchir la liste des récompenses dans le controller parent
        if (parentController != null) {
            parentController.refreshRecompensesList(); // Using the method from RewardsManagementController
        }

        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}