package controllers;

import models.Hebergements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
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

    @FXML
    private Button btnAddRating;

    private Hebergements hebergement;

    @FXML
    void modifier(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateHeberg.fxml"));

            Parent root = loader.load();

            modifier.getScene().setRoot(root);


            UpdateHebergController controller = loader.getController();
            controller.setHebergementData(hebergement);

        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setHebergementDetails(Hebergements hebergement) {

        this.hebergement = hebergement;


        nomHebergLabel.setText(hebergement.getNomHeberg());
        descrpLabel.setText(hebergement.getDescrHeberg());
        adresseHebergLabel.setText(hebergement.getAdresse());
        typeHebergLabel.setText(hebergement.getTypeHeberg());
        nbrCLabel.setText(String.valueOf(hebergement.getNbrClient()));
        prixLabel.setText(String.valueOf(hebergement.getPrixHeberg()));

        // Affichage de l'image
        Image image = new Image(hebergement.getImageHebrg());
        imageHeberg.setImage(image);
    }

    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouterHeberg.fxml"));

            Parent root = loader.load();

            btnBack.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    // 📌 Retourner à la liste des hébergements après suppression
    private void retourAListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/listeHebergements.fxml"));
            Parent root = loader.load();
            btnBack.getScene().setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
