package models;

import java.util.Date;

public class ReservationsFlights {
    private int id;
    private User user;
    private Flight flight;
    private Date booking_date;

    public ReservationsFlights(int id, User user, Flight flight, Date booking_date) {
        this.id = id;
        this.user = user;
        this.flight = flight;

        this.booking_date = booking_date;
    }




    public ReservationsFlights() {
    }

    public ReservationsFlights(User user, Flight flight, Date bookingDate) {
        this.user = user;
        this.flight = flight;
        this.booking_date = bookingDate;
    }

    public int getIdResFlight() {
        return id;
    }

    public void setIdResFlight(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }



    public Date getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(Date booking_date) {
        this.booking_date = booking_date;
    }

    @Override
    public String toString() {
        return "ReservationFlight{" + "id=" + id + "," +
                " idClient=" + user.getId() + ", idFlight=" + flight.getIdFlight() + "" +
                  ", booking_date=" + booking_date + '}';
    }






}
