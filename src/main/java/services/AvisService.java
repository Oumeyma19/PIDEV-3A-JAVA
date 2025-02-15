package services;

import interfaces.iCrud;
import models.AvisHebergement;
import models.Hebergements;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements iCrud<AvisHebergement> {

    private final Connection conn = MyConnection.getInstance().getConn();

    @Override
    public void ajouter(AvisHebergement avis) throws SQLException {
        String sql = "INSERT INTO avishebergement(comment, review, idUser, idHeberg) VALUES(?, ?, ?, ?)";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, avis.getComment());
        st.setFloat(2, avis.getReview());
        st.setObject(3, avis.getIdUser());
        st.setObject(4, avis.getIdHeberg());

        st.executeUpdate();
        System.out.println("Avis ajouté avec succès");
    }

    @Override
    public void supprimer(int id) {
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
    }

    @Override
    public void modifier(AvisHebergement avis) {
        String sql = "UPDATE avisHebergement SET comment = ?, review = ?, idUser = ?, idHeberg = ? WHERE idAvis = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, avis.getComment());
            st.setFloat(2, avis.getReview());
            st.setObject(3, avis.getIdUser());
            st.setObject(4, avis.getIdHeberg());
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
    public List<AvisHebergement> recuperer() throws SQLException {
        String sql = "SELECT * FROM avisHebergement";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<AvisHebergement> avisList = new ArrayList<>();
        while (rs.next()) {
            AvisHebergement avis = new AvisHebergement();
            avis.setIdAvis(rs.getInt("idAvis"));
            avis.setComment(rs.getString("comment"));
            avis.setReview(rs.getFloat("review"));
            avis.setIdUser((User) rs.getObject("idUser"));
            avis.setIdHeberg((Hebergements) rs.getObject("idHeberg"));

            avisList.add(avis);
        }
        return avisList;
    }

    @Override
    public AvisHebergement recupererId(int id) throws SQLException {
        String sql = "SELECT * FROM avisHebergement WHERE idAvis = ?";
        PreparedStatement st = conn.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        AvisHebergement avis = new AvisHebergement();

        while (rs.next()) {
            avis.setIdAvis(rs.getInt("idAvis"));
            avis.setComment(rs.getString("comment"));
            avis.setReview(rs.getFloat("review"));
            avis.setIdUser((User) rs.getObject("idUser"));
            avis.setIdHeberg((Hebergements) rs.getObject("idHeberg"));
        }

        return avis;
    }
}
