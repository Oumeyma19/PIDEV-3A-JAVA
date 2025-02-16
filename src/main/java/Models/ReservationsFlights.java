package Models;

import java.util.Date;

public class ReservationsFlights {
    private int idResFlight;
    private int idClient;
    private int idFlight;
    private String seat_number;
    private Date booking_date;

    public ReservationsFlights(int idResFlight, int idClient, int idFlight, String seat_number, Date booking_date) {
        this.idResFlight = idResFlight;
        this.idClient = idClient;
        this.idFlight = idFlight;
        this.seat_number = seat_number;
        this.booking_date = booking_date;
    }



    public ReservationsFlights( int idClient, int idFlight, String seat_number, Date booking_date) {
        this.idClient = idClient;
        this.idFlight = idFlight;
        this.seat_number = seat_number;
        this.booking_date = booking_date;
    }



    public ReservationsFlights() {
    }

    public int getIdResFlight() {
        return idResFlight;
    }

    public void setIdResFlight(int idResFlight) {
        this.idResFlight = idResFlight;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdFlight() {
        return idFlight;
    }

    public void setIdFlight(int idFlight) {
        this.idFlight = idFlight;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public Date getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(Date booking_date) {
        this.booking_date = booking_date;
    }

    @Override
    public String toString() {
        return "ReservationFlight{" + "idResFlight=" + idResFlight + "," +
                " idClient=" + idClient + ", idFlight=" + idFlight + "" +
                ", seat_number=" + seat_number + ", booking_date=" + booking_date + '}';
    }






}
