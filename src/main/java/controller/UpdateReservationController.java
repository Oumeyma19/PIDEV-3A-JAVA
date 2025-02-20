package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.ReservationOffre;
import services.ReservationOffreService;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateReservationController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField adultsField;
    @FXML private TextField childrenField;

    private ReservationOffre reservation;
    private final ReservationOffreService reservationService = new ReservationOffreService();

    public void setReservation(ReservationOffre reservation) {
        this.reservation = reservation;
        startDatePicker.setValue(reservation.getStartDate());
        endDatePicker.setValue(reservation.getEndDate());
        adultsField.setText(String.valueOf(reservation.getNumberOfAdults()));
        childrenField.setText(String.valueOf(reservation.getNumberOfChildren()));
    }

    @FXML
    private void updateReservation() {
        try {
            reservation.setStartDate(startDatePicker.getValue());
            reservation.setEndDate(endDatePicker.getValue());
            reservation.setNumberOfAdults(Integer.parseInt(adultsField.getText()));
            reservation.setNumberOfChildren(Integer.parseInt(childrenField.getText()));

            reservationService.modifier(reservation);
            showAlert("Succès", "Réservation mise à jour avec succès.");

            // Close the window
            Stage stage = (Stage) startDatePicker.getScene().getWindow();
            stage.close();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Erreur", "Impossible de modifier la réservation.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
