package com.example.pidev.services;

import com.example.pidev.Exceptions.UserNotFoundException;
import com.example.pidev.interfaces.ICrud;
import com.example.pidev.models.AvisHebergement;
import com.example.pidev.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements ICrud<AvisHebergement> {

    private final Connection conn = MyConnection.getInstance().getConnection();
    private UserService userService = UserService.getInstance();
    private HebergementService hebService = HebergementService.getInstance();

    private static AvisService instance;

    private AvisService() {
    }

    public static AvisService getInstance() {
        if (instance == null) {
            instance = new AvisService();
        }
        return instance;
    }

    @Override
    public Boolean ajouter(AvisHebergement avis) throws SQLException {
        String sql = "INSERT INTO avishebergement(comment, review, idUser, idHeberg) VALUES(?, ?, ?, ?)";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, avis.getComment());
        st.setFloat(2, avis.getReview());
        st.setInt(3, avis.getUser().getId());
        st.setInt(4, avis.getHebergements().getIdHebrg());

        st.executeUpdate();
        System.out.println("Avis ajouté avec succès");

        return true;
    }

    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM avisHebergement WHERE idAvis = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Avis supprimé avec succès !");
            } else {
                System.out.println("Aucun avis trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
        return false;
    }

    @Override
    public void modifier(AvisHebergement avis) {
        String sql = "UPDATE avisHebergement SET comment = ?, review = ?, idUser = ?, idHeberg = ? WHERE idAvis = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, avis.getComment());
            st.setFloat(2, avis.getReview());
            st.setInt(3, avis.getUser().getId());
            st.setInt(4, avis.getHebergements().getIdHebrg());
            st.setInt(5, avis.getIdAvis());

            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Avis mis à jour avec succès !");
            } else {
                System.out.println("Aucun avis trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public List<AvisHebergement> recuperer() throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM avisHebergement";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<AvisHebergement> avisList = new ArrayList<>();
        while (rs.next()) {
            AvisHebergement avis = new AvisHebergement();
            avis.setIdAvis(rs.getInt("idAvis"));
            avis.setComment(rs.getString("comment"));
            avis.setReview(rs.getFloat("review"));
            avis.setUser(userService.getUserbyID(rs.getInt("idUser")));
            avis.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));

            avisList.add(avis);
        }
        return avisList;
    }

    @Override
    public AvisHebergement recupererId(int id) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM avisHebergement WHERE idAvis = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        AvisHebergement avis = new AvisHebergement();

        while (rs.next()) {
            avis.setIdAvis(rs.getInt("idAvis"));
            avis.setComment(rs.getString("comment"));
            avis.setReview(rs.getFloat("review"));
            avis.setUser(userService.getUserbyID(rs.getInt("idUser")));
            avis.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));

        }

        return avis;
    }
    public List<AvisHebergement> recupererParHebergement(int idHeberg) throws SQLException, UserNotFoundException {
        String sql = "SELECT * FROM avisHebergement WHERE idHeberg = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, idHeberg);
        ResultSet rs = st.executeQuery();

        List<AvisHebergement> avisList = new ArrayList<>();
        while (rs.next()) {
            AvisHebergement avis = new AvisHebergement();
            avis.setIdAvis(rs.getInt("idAvis"));
            avis.setComment(rs.getString("comment"));
            avis.setReview(rs.getFloat("review"));
            avis.setUser(userService.getUserbyID(rs.getInt("idUser")));
            avis.setHebergements(hebService.recupererId(rs.getInt("idHeberg")));
            avisList.add(avis);
        }
        return avisList;
    }

}
