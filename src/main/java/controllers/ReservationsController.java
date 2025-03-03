package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Reservation;
import models.Tour;
import services.ReservationService;

import java.sql.SQLException;
import java.util.List;

public class ReservationsController {

    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, Integer> clientIdColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;

    private Tour selectedTour;
    private ReservationService reservationService = new ReservationService();

    public void setTour(Tour tour) {
        this.selectedTour = tour;
        loadReservations();
    }

    @FXML
    public void initialize() {
        // Initialize TableView columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
    }

    private void loadReservations() {
        if (selectedTour == null) return;

        try {
            List<Reservation> reservations = reservationService.getReservationsByTourId(selectedTour.getId());
            reservationsTable.getItems().clear();
            reservationsTable.getItems().addAll(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}