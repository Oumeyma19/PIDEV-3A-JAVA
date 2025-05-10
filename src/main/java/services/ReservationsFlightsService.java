package services;

import interfaces.IService;
import models.*;
import tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationsFlightsService implements IService<ReservationsFlights> {
    private Connection cnx;

    public ReservationsFlightsService() {
        cnx = MyDataBase.getInstance().getCnx();
    }



    @Override
    public void ajouter(ReservationsFlights r) {
        String sql = "INSERT INTO reservationsflights (id, idClient, idFlight,  booking_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, r.getIdResFlight());
            pst.setInt(2, r.getUser().getId());
            pst.setInt(3, r.getFlight().getIdFlight());
            pst.setDate(4, new java.sql.Date(r.getBooking_date().getTime()));
            pst.executeUpdate();
            System.out.println("Reservation added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(ReservationsFlights r) {
        String sql = "DELETE FROM reservationsflights WHERE id= ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, r.getIdResFlight());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Reservation deleted successfully.");
            } else {
                System.out.println("No reservation found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(ReservationsFlights r, String seatNumber) {

    }

    @Override
    public List<ReservationsFlights> afficher() {
        List<ReservationsFlights> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservationsflights";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ReservationsFlights reservation = new ReservationsFlights();
                reservation.setIdResFlight(rs.getInt("id"));
                reservation.setUser(new User(rs.getInt("idClient")));
                reservation.setFlight(new Flight(rs.getInt("idFlight")));
                reservation.setBooking_date(rs.getDate("booking_date"));
                reservations.add(reservation);




            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public void modifier(Offre o) throws SQLException {

    }

    @Override
    public void modifier(Hebergements H) {

    }

    @Override
    public List<ReservationsFlights> recuperer() throws SQLException {
        return List.of();
    }

    @Override
    public void modifier2(Hebergements p, String s) throws SQLException {

    }

    public List<ReservationsFlights> getReservationsByUserId(int userId) {
        List<ReservationsFlights> userReservations = new ArrayList<>();
        String sql = "SELECT rf.*, f.*, u.* FROM reservationsflights rf " +
                "LEFT JOIN flight f ON rf.idFlight = f.id " +
                "LEFT JOIN user u ON rf.idClient = u.id " +
                "WHERE rf.idClient = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Create User object
                    User user = new User(
                            rs.getInt("idClient")
                            // Add more fields as needed
                    );

                    // Create Flight object
                    Flight flight = new Flight(
                            rs.getInt("idFlight")
                    );

                    // Create Reservation object
                    ReservationsFlights reservation = new ReservationsFlights();
                    reservation.setIdResFlight(rs.getInt("id"));
                    reservation.setUser(user);
                    reservation.setFlight(flight);
                    reservation.setBooking_date(rs.getDate("booking_date"));

                    userReservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations for user: " + userId);
            e.printStackTrace();
        }

        return userReservations;
    }
}