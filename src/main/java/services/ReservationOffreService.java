package services;

import models.Offre;
import models.ReservationOffre;
import models.User;
import tools.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationOffreService implements IService<ReservationOffre> {
    private Connection cnx;

    public ReservationOffreService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationOffre r) throws SQLException {
        String sql = "INSERT INTO reservation_offres (offer_id, startDate, end_date, status, id_user, number_of_adults, number_of_children) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, r.getOffre().getId());  // Offer ID
            stmt.setDate(2, Date.valueOf(r.getStartDate()));  // Start date
            stmt.setDate(3, Date.valueOf(r.getEndDate()));    // End date
            stmt.setString(4, r.getStatus());                 // Status
            stmt.setInt(5, r.getUser().getId());              // User ID
            stmt.setInt(6, r.getNumberOfAdults());            // Number of adults
            stmt.setInt(7, r.getNumberOfChildren());          // Number of children
            stmt.executeUpdate();
            System.out.println("Réservation ajoutée pour l'Offre ID: " + r.getOffre().getId());
        }
    }

    @Override
    public void supprimer(ReservationOffre r) throws SQLException {
        String sql = "DELETE FROM reservation_offres WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, r.getId());
            stmt.executeUpdate();
            System.out.println("Réservation supprimée");
        }
    }

    @Override
    public void modifier(ReservationOffre r) throws SQLException {
        String sql = "UPDATE reservation_offres SET offer_id = ?, startDate = ?, endDate = ?, status = ?, id_user = ?, " +
                "number_of_adults = ?, number_of_children = ? WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, r.getOffre().getId());  // Offer ID
            stmt.setDate(2, Date.valueOf(r.getStartDate()));  // Start date
            stmt.setDate(3, Date.valueOf(r.getEndDate()));    // End date
            stmt.setString(4, r.getStatus());                 // Status
            stmt.setInt(5, r.getUser().getId());              // User ID
            stmt.setInt(6, r.getNumberOfAdults());            // Number of adults
            stmt.setInt(7, r.getNumberOfChildren());          // Number of children
            stmt.setInt(8, r.getId());                        // Reservation ID
            stmt.executeUpdate();
            System.out.println("Réservation modifiée");
        }
    }

    @Override
    public List<ReservationOffre> recuperer() throws SQLException {
        String sql = "SELECT * FROM reservation_offres";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<ReservationOffre> reservations = new ArrayList<>();
        while (rs.next()) {
            ReservationOffre r = new ReservationOffre(
                    rs.getInt("id"),
                    new Offre(rs.getInt("offer_id")),  // Offer
                    rs.getDate("startDate").toLocalDate(),  // Start date
                    rs.getDate("endDate").toLocalDate(),    // End date
                    rs.getString("status"),                  // Status
                    new User(rs.getInt("id_user")),          // User
                    rs.getInt("numberOfAdults"),           // Number of adults
                    rs.getInt("numberOfChildren")          // Number of children
            );

            reservations.add(r);
        }
        return reservations;
    }

    // New method: Retrieve reservations for a specific user
    public List<ReservationOffre> recupererParUtilisateur(int userId) throws SQLException {
        String sql = "SELECT * FROM reservation_offres WHERE id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<ReservationOffre> reservations = new ArrayList<>();
            while (rs.next()) {
                ReservationOffre r = new ReservationOffre(
                        rs.getInt("id"),
                        new Offre(rs.getInt("offer_id")),  // Offer
                        rs.getDate("startDate").toLocalDate(),  // Start date
                        rs.getDate("endDate").toLocalDate(),    // End date
                        rs.getString("status"),                  // Status
                        new User(rs.getInt("id_user")),          // User
                        rs.getInt("numberOfAdults"),           // Number of adults
                        rs.getInt("numberOfChildren")          // Number of children
                );

                reservations.add(r);
            }
            return reservations;
        }
    }

    // New method: Cancel a reservation (update status to "Canceled")
    public void annulerReservation(int reservationId) throws SQLException {
        String sql = "UPDATE reservation_offres SET status = 'Canceled' WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
            System.out.println("Réservation annulée");
        }
    }
}