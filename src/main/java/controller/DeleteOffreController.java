package controller;

import javafx.scene.control.Alert;
import tools.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteOffreController {

    private final Connection cnx = MyDataBase.getInstance().getCnx();

    public void deleteOfferById(int id) {
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
