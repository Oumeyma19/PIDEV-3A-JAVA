package services;

import models.Reservation;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private Connection connection;

    public ReservationService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // ✅ Ajouter une réservation
    public void addReservation(Reservation reservation) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reservationstours (client_id, tour_id, status, reservation_date) VALUES (?, ?, ?, ?)");
            ps.setInt(1, reservation.getClientId());
            ps.setInt(2, reservation.getTourId());
            ps.setString(3, reservation.getStatus());
            ps.setDate(4, reservation.getDate()); // Ensure reservation date is set
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ✅ Obtenir toutes les réservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reservationstours");
            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getInt("tour_id"),
                        rs.getString("status"),
                        rs.getDate("reservation_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // ✅ Supprimer une réservation
    public void deleteReservation(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM reservationstours WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateReservation(int id, String newStatus) {
        try {
            // Use the current date
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());

            // Update the reservation
            PreparedStatement ps = connection.prepareStatement("UPDATE reservationstours SET status = ?, reservation_date = ? WHERE id = ?");
            ps.setString(1, newStatus);
            ps.setDate(2, sqlDate);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}
