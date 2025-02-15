package models;

import java.sql.Timestamp;

public class ReservationHebergement {
    private int reservationHeberg_id;
    private User idUser;
    private Hebergements idHeberg;
    private Timestamp reservationDateHeberg;
    private boolean statusHeberg;

    public ReservationHebergement() {
    }

    public ReservationHebergement(Hebergements idHeberg, User idUser, Timestamp reservationDateHeberg, int reservationHeberg_id, boolean statusHeberg) {
        this.idHeberg = idHeberg;
        this.idUser = idUser;
        this.reservationDateHeberg = reservationDateHeberg;
        this.reservationHeberg_id = reservationHeberg_id;
        this.statusHeberg = statusHeberg;
    }

    public ReservationHebergement(boolean statusHeberg, Timestamp reservationDateHeberg, User idUser, Hebergements idHeberg) {
        this.statusHeberg = statusHeberg;
        this.reservationDateHeberg = reservationDateHeberg;
        this.idUser = idUser;
        this.idHeberg = idHeberg;
    }

    public Hebergements getIdHeberg() {
        return idHeberg;
    }

    public User getIdUser() {
        return idUser;
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

    public void setIdHeberg(Hebergements idHeberg) {
        this.idHeberg = idHeberg;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
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
                "idHeberg=" + idHeberg +
                ", reservationHeberg_id=" + reservationHeberg_id +
                ", idUser=" + idUser +
                ", reservationDateHeberg=" + reservationDateHeberg +
                ", statusHeberg=" + statusHeberg +
                '}';
    }
}
