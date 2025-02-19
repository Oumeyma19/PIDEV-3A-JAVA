package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import tools.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteOffreController {

    @FXML private TextField idField;

    private final Connection cnx = MyDataBase.getInstance().getCnx();

    @FXML
    private void deleteOffer() {
        int id = Integer.parseInt(idField.getText());

        String query = "DELETE FROM offers WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Succès", "Offre supprimée avec succès!");
            } else {
                showAlert("Erreur", "Offre introuvable!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Suppression échouée!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
