package com.example.pidev.controllers;

import com.example.pidev.models.Hebergements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailHebergController {

    @FXML
    private Label nomHebergLabel;

    @FXML
    private Label descrpLabel;

    @FXML
    private Label adresseHebergLabel;

    @FXML
    private Label typeHebergLabel;

    @FXML
    private Label nbrCLabel;

    @FXML
    private Label prixLabel;

    @FXML
    private Label dateICLabel;

    @FXML
    private Label dateOCLabel;

    @FXML
    private Button btnBack;

    @FXML
    private ImageView imageHeberg;

    @FXML
    private Button modifier;

    private Hebergements hebergement;

    @FXML
    void modifier(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/updateHeberg.fxml"));

            Parent root = loader.load();

            modifier.getScene().setRoot(root);


            UpdateHebergController controller = loader.getController();
            controller.setHebergementData(hebergement);

        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    // Méthode pour afficher les détails de l'hébergement
    // Méthode pour afficher les détails de l'hébergement
    public void setHebergementDetails(Hebergements hebergement) {

        this.hebergement = hebergement;


        nomHebergLabel.setText(hebergement.getNomHeberg());
        descrpLabel.setText(hebergement.getDescrHeberg());
        adresseHebergLabel.setText(hebergement.getAdresse());
        typeHebergLabel.setText(hebergement.getTypeHeberg());
        nbrCLabel.setText(String.valueOf(hebergement.getNbrClient()));
        prixLabel.setText(String.valueOf(hebergement.getPrixHeberg()));

        // Affichage des dates
        Timestamp dateCheckin = hebergement.getDateCheckin();
        Timestamp dateCheckout = hebergement.getDateCheckout();
        dateICLabel.setText(dateCheckin.toString());
        dateOCLabel.setText(dateCheckout.toString());

        // Affichage de l'image
        Image image = new Image(hebergement.getImageHebrg());
        imageHeberg.setImage(image);
    }

    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ajouterHeberg.fxml"));

            Parent root = loader.load();

            btnBack.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    // 📌 Retourner à la liste des hébergements après suppression
    private void retourAListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/listeHebergements.fxml"));
            Parent root = loader.load();
            btnBack.getScene().setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // 📌 Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
