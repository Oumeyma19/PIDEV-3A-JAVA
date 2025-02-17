package Controller;

import Models.Recompense;
import Service.RecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import java.util.List;

public class RecompenseController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField pointsField;
    @FXML
    private TextField photoField;
    @FXML
    private ListView<String> recompensesList;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private RecompenseService recompenseService;

    public RecompenseController() {
        this.recompenseService = new RecompenseService();
    }

    @FXML
    private void initialize() {
        afficherRecompenses();
    }

    // Ajouter une récompense
    @FXML
    private void handleAddRecompense(ActionEvent event) {
        String nom = nomField.getText();
        int pointsRequis = Integer.parseInt(pointsField.getText());
        String photo = photoField.getText();

        if (nom.isEmpty() || pointsRequis <= 0 || photo.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs avec des valeurs valides.");
            return;
        }

        Recompense recompense = new Recompense(0, nom, pointsRequis, photo);  // ID est 0 lors de l'ajout
        recompenseService.addRecompense(recompense);
        afficherRecompenses();
    }

    // Modifier une récompense
    @FXML
    private void handleUpdateRecompense(ActionEvent event) {
        String nom = nomField.getText();
        int pointsRequis = Integer.parseInt(pointsField.getText());
        String photo = photoField.getText();

        if (nom.isEmpty() || pointsRequis <= 0 || photo.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs avec des valeurs valides.");
            return;
        }

        // L'ID de la récompense doit être sélectionné à partir de la liste des récompenses
        // Supposons ici qu'on récupère un ID pour la mise à jour (à adapter selon ton interface)
        int id = 1;  // Remplacer par l'ID de la récompense sélectionnée
        Recompense recompense = new Recompense(id, nom, pointsRequis, photo);
        recompenseService.updateRecompense(recompense);
        afficherRecompenses();
    }

    // Supprimer une récompense
    @FXML
    private void handleDeleteRecompense(ActionEvent event) {
        // L'ID de la récompense à supprimer doit être sélectionné à partir de la liste
        int id = 1;  // Remplacer par l'ID de la récompense sélectionnée
        recompenseService.deleteRecompense(id);
        afficherRecompenses();
    }

    // Afficher toutes les récompenses
    private void afficherRecompenses() {
        recompensesList.getItems().clear();  // Effacer les anciennes récompenses

        List<Recompense> recompenses = recompenseService.getAllRecompenses();
        for (Recompense recompense : recompenses) {
            String text = "Nom: " + recompense.getNom() + " | Points: " + recompense.getPointsRequis() + " | Photo: " + recompense.getPhoto();
            recompensesList.getItems().add(text);
        }
    }

    // Afficher une alerte
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
