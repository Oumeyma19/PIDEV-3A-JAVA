package controllers;

import Util.Helpers;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.ReservHebergService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservController {

    private final ReservHebergService reservService = ReservHebergService.getInstance();

    @FXML
    private DatePicker dateI;

    @FXML
    private DatePicker dateO;

    @FXML
    private TextField nbPersons;

    @FXML
    private Label nomClient;

    @FXML
    private Button retour;

    @FXML
    private Button submit;

    private User owner;

    private Hebergements hebergement;

    public void setData(Hebergements hebergement, User owner) {
        this.hebergement = hebergement;
        this.owner = owner;

        nomClient.setText(owner.getFirstname() + " " + owner.getLastname());
    }

    @FXML
    public void submitReservation(ActionEvent event) {
        try {
            // ✅ Vérification que tous les champs sont remplis
            if (nbPersons.getText().isEmpty() || dateI.getValue() == null || dateO.getValue() == null) {
                Helpers.showAlert("Error", "Veuillez remplir tous les champs!", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Validation du nombre de clients
            int nbrClient;
            try {
                nbrClient = Integer.parseInt(nbPersons.getText());
                if ((nbrClient <= 0) || (nbrClient > hebergement.getNbrClient())) {
                    Helpers.showAlert("Error", "Le nombre de clients doit être > 0 et inférieur ou égal à la capacité maximale de l'hébergement", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                Helpers.showAlert("Error", "Veuillez entrer un nombre valide pour les clients!", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Validation des dates
            Timestamp dateCheckin = Timestamp.valueOf(dateI.getValue().atStartOfDay());
            Timestamp dateCheckout = Timestamp.valueOf(dateO.getValue().atStartOfDay());

            if (dateCheckin.after(dateCheckout)) {
                Helpers.showAlert("Error", "La date de sortie doit être après la date d'entrée!", Alert.AlertType.ERROR);
                return;
            }

            if (reservService.existsBySameDates(dateCheckin, dateCheckout)) {
                Helpers.showAlert("Error", "Impossible de réserver le même logement sur la même date !", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Création et enregistrement de la réservation
            ReservationHebergement reservation = new ReservationHebergement(dateCheckin, dateCheckout, owner, hebergement, nbrClient);
            reservService.ajouter(reservation);

            Helpers.showAlert("Succès", "Réservation ajoutée avec succès !", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            Helpers.showAlert("Erreur", "Impossible d'ajouter la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ReservationHeberg.fxml"));

            Parent root = loader.load();

            retour.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
