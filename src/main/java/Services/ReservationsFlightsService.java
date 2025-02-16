package Services;

import Interfaces.IService;
import Models.ReservationsFlights;
import Tools.MyDataBase;

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
        String sql = "INSERT INTO reservation_flight (idResFlight, idClient, idFlight, seat_number, booking_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, r.getIdResFlight());
            pst.setInt(2, r.getIdClient());
            pst.setInt(3, r.getIdFlight());
            pst.setString(4, r.getSeat_number());
            pst.setDate(5, new java.sql.Date(r.getBooking_date().getTime()));
            pst.executeUpdate();
            System.out.println("Reservation added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(ReservationsFlights r) {
        String sql = "DELETE FROM reservation_flight WHERE idResFlight = ?";
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
        String sql = "UPDATE reservation_flight SET idClient = ?, idFlight = ?, seat_number = ?, booking_date = ? WHERE seat_number = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, r.getIdClient());
            pst.setInt(2, r.getIdFlight());
            pst.setString(3, r.getSeat_number());
            pst.setDate(4, new java.sql.Date(r.getBooking_date().getTime()));
            pst.setString(5, seatNumber);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reservation updated successfully.");
            } else {
                System.out.println("No reservation found with the given seat number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ReservationsFlights> afficher() {
        List<ReservationsFlights> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_flight";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ReservationsFlights reservation = new ReservationsFlights(
                        rs.getInt("idResFlight"),
                        rs.getInt("idClient"),
                        rs.getInt("idFlight"),
                        rs.getString("seat_number"),
                        rs.getDate("booking_date")
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
}
