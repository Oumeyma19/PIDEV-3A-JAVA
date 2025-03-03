package models;

import java.util.Date;

public class Reservation {
    private int id;
    private User client;
    private Tour tour;
    private String status;
    private Date reservationDate;

    // Constructor with Tour object
    public Reservation(int id, User client, Tour tour, String status, Date reservationDate) {
        this.id = id;
        this.client = client;
        this.tour = tour;
        this.status = status;
        this.reservationDate = reservationDate;
    }

    // Constructor with tourId (if needed)
    public Reservation(int id, User client, int tourId, String status, Date reservationDate) {
        this.id = id;
        this.client = client;
        this.tour = new Tour(); // Initialize a new Tour object with the given tourId
        this.tour.setId(tourId); // Set the tour ID
        this.status = status;
        this.reservationDate = reservationDate;
    }

    public Reservation(int id, int clientId, int tourId, String status, java.sql.Date reservationDate) {
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
}
