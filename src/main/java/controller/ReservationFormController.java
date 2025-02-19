package controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Offre;
import models.ReservationOffre;
import models.User;
import services.ReservationOffreService;
import services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationFormController {
    @FXML private VBox root; // Root is now VBox
    @FXML private VBox formCard; // Form card is VBox
    @FXML private ImageView imgOffer;
    @FXML private Label lblOfferName;
    @FXML private Label lblOfferDescription;
    @FXML private Spinner<Integer> spAdults;
    @FXML private Spinner<Integer> spChildren;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private Label lblTotalPrice;
    @FXML private Button btnConfirm;
    @FXML private Label lblStatus;

    private Offre selectedOffer;
    private final ReservationOffreService reservationService = new ReservationOffreService();
    private final UserService userService = new UserService();
    private User loggedInUser;

    @FXML
    public void initialize() {
        // Initialize spinners
        spAdults.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        spChildren.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));

        // Add listeners to update the total price
        spAdults.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        spChildren.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        dpStartDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        dpEndDate.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        fetchLoggedInUser();
        animateForm();
    }

    private void fetchLoggedInUser() {
        try {
            loggedInUser = userService.getUserById(6); // Replace with actual logged-in user ID
            if (loggedInUser == null) {
                showError("Erreur : Utilisateur introuvable !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de la récupération de l'utilisateur !");
        }
    }

    public void setSelectedOffer(Offre offer) {
        this.selectedOffer = offer;
        lblOfferName.setText(offer.getTitle());
        lblOfferDescription.setText(offer.getDescription());
        imgOffer.setImage(new Image(offer.getImagePath())); // Set the offer image
        updateTotalPrice(); // Update the price when the offer is set
    }
    @FXML
    private void goToReservationList() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reservation_list.fxml"));
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateTotalPrice() {
        if (selectedOffer == null || dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
            return;
        }

        // Calculate the number of nights
        long nights = ChronoUnit.DAYS.between(dpStartDate.getValue(), dpEndDate.getValue());
        if (nights < 0) {
            lblTotalPrice.setText("Dates invalides");
            return;
        }

        // Calculate the total price
        int adults = spAdults.getValue();
        int children = spChildren.getValue();
        double pricePerNight = selectedOffer.getPrice();
        double totalPrice = nights * pricePerNight * (adults + children * 0.5); // Children at half price

        lblTotalPrice.setText(String.format("Prix total: %.2f €", totalPrice));
    }

    @FXML
    public void confirmReservation() {
        if (selectedOffer == null || loggedInUser == null) {
            showError("Offre ou utilisateur invalide !");
            return;
        }

        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            showError("Dates invalides !");
            return;
        }

        ReservationOffre reservation = new ReservationOffre(
                selectedOffer, startDate, endDate, "Pending", loggedInUser,
                spAdults.getValue(), spChildren.getValue()
        );

        try {
            reservationService.ajouter(reservation);
            showSuccess("Réservation En attente !");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de la réservation. Veuillez réessayer.");
        }
    }

    private void showError(String message) {
        lblStatus.setText(message);
        lblStatus.setStyle("-fx-text-fill: #e74c3c;");
        lblStatus.setVisible(true);
    }

    private void showSuccess(String message) {
        lblStatus.setText(message);
        lblStatus.setStyle("-fx-text-fill: #2ecc71;");
        lblStatus.setVisible(true);
    }

    private void animateForm() {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), formCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}