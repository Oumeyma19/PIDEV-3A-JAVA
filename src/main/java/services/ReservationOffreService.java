package services;

import models.Offre;
import models.ReservationOffre;
import models.User;
import tools.MyDataBase;
import util.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationOffreService implements IService<ReservationOffre> {
    private Connection cnx;

    public ReservationOffreService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationOffre r) throws SQLException {
        String sql = "INSERT INTO reservation_offres (offer_id, startDate, endDate, status, id_user, numberOfAdults, numberOfChildren) " +
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
                "numberOfAdults = ?, numberOfChildren = ? WHERE id = ?";

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
        return List.of();
    }
    public List<ReservationOffre> getReservationsByUser(int id_user) throws SQLException {
        String sql = "SELECT * FROM reservation_offres WHERE id_user = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, id_user);
            ResultSet rs = stmt.executeQuery();

            List<ReservationOffre> reservations = new ArrayList<>();
            while (rs.next()) {
                ReservationOffre r = new ReservationOffre(
                        rs.getInt("id"),
                        new Offre(rs.getInt("offer_id")),  // Offer
                        rs.getDate("startDate").toLocalDate(),  // Start date
                        rs.getDate("endDate").toLocalDate(),    // End date
                        rs.getString("status"),                  // Status
                        new User(rs.getInt("id_user"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("phone"), rs.getString("password"), rs.getString("nivfid"), Type.CLIENT, rs.getBoolean("is_banned"), rs.getBoolean("is_active")), // User
                        rs.getInt("numberOfAdults"),           // Number of adults
                        rs.getInt("numberOfChildren")          // Number of children
                );

                reservations.add(r);
            }
            return reservations;
        }
    }


    @Override
    public void modifier2(ReservationOffre p, String s) throws SQLException {

    }

    // New method: Retrieve reservations for a specific user
    public List<ReservationOffre> recupererParUtilisateur(int userId) throws SQLException {
        String sql = """
        SELECT r.*, u.firstname, u.lastname, u.email, u.phone, u.password, u.nivfid, 
               u.is_banned, u.is_active
        FROM reservation_offres r
        JOIN user u ON r.id_user = u.id
        WHERE r.id_user = ?
    """;

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<ReservationOffre> reservations = new ArrayList<>();
            while (rs.next()) {
                ReservationOffre r = new ReservationOffre(
                        rs.getInt("id"),                               // Reservation ID
                        new Offre(rs.getInt("offer_id")),              // Offer object (needs full data later)
                        rs.getDate("startDate").toLocalDate(),         // Start date
                        rs.getDate("endDate").toLocalDate(),           // End date
                        rs.getString("status"),                        // Status
                        new User(                                       // User object
                                rs.getInt("id_user"),
                                rs.getString("firstname"),
                                rs.getString("lastname"),
                                rs.getString("email"),
                                rs.getString("phone"),
                                rs.getString("password"),
                                rs.getString("nivfid"),
                                Type.CLIENT,
                                rs.getBoolean("is_banned"),
                                rs.getBoolean("is_active")
                        ),
                        rs.getInt("numberOfAdults"),                   // Number of adults
                        rs.getInt("numberOfChildren")                  // Number of children
                );

                reservations.add(r);
            }
            return reservations;
        }
    }


    // New method: Cancel a reservation (update status to "Canceled")
    public void annulerReservation(int id) throws SQLException {
        String query = "DELETE FROM reservation_offres WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

}