package services;

import interfaces.iCrud;
import models.Hebergements;
import models.ReservationHebergement;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservHebergService implements iCrud<ReservationHebergement> {

    private final Connection conn = MyConnection.getInstance().getConn();

    @Override
    public void ajouter(ReservationHebergement R) throws SQLException {
        String sql = "INSERT INTO reservationhebergement (idHeberg, idUser, reservationDate, statusHeberg) VALUES (?, ?, ?, ?)";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setObject(1, R.getIdHeberg());
        st.setObject(2, R.getIdUser());
        st.setTimestamp(3,R.getReservationDateHeberg());
        st.setBoolean(4, R.isStatusHeberg());

        st.executeUpdate();
        System.out.println("Réservation ajoutée avec succès !");
    }

    @Override
    public void supprimer(int id) {
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
    }

    @Override
    public void modifier(ReservationHebergement R) {
        String sql = "UPDATE reservationhebergement SET idHeberg = ?, idUser = ?, reservationDate = ?, statusHeberg = ? WHERE reservationHeberg_id = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setObject(1, R.getIdHeberg());
            st.setObject(2, R.getIdUser());
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
    public List<ReservationHebergement> recuperer() throws SQLException {
        String sql = "SELECT * FROM reservationhebergement";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<ReservationHebergement> reservations = new ArrayList<>();
        while (rs.next()) {
            ReservationHebergement R = new ReservationHebergement();
            R.setReservationHeberg_id(rs.getInt("reservationHeberg_id"));
            R.setIdHeberg((Hebergements) rs.getObject("idHeberg"));
            R.setIdUser((User) rs.getObject("idUser"));
            R.setReservationDateHeberg(rs.getTimestamp("reservationDate"));
            R.setStatusHeberg(rs.getBoolean("statusHeberg"));

            reservations.add(R);
        }
        return reservations;
    }

    @Override
    public ReservationHebergement recupererId(int id) throws SQLException {
        String sql = "SELECT * FROM reservationhebergement WHERE reservationHeberg_id = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);

        ResultSet rs = st.executeQuery();
        ReservationHebergement R = new ReservationHebergement();

        if (rs.next()) {
            R.setReservationHeberg_id(rs.getInt("reservationHeberg_id"));
            R.setIdHeberg((Hebergements) rs.getObject("idHeberg"));
            R.setIdUser((User) rs.getObject("idUser"));
            R.setReservationDateHeberg(rs.getTimestamp("reservationDate"));
            R.setStatusHeberg(rs.getBoolean("statusHeberg"));
        }

        return R;
    }
}
