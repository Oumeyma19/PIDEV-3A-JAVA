package com.example.pidev.services;

import com.example.pidev.Exceptions.UserNotFoundException;
import com.example.pidev.interfaces.ICrud;
import com.example.pidev.models.ReservationHebergement;
import com.example.pidev.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservHebergService implements ICrud<ReservationHebergement> {

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

    @Override
    public Boolean ajouter(ReservationHebergement R) throws SQLException {
        String sql = "INSERT INTO reservationhebergement (idHeberg, idUser, reservationDate, statusHeberg) VALUES (?, ?, ?, ?)";
        PreparedStatement st = conn.prepareStatement(sql);

        st.setInt(1, R.getHebergements().getIdHebrg());
        st.setInt(2, R.getUser().getId());
        st.setTimestamp(3, R.getReservationDateHeberg());
        st.setBoolean(4, R.isStatusHeberg());

        st.executeUpdate();
        System.out.println("Réservation ajoutée avec succès !");

        return true;
    }

    @Override
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

    @Override
    public void modifier(ReservationHebergement R) {
        String sql = "UPDATE reservationhebergement SET idHeberg = ?, idUser = ?, reservationDate = ?, statusHeberg = ? WHERE reservationHeberg_id = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, R.getHebergements().getIdHebrg());
            st.setInt(2, R.getUser().getId());
            st.setTimestamp(3, R.getReservationDateHeberg());
            st.setBoolean(4, R.isStatusHeberg());
            st.setInt(5, R.getReservationHeberg_id());

            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réservation mise à jour avec succès !");
            } else {
                System.out.println("Aucune réservation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
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
            R.setReservationDateHeberg(rs.getTimestamp("reservationDate"));
            R.setStatusHeberg(rs.getBoolean("statusHeberg"));

            reservations.add(R);
        }
        return reservations;
    }

    @Override
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
            R.setReservationDateHeberg(rs.getTimestamp("reservationDate"));
            R.setStatusHeberg(rs.getBoolean("statusHeberg"));
        }

        return R;
    }
}
