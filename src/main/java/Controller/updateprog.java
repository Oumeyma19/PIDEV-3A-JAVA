package Controller;

import Models.ProgrammeFidelite;
import Service.ProgrammeFideliteService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class updateprog {
    @FXML
    private TextField nomProgrammeField;
    @FXML
    private TextField pointsField;

    private ProgrammeFidelite programme;
    private ProgrammeFideliteService service = new ProgrammeFideliteService();
    private afficherlist parentController; // Référence au contrôleur principal

    // Cette méthode initialise les données du programme et le contrôleur parent
    public void initData(ProgrammeFidelite programme, afficherlist controller) {
        this.programme = programme;
        this.parentController = controller;

        // Remplir les champs avec les valeurs actuelles du programme
        nomProgrammeField.setText(programme.getNomProgramme());
        pointsField.setText(String.valueOf(programme.getPoints()));
    }

    @FXML
    private void handleUpdate() {
        String newNom = nomProgrammeField.getText();
        int newPoints = Integer.parseInt(pointsField.getText());

        programme.setNomProgramme(newNom);
        programme.setPoints(newPoints);

        service.updateProgramme(programme);

        // Mettre à jour la liste dans le contrôleur parent
        parentController.loadProgrammes();

        // Fermer la fenêtre
        Stage stage = (Stage) nomProgrammeField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomProgrammeField.getScene().getWindow();
        stage.close();
    }
}
