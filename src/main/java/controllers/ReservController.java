package controllers;

import Util.Helpers;
import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.ReservHebergService;
import services.UserService;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservController {

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

    private Hebergements hebergement;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setData(Hebergements hebergement) {
        if (this.currentUser == null) {
            throw new IllegalStateException("Current user is not set. Please set the current user before calling setData.");
        }
        this.hebergement = hebergement;
        nomClient.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
    }

    @FXML
    void submitReservation(ActionEvent event) {
        try {
            // Validate input fields
            if (nbPersons.getText().isEmpty() || dateI.getValue() == null || dateO.getValue() == null) {
                Helpers.showAlert("Error", "Veuillez remplir tous les champs!", Alert.AlertType.ERROR);
                return;
            }

            // Validate number of persons
            int nbrClient;
            try {
                nbrClient = Integer.parseInt(nbPersons.getText());
                if (nbrClient <= 0 || nbrClient > hebergement.getNbrClient()) {
                    Helpers.showAlert("Error", "Le nombre de clients doit être > 0 et inférieur ou égal à la capacité maximale de l'hébergement", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                Helpers.showAlert("Error", "Veuillez entrer un nombre valide pour les clients!", Alert.AlertType.ERROR);
                return;
            }

            // Validate dates
            Timestamp dateCheckin = Timestamp.valueOf(dateI.getValue().atStartOfDay());
            Timestamp dateCheckout = Timestamp.valueOf(dateO.getValue().atStartOfDay());

            if (dateCheckin.after(dateCheckout)) {
                Helpers.showAlert("Error", "La date de sortie doit être après la date d'entrée!", Alert.AlertType.ERROR);
                return;
            }

            // Create and save the reservation
            ReservationHebergement reservation = new ReservationHebergement(dateCheckin, dateCheckout, currentUser, hebergement, nbrClient);
            ReservHebergService reservService = ReservHebergService.getInstance();
            reservService.ajouter(reservation);

            Helpers.showAlert("Succès", "Réservation ajoutée avec succès !", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            Helpers.showAlert("Erreur", "Impossible d'ajouter la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            // Load the FXML file first
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load(); // Load the FXML file

            // Get the controller after loading the FXML
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser); // Set the current user

            // Set the new scene
            retour.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(ReservController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}