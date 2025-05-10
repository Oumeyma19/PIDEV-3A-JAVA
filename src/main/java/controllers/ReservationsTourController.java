package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ReservationTour;
import models.Tour;
import services.ReservationTourService;

import java.sql.SQLException;
import java.util.List;

public class ReservationsTourController {

    @FXML private TableView<ReservationTour> reservationsTable;
    @FXML private TableColumn<ReservationTour, Integer> idColumn;
    @FXML private TableColumn<ReservationTour, Integer> clientIdColumn;
    @FXML private TableColumn<ReservationTour, String> statusColumn;
    @FXML private TableColumn<ReservationTour, String> dateColumn;

    private Tour selectedTour;
    private ReservationTourService reservationService = new ReservationTourService();

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
            List<ReservationTour> reservations = reservationService.getReservationsByTourId(selectedTour.getId());
            reservationsTable.getItems().clear();
            reservationsTable.getItems().addAll(reservations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}