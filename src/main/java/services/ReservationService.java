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

    // ✅ Add a reservation and increment nbPlaceReserver in the tour table
    public void addReservation(Reservation reservation) {
        try {
            if (reservation.getClient() == null) {
                System.out.println("Client is null in reservation object!");
                return;
            }

            if (reservation.getTour() == null) {
                System.out.println("Tour is null in reservation object!");
                return;
            }

            // Start a transaction
            connection.setAutoCommit(false);

            // Step 1: Insert the reservation into the reservationstours table
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reservationstours (client_id, tour_id, status, reservation_date) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, reservation.getClient().getId());  // Get client ID from User
            ps.setInt(2, reservation.getTour().getId());    // Get tour ID from Tour
            ps.setString(3, reservation.getStatus());      // Set reservation status
            ps.setDate(4, new java.sql.Date(reservation.getReservationDate().getTime())); // Set reservation date
            ps.executeUpdate();

            // Step 2: Increment nbPlaceReserver in the tour table
            PreparedStatement updateTour = connection.prepareStatement(
                    "UPDATE tours SET nbPlaceReserver = nbPlaceReserver + 1 WHERE id = ?"
            );
            updateTour.setInt(1, reservation.getTour().getId());
            updateTour.executeUpdate();

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                // Reset auto-commit to true
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ Get all reservations
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

    // ✅ Check if a user has reserved a specific tour
    public boolean hasUserReservedTour(int clientId, int tourId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM reservationstours WHERE client_id = ? AND tour_id = ? AND status = 'confirmed'"
            );
            ps.setInt(1, clientId);
            ps.setInt(2, tourId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Delete a reservation
    public void deleteReservation(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM reservationstours WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Update a reservation's status
    public void updateReservation(int id, String newStatus) {
        try {
            // Use the current date
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());

            // Update the reservation
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE reservationstours SET status = ?, reservation_date = ? WHERE id = ?"
            );
            ps.setString(1, newStatus);
            ps.setDate(2, sqlDate);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservationsByTourId(int tourId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservationstours WHERE tour_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tourId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            reservations.add(new Reservation(
                    rs.getInt("id"),
                    rs.getInt("client_id"),
                    rs.getInt("tour_id"),
                    rs.getString("status"),
                    rs.getDate("reservation_date")
            ));
        }
        return reservations;
    }
}