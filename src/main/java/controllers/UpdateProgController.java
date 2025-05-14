package controllers;

import models.ProgrammeFidelite;
import services.ProgrammeFideliteService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateProgController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField pointsField;

    @FXML
    private TextField photoField;

    private ProgrammeFidelite programme;
    private AfficherListProgrammeClient parentController;

    public void initData(ProgrammeFidelite programme, AfficherListProgrammeClient parentController) {
        this.programme = programme;
        this.parentController = parentController;
        nomField.setText(programme.getNomProgramme());
        pointsField.setText(String.valueOf(programme.getPoints()));
        photoField.setText(programme.getPhoto());
    }

    @FXML
    private void handleSave() {
        // Validation des champs
        if (nomField.getText().isEmpty() || pointsField.getText().isEmpty() || photoField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Mettre à jour les données du programme
            programme.setNomProgramme(nomField.getText());
            programme.setPoints(Integer.parseInt(pointsField.getText()));
            programme.setPhoto(photoField.getText());

            // Enregistrer les modifications dans la base de données
            ProgrammeFideliteService service = new ProgrammeFideliteService();
            service.updateProgramme(programme);

            // Recharger la liste des programmes dans le parent
            parentController.loadProgrammes();

            // Fermer la fenêtre de modification
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les points doivent être un nombre valide.", Alert.AlertType.ERROR);
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}