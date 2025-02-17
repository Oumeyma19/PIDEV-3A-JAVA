package Controller;

import Service.ProgrammeFideliteService;
import Models.ProgrammeFidelite;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ProgrammeFideliteController {




    @FXML
    private TextField nomProgrammeField;

    @FXML
    private TextField pointsField;

    private final ProgrammeFideliteService programmeFideliteService;

    public ProgrammeFideliteController() {
        programmeFideliteService = new ProgrammeFideliteService();
    }

    @FXML
    public void handleAddProgramme() {
        try {
            // Récupérer les valeurs des champs de texte

            String nomProgramme = nomProgrammeField.getText();
            int points = Integer.parseInt(pointsField.getText());

            // Créer un objet ProgrammeFidelite
            ProgrammeFidelite programme = new ProgrammeFidelite(0,  nomProgramme, points);

            // Ajouter le programme via le service
            programmeFideliteService.addProgramme(programme);

            // Afficher un message de succès
            showAlert("Succès", "Le programme de fidélité a été ajouté avec succès.", AlertType.INFORMATION);

            // Effacer les champs après l'ajout
            clearFields();
        } catch (NumberFormatException e) {
            // Si les entrées sont incorrectes (par exemple, une chaîne au lieu d'un entier)
            showAlert("Erreur", "Veuillez entrer des valeurs valides pour tous les champs.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {

        nomProgrammeField.clear();
        pointsField.clear();
    }
}
