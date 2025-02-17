package com.example.pidev.models;

import java.sql.Timestamp;

public class ReservationHebergement {
    private int reservationHeberg_id;
    private User user;
    private Hebergements hebergements;
    private Timestamp reservationDateHeberg;
    private boolean statusHeberg;

    public ReservationHebergement() {
    }

    public ReservationHebergement(Hebergements hebergements, User user, Timestamp reservationDateHeberg, int reservationHeberg_id, boolean statusHeberg) {
        this.hebergements = hebergements;
        this.user = user;
        this.reservationDateHeberg = reservationDateHeberg;
        this.reservationHeberg_id = reservationHeberg_id;
        this.statusHeberg = statusHeberg;
    }

    public ReservationHebergement(boolean statusHeberg, Timestamp reservationDateHeberg, User user, Hebergements hebergements) {
        this.statusHeberg = statusHeberg;
        this.reservationDateHeberg = reservationDateHeberg;
        this.user = user;
        this.hebergements = hebergements;
    }

    public Hebergements getHebergements() {
        return hebergements;
    }

    public User getUser() {
        return user;
    }

    public Timestamp getReservationDateHeberg() {
        return reservationDateHeberg;
    }

    public int getReservationHeberg_id() {
        return reservationHeberg_id;
    }

    public boolean isStatusHeberg() {
        return statusHeberg;
    }

    public void setHebergements(Hebergements hebergements) {
        this.hebergements = hebergements;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReservationDateHeberg(Timestamp reservationDateHeberg) {
        this.reservationDateHeberg = reservationDateHeberg;
    }

    public void setReservationHeberg_id(int reservationHeberg_id) {
        this.reservationHeberg_id = reservationHeberg_id;
    }

    public void setStatusHeberg(boolean statusHeberg) {
        this.statusHeberg = statusHeberg;
    }

    @Override
    public String toString() {
        return "ReservationHebergement{" +
                "idHeberg=" + hebergements +
                ", reservationHeberg_id=" + reservationHeberg_id +
                ", idUser=" + user +
                ", reservationDateHeberg=" + reservationDateHeberg +
                ", statusHeberg=" + statusHeberg +
                '}';
    }
}
