package controllers;

import models.ProgrammeFidelite;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterProgrammeController {
    @FXML
    private TextField nomProgrammeField;
    @FXML
    private TextField pointsField;
    @FXML
    private Label messageLabel;

    private ProgrammeFideliteController parentController;

    public void setParentController(ProgrammeFideliteController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleAddProgramme() {
        try {
            String nom = nomProgrammeField.getText();
            if (nom.isEmpty()) {
                showMessage("Le nom du programme ne peut pas être vide.", "red");
                return;
            }

            int points;
            try {
                points = Integer.parseInt(pointsField.getText());
            } catch (NumberFormatException e) {
                showMessage("Les points doivent être un nombre entier.", "red");
                return;
            }

            // Création du nouveau programme
            ProgrammeFidelite programme = new ProgrammeFidelite(0, nom, points, "default.png");

            // Ajout via le contrôleur parent
            parentController.addProgramme(programme);

            // Fermeture de la fenêtre
            Stage stage = (Stage) nomProgrammeField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Une erreur est survenue: " + e.getMessage(), "red");
        }
    }

    private void showMessage(String message, String color) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + color + ";");
            messageLabel.setVisible(true);
        }
    }
}