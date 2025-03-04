package controllers;

import Util.*;
import exceptions.UserNotFoundException;
import models.AvisHebergement;
import models.Hebergements;
import models.User;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
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
import javafx.scene.web.WebView;

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
    private Rating myRatingStars;

    @FXML
    private TextField myRatingTxt;

    @FXML
    private Button btnCancelRating;

    @FXML
    private WebView mapWebView;

    private Boolean isEditingRating = false;

    private AvisHebergement selectedAvis;

    private String apiKey = ConfigProperties.getProperty("google.maps.api.key");

    @FXML
    private ListView<AvisHebergement> avisListView;

    private final AvisService avisService = AvisService.getInstance();

    private Hebergements hebergement;

    private final ObservableList<AvisHebergement> avisList = FXCollections.observableArrayList();
    private User currentUser;

    private void onDeleteItem(AvisHebergement avis) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ton avis ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
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

    private void onUpdateItem(@NotNull AvisHebergement avis) {
        this.selectedAvis = avis;
        this.myRatingTxt.setText(avis.getComment());
        this.myRatingStars.setRating(avis.getReview());
        this.btnCancelRating.setVisible(true);
        this.isEditingRating = true;
    }

    public void setHebergementDetails(@NotNull Hebergements hebergement) {

        this.hebergement = hebergement;

        nomHebergLabel.setText(hebergement.getNomHeberg());
        descrpLabel.setText(hebergement.getDescrHeberg());
        adresseHebergLabel.setText(hebergement.getAdresse());
        typeHebergLabel.setText(hebergement.getTypeHeberg());
        nbrCLabel.setText(String.valueOf(hebergement.getNbrClient()) + " personnes");
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

        loadGoogleMap();
    }

    private void loadGoogleMap() {
        if (hebergement == null || hebergement.getAdresse() == null || hebergement.getAdresse().isEmpty()) {
            mapWebView.getEngine().loadContent("<html><body><p>No address available</p></body></html>");
            return;
        }

        try {
            // Use OpenStreetMap instead of Google Maps
            String encodedAddress = java.net.URLEncoder.encode(hebergement.getAdresse(), "UTF-8");

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\"/>\n" +
                    "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                    "    <style>\n" +
                    "        html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; }\n" +
                    "        .error-message { display: none; text-align: center; padding: 20px; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div id=\"map\"></div>\n" +
                    "    <div id=\"error-message\" class=\"error-message\">Could not locate the address.</div>\n" +
                    "    <script>\n" +
                    "        var map = L.map('map').setView([48.8566, 2.3522], 13); // Default to Paris\n" +
                    "        \n" +
                    "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                    "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                    "        }).addTo(map);\n" +
                    "        \n" +
                    "        // Use Nominatim to geocode the address\n" +
                    "        fetch('https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress + "')\n" +
                    "            .then(response => response.json())\n" +
                    "            .then(data => {\n" +
                    "                if (data && data.length > 0) {\n" +
                    "                    var lat = parseFloat(data[0].lat);\n" +
                    "                    var lon = parseFloat(data[0].lon);\n" +
                    "                    map.setView([lat, lon], 15);\n" +
                    "                    L.marker([lat, lon]).addTo(map)\n" +
                    "                        .bindPopup(\"" + hebergement.getNomHeberg().replace("\"", "\\\"") + "\")\n" +
                    "                        .openPopup();\n" +
                    "                } else {\n" +
                    "                    document.getElementById('error-message').style.display = 'block';\n" +
                    "                }\n" +
                    "            })\n" +
                    "            .catch(error => {\n" +
                    "                console.error('Error:', error);\n" +
                    "                document.getElementById('error-message').style.display = 'block';\n" +
                    "                document.getElementById('error-message').textContent = 'Error: ' + error.message;\n" +
                    "            });\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";

            mapWebView.getEngine().loadContent(htmlContent);

        } catch (Exception e) {
            e.printStackTrace();
            mapWebView.getEngine().loadContent("<html><body><p>Error: " + e.getMessage() + "</p></body></html>");
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
            ListesHebergController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);
            btnBack.getScene().setRoot(root);
        } catch (Exception ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    public void saveRating(ActionEvent event) {
        if (!isEditingRating) {
            try {
                avisService.ajouter(new AvisHebergement(myRatingTxt.getText(), (float) myRatingStars.getRating(), this.currentUser, hebergement));
                Helpers.showAlert("Avis", "ajout succes", Alert.AlertType.CONFIRMATION);
                fetchAvis();
            } catch (Exception e) {
                Helpers.showAlert("Avis", "ajout echec", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            try {
                avisService.modifier(new AvisHebergement(myRatingTxt.getText(), this.selectedAvis.getIdAvis(), this.selectedAvis.getHebergements(), this.selectedAvis.getUser(), (float) myRatingStars.getRating()));
                Helpers.showAlert("Avis", "modification succes", Alert.AlertType.CONFIRMATION);
                fetchAvis();

                resetRating(null);

            } catch (Exception e) {
                Helpers.showAlert("Avis", "modification echec", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void openReservationPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservationHebergForm.fxml"));
            Parent root = loader.load();
            ReservController reservController = loader.getController();

            // Ensure currentUser is set
            if (currentUser == null) {
                throw new IllegalStateException("Current user is not set. Please set the current user before opening the reservation page.");
            }
            reservController.setCurrentUser(currentUser); // Set the owner
            reservController.setData(hebergement); // Now setData can safely use the owner

            btnReservation.getScene().setRoot(root);
        } catch (Exception e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des hébergements.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void resetRating(ActionEvent event) {
        this.myRatingTxt.clear();
        this.myRatingStars.setRating(0);
        this.btnCancelRating.setVisible(false);
        this.selectedAvis = null;
        this.isEditingRating = false;
    }
}
