package com.example.pidev.models;

import java.sql.Timestamp;

public class ReservationHebergement {
    private int reservationHeberg_id;
    private User user;
    private Hebergements hebergements;
    private Timestamp dateCheckIn;
    private Timestamp dateCheckOut;
    private Integer nbPersonnes;

    public ReservationHebergement() {
    }

    public ReservationHebergement(Hebergements hebergements, User user, Timestamp dateCheckIn, Timestamp dateCheckOut, int reservationHeberg_id, Integer nbPersonnes) {
        this.hebergements = hebergements;
        this.user = user;
        this.dateCheckIn = dateCheckIn;
        this.dateCheckOut = dateCheckOut;
        this.reservationHeberg_id = reservationHeberg_id;
        this.nbPersonnes = nbPersonnes;
    }

    public ReservationHebergement(Timestamp dateCheckIn, Timestamp dateCheckOut, User user, Hebergements hebergements, Integer nbPersonnes) {
        this.dateCheckIn = dateCheckIn;
        this.dateCheckOut = dateCheckOut;
        this.user = user;
        this.hebergements = hebergements;
        this.nbPersonnes = nbPersonnes;
    }

    @Override
    public String toString() {
        return "ReservationHebergement{" +
                "dateCheckin=" + dateCheckIn +
                ", reservationHeberg_id=" + reservationHeberg_id +
                ", user=" + user +
                ", hebergements=" + hebergements +
                ", dateCheckout=" + dateCheckOut +
                ", nbPersonnes=" + nbPersonnes +
                '}';
    }

    public Timestamp getDateCheckIn() {
        return dateCheckIn;
    }

    public void setDateCheckIn(Timestamp dateCheckIn) {
        this.dateCheckIn = dateCheckIn;
    }

    public Timestamp getDateCheckOut() {
        return dateCheckOut;
    }

    public void setDateCheckOut(Timestamp dateCheckOut) {
        this.dateCheckOut = dateCheckOut;
    }

    public Hebergements getHebergements() {
        return hebergements;
    }

    public void setHebergements(Hebergements hebergements) {
        this.hebergements = hebergements;
    }

    public Integer getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(Integer nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public int getReservationHeberg_id() {
        return reservationHeberg_id;
    }

    public void setReservationHeberg_id(int reservationHeberg_id) {
        this.reservationHeberg_id = reservationHeberg_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
