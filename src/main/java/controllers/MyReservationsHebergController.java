package controllers;

import Util.Helpers;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.AvisService;
import services.ReservHebergService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyReservationsHebergController implements Initializable {

    @FXML
    private FlowPane reservationsFlowPane;

    @FXML
    private Button retourr;
    private User currentUser;

    private final ReservHebergService reservHebergService = ReservHebergService.getInstance();

    private final ObservableList<ReservationHebergement> reservations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchData();

        for (ReservationHebergement reservationHebergement : reservations) {
            VBox hebergementContainer = createHebergementContainer(reservationHebergement);
            hebergementContainer.setId(reservationHebergement.getReservationHeberg_id() + "");
            reservationsFlowPane.getChildren().add(hebergementContainer);
        }
    }

    private void fetchData() {
        try {
            reservations.setAll(reservHebergService.getMyReservations(currentUser.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createHebergementContainer(ReservationHebergement reservationHebergement) {

        final Hebergements hebergement = reservationHebergement.getHebergements();

        VBox hebergementContainer = new VBox(10);
        hebergementContainer.setPadding(new Insets(10));
        hebergementContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        hebergementContainer.setMaxWidth(300);
        hebergementContainer.setMinWidth(300);

        if (hebergement.getImageHebrg() != null && !hebergement.getImageHebrg().isEmpty()) {
            ImageView imageView = new ImageView(new Image(hebergement.getImageHebrg()));
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            hebergementContainer.getChildren().add(imageView);
        }

        Text hebergementName = new Text(hebergement.getNomHeberg());
        hebergementName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        hebergementContainer.getChildren().add(hebergementName);

        VBox priceAndButtons = new VBox(10);
        priceAndButtons.setPadding(new Insets(5, 0, 5, 0));
        priceAndButtons.setAlignment(Pos.CENTER_LEFT);

        Text nbPersonnesText = new Text("Nb. Personnes: " + reservationHebergement.getNbPersonnes());
        nbPersonnesText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Text date1Text = new Text("Checkin: " + reservationHebergement.getDateCheckIn().toLocalDateTime().toLocalDate());
        date1Text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Text date2Text = new Text("Checkout: " + reservationHebergement.getDateCheckOut().toLocalDateTime().toLocalDate());
        date2Text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b9a9a;");

        Button consultButton = new Button("Annuler");
        consultButton.setStyle("-fx-background-color: #FA7335; -fx-text-fill: white; -fx-font-weight: bold;");
        consultButton.setOnAction(event -> onDeleteItem(reservationHebergement));


        priceAndButtons.getChildren().addAll(nbPersonnesText, date1Text, date2Text, consultButton);

        hebergementContainer.getChildren().add(priceAndButtons);

        return hebergementContainer;
    }

    @FXML
    void goBack(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));

            Parent root = loader.load();
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);
            retourr.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    private void onDeleteItem(ReservationHebergement reservationHebergement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'annulation");
        alert.setHeaderText("Voulez-vous vraiment annuler votre reservation ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    AvisService avisService = AvisService.getInstance();

                    if (!reservHebergService.supprimer(reservationHebergement.getReservationHeberg_id())) {
                        Helpers.showAlert("Succès", "Reservation annuler avec succès!", Alert.AlertType.INFORMATION);
                        reservations.removeIf(r -> r.getReservationHeberg_id() == reservationHebergement.getReservationHeberg_id());
                        reservationsFlowPane.getChildren().removeIf(vb -> vb.getId().equals("" + reservationHebergement.getReservationHeberg_id()));
                    } else {
                        Helpers.showAlert("Erreur", "Échec del'annulation de la reservation.", Alert.AlertType.ERROR);
                    }

                } catch (Exception e) {
                    Helpers.showAlert("Erreur", "Une erreur est survenue lors de l'annulation'.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
