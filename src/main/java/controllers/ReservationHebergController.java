package controllers;

import Util.Helpers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class ReservationHebergController {


    @FXML
    private Button navlistes;

    @FXML
    void afficherListeHebergements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/listesHeberg.fxml"));
            Parent root = loader.load();
            navlistes.getScene().setRoot(root);

        } catch (IOException e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des hébergements.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void goToMyReservations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/listReservationsHebergements.fxml"));
            Parent root = loader.load();

            navlistes.getScene().setRoot(root);

        } catch (IOException e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des resérvations.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
