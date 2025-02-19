package com.example.pidev.controllers;

import com.example.pidev.Exceptions.UserNotFoundException;
import com.example.pidev.Util.AvisListCell;
import com.example.pidev.Util.AvisProperties;
import com.example.pidev.Util.Helpers;
import com.example.pidev.Util.RatingDialog;
import com.example.pidev.models.AvisHebergement;
import com.example.pidev.models.Hebergements;
import com.example.pidev.models.User;
import com.example.pidev.services.AvisService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.Rating;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailHebergCController {

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
    private ListView<AvisHebergement> avisListView;

    private Hebergements hebergement;

    private final ObservableList<AvisHebergement> avisList = FXCollections.observableArrayList();

    private final AvisService avisService = AvisService.getInstance();

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

        avisListView.setCellFactory(param -> new AvisListCell());

        try {
            fetchAvis();
            avisListView.setItems(avisList);
        } catch (Exception e) {
            e.printStackTrace();
            avisListView.getItems().add(null);
        }
    }

    private void fetchAvis() throws SQLException, UserNotFoundException {
        avisList.setAll(avisService
                .recupererParHebergement(hebergement.getIdHebrg()));
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


    //gfgsfg
    private void retourAListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/listeHebergements.fxml"));
            Parent root = loader.load();
            btnBack.getScene().setRoot(root);
        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    public void openRatingDialog(ActionEvent event) {
        final RatingDialog dialog = new RatingDialog(new AvisProperties("", 0.0f));
        Optional<AvisProperties> result = dialog.showAndWait();
        if (result.isPresent()) {
            AvisProperties avis = result.get();
            try {
                avisService.ajouter(new AvisHebergement(avis.getComment(), avis.getRating(), new User(7, null, null, null, null, null, null), hebergement));
                Helpers.showAlert("Avis", "ajout succes", Alert.AlertType.CONFIRMATION);
                fetchAvis();
            } catch (Exception e) {
                Helpers.showAlert("Avis", "ajout echec", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }


    }

    @FXML
    public void openReservationPage(ActionEvent event) {
    }
}
