package models;

import java.util.Date;

public class ReservationTour {
    private int id;
    private User client;
    private Tour tour;
    private String status;
    private Date reservationDate;

    // Constructor with Tour object
    public ReservationTour(int id, User client, Tour tour, String status, Date reservationDate) {
        this.id = id;
        this.client = client;
        this.tour = tour;
        this.status = status;
        this.reservationDate = reservationDate;
    }

    // Constructor with clientId and tourId
    public ReservationTour(int id, int clientId, int tourId, String status, java.sql.Date reservationDate) {
        this.id = id;
        this.client = new User(clientId); // Initialize User with clientId
        this.tour = new Tour(tourId);    // Initialize Tour with tourId
        this.status = status;
        this.reservationDate = reservationDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    // Additional methods for PropertyValueFactory
    public int getClientId() {
        return client != null ? client.getId() : -1; // Return client ID or -1 if client is null
    }

    public int getTourId() {
        return tour != null ? tour.getId() : -1; // Return tour ID or -1 if tour is null
    }
}