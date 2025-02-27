package services;

import exceptions.UserNotFoundException;
import models.ReservationHebergement;
import models.User;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservHebergService {

    private final Connection conn = MyConnection.getInstance().getConnection();

    private UserService userService = UserService.getInstance();
    private HebergementService hebService = HebergementService.getInstance();

    private static ReservHebergService instance;

    private ReservHebergService() {
    }

    public static ReservHebergService getInstance() {
        if (instance == null) {
            instance = new ReservHebergService();
        }
        return instance;
    }

    public Boolean ajouter(ReservationHebergement R) throws SQLException {
        String sql = "INSERT INTO reservationhebergement (idHeberg, idUser, dateCheckIn, dateCheckOut, nbPersonnes) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1, R.getHebergements().getIdHebrg());
        st.setInt(2, R.getUser().getId());
        st.setTimestamp(3, R.getDateCheckIn());
        st.setTimestamp(4, R.getDateCheckOut());
        st.setInt(5, R.getNbPersonnes());

        st.executeUpdate();
        System.out.println("Réservation ajoutée avec succès !");

        return true;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM reservationhebergement WHERE reservationHeberg_id = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Réservation supprimée avec succès !");
            } else {
                System.out.println("Aucune réservation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
        return false;
    }

    public List<ReservationHebergement> recuperer() throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM reservationhebergement";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<ReservationHebergement> reservations = new ArrayList<>();
        while (rs.next()) {
            ReservationHebergement R = new ReservationHebergement();
            R.setReservationHeberg_id(rs.getInt("reservationHeberg_id"));
            R.setUser(userService.getUserbyID(rs.getInt("idUser")));
            R.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));
            R.setDateCheckIn(rs.getTimestamp("dateCheckIn"));
            R.setDateCheckOut(rs.getTimestamp("dateCheckOut"));
            R.setNbPersonnes(rs.getInt("nbPersonnes"));
            reservations.add(R);
        }
        return reservations;
    }

    public List<ReservationHebergement> getMyReservations(int userId) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM reservationhebergement WHERE idUser = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery(sql);
        List<ReservationHebergement> reservations = new ArrayList<>();
        while (rs.next()) {
            ReservationHebergement R = new ReservationHebergement();
            R.setReservationHeberg_id(rs.getInt("reservationHeberg_id"));
            R.setUser(userService.getUserbyID(rs.getInt("idUser")));
            R.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));
            R.setDateCheckIn(rs.getTimestamp("dateCheckIn"));
            R.setDateCheckOut(rs.getTimestamp("dateCheckOut"));
            R.setNbPersonnes(rs.getInt("nbPersonnes"));
            reservations.add(R);
        }
        return reservations;
    }

    public ReservationHebergement recupererId(int id) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM reservationhebergement WHERE reservationHeberg_id = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        ReservationHebergement R = new ReservationHebergement();

        if (rs.next()) {
            R.setReservationHeberg_id(rs.getInt("reservationHeberg_id"));
            R.setUser(userService.getUserbyID(rs.getInt("idUser")));
            R.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));
            R.setDateCheckIn(rs.getTimestamp("dateCheckIn"));
            R.setDateCheckOut(rs.getTimestamp("dateCheckOut"));
            R.setNbPersonnes(rs.getInt("nbPersonnes"));
        }

        return R;
    }

    public Boolean existsBySameDates(Timestamp dateIn, Timestamp dateOut) throws SQLException {
        String sql = "select count(1) from reservationhebergement where ? between dateCheckIn and dateCheckOut or ? between dateCheckIn and dateCheckOut  ";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setTimestamp(1, dateIn);
        st.setTimestamp(2, dateOut);

        ResultSet rs = st.executeQuery();

        boolean exist = false;

        while (rs.next()) {
            exist = rs.getInt("count(1)") > 0;
        }

        return exist;
    }
}
