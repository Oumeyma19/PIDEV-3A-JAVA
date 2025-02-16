package models;

import java.util.Date;

public class Reservation {
    private int id;
    private int clientId;
    private int tourId;
    private String status;
    private Date reservationDate;


    public Reservation(int id, int clientId, int tourId, String status, java.sql.Date reservationDate) {
        this.id = id;
        this.clientId = clientId;
        this.tourId = tourId;
        this.status = status;
        this.reservationDate = reservationDate;
    }



    // Getters et Setters
    public java.sql.Date getDate() {return (java.sql.Date) reservationDate;}

    public void setDate(Date date) {this.reservationDate	 = date;}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getTourId() { return tourId; }
    public void setTourId(int tourId) { this.tourId = tourId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
