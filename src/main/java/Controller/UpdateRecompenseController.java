package Controller;

import Models.Recompense;
import Service.RecompenseService;
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
    private ImageView imageView; // Ajout de l'ImageView pour afficher la photo

    @FXML
    private Button changePhotoButton;

    private Recompense recompense;
    private final RecompenseService recompenseService = new RecompenseService();
    private String selectedImagePath; // Stocke le chemin de l'image sélectionnée

    // Méthode pour initialiser la récompense à modifier
    public void setRecompense(Recompense recompense) {
        this.recompense = recompense;
        if (recompense != null) {
            nomField.setText(recompense.getNom());
            pointsField.setText(String.valueOf(recompense.getPointsRequis()));

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

        String nom = nomField.getText();
        String pointsText = pointsField.getText();

        if (nom.isEmpty() || pointsText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int pointsRequis = Integer.parseInt(pointsText);
            recompense.setNom(nom);
            recompense.setPointsRequis(pointsRequis);

            // Mettre à jour la photo si une nouvelle a été sélectionnée
            if (selectedImagePath != null) {
                recompense.setPhoto(selectedImagePath);
            }

            recompenseService.updateRecompense(recompense);
            showAlert("Succès", "Récompense mise à jour avec succès.", Alert.AlertType.INFORMATION);

            // Rafraîchir la liste dans le contrôleur parent


            // Fermer la fenêtre
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les points doivent être un nombre valide.", Alert.AlertType.ERROR);
        }
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
