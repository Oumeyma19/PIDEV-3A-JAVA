package com.example.pidev.controllers;

import com.example.pidev.Util.Helpers;
import com.example.pidev.models.Hebergements;
import com.example.pidev.models.ReservationHebergement;
import com.example.pidev.models.User;
import com.example.pidev.services.ReservHebergService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

            // ✅ Validation du nombre de clients
            int nbrClient;
            try {
                nbrClient = Integer.parseInt(nbPersons.getText());
                if ((nbrClient <= 0) || (nbrClient > hebergement.getNbrClient())) {
                    Helpers.showAlert("Error", "Le nombre de clients doit être > 0 et inférieur ou égale à la capacité maximale de l'hébergement", Alert.AlertType.ERROR);
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

            Timestamp checkin = Timestamp.valueOf(LocalDateTime.of(dateI.getValue(), LocalDateTime.now().toLocalTime()));
            Timestamp checkout = Timestamp.valueOf(LocalDateTime.of(dateO.getValue(), LocalDateTime.now().toLocalTime()));
            int nbPersonnes = Integer.parseInt(nbPersons.getText());

            ReservationHebergement reservation = new ReservationHebergement(checkin, checkout, owner, hebergement, nbPersonnes);
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
