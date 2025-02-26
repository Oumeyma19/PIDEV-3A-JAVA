package controllers;

import exceptions.UserNotFoundException;
import Util.AvisListCell;
import Util.AvisProperties;
import Util.Helpers;
import Util.RatingDialog;
import models.AvisHebergement;
import models.Hebergements;
import models.User;
import services.AvisService;
import services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
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
    private Button btnBack;

    @FXML

    private Button btnReservation;

    @FXML
    private ImageView imageHeberg;

    @FXML
    private ListView<AvisHebergement> avisListView;

    private final UserService userService = UserService.getInstance();

    private Hebergements hebergement;

    private final ObservableList<AvisHebergement> avisList = FXCollections.observableArrayList();

    private final AvisService avisService = AvisService.getInstance();

    private void onDeleteItem(AvisHebergement avis) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ton avis ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AvisService avisService = AvisService.getInstance();

                    if (!avisService.supprimer(avis.getIdAvis())) {
                        Helpers.showAlert("Succès", "Hébergement supprimé avec succès!", Alert.AlertType.INFORMATION);
                        fetchAvis();
                    } else {
                        Helpers.showAlert("Erreur", "Échec de la suppression de l'hébergement.", Alert.AlertType.ERROR);
                    }

                } catch (Exception e) {
                    Helpers.showAlert("Erreur", "Une erreur est survenue lors de la suppression.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    private void onUpdateItem(AvisHebergement avis) {
        final RatingDialog dialog = new RatingDialog(new AvisProperties(avis.getComment(), avis.getReview()));
        Optional<AvisProperties> result = dialog.showAndWait();
        if (result.isPresent()) {
            AvisProperties avisProperties = result.get();
            try {
                avisService.modifier(new AvisHebergement(avisProperties.getComment(), avis.getIdAvis(), avis.getHebergements(), avis.getUser(), avisProperties.getRating()));
                Helpers.showAlert("Avis", "modification succes", Alert.AlertType.CONFIRMATION);
                fetchAvis();
            } catch (Exception e) {
                Helpers.showAlert("Avis", "modification echec", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
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

        avisListView.setCellFactory(param -> new AvisListCell(this::onDeleteItem, this::onUpdateItem));

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/listesHeberg.fxml"));

            Parent root = loader.load();

            btnBack.getScene().setRoot(root);
        } catch (Exception ex) {
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
                avisService.ajouter(new AvisHebergement(avis.getComment(), avis.getRating(), UserService.getLoggedInUser(), hebergement));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservationHebergForm.fxml"));
            Parent root = loader.load();
            btnReservation.getScene().setRoot(root);

            ReservController reservController = loader.getController();

            reservController.setData(hebergement, userService.getUserbyID(7));

        } catch (Exception e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des hébergements.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


}
