package services;

import models.AvisTour;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService {

        private Connection connection;

        public AvisService() {
            this.connection = MyConnection.getInstance().getConnection();
        }


        // ✅ Vérifier si le client a réservé ce tour
    private boolean clientAReserveTour(int clientId, int tourId) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM reservationstours WHERE client_id = ? AND tour_id = ? AND status = 'confirmed'");
            ps.setInt(1, clientId);
            ps.setInt(2, tourId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Ajouter un avis (seulement si le client a réservé)
    public boolean addAvis(AvisTour avis) {
        if (!clientAReserveTour(avis.getClientId(), avis.getTourId())) {
            System.out.println("Le client n'a pas réservé ce tour !");
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO avis_tour (clientId, tourId, etoile, commentaire) VALUES (?, ?, ?, ?)");
            ps.setInt(1, avis.getClientId());
            ps.setInt(2, avis.getTourId());
            ps.setInt(3, avis.getEtoile());
            ps.setString(4, avis.getCommentaire());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Récupérer tous les avis
    public List<AvisTour> getAllAvis() {
        List<AvisTour> avisList = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM avis_tour");
            while (rs.next()) {
                avisList.add(new AvisTour(rs.getInt("id"), rs.getInt("clientId"), rs.getInt("tourId"), rs.getInt("etoile"), rs.getString("commentaire")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avisList;
    }

    // ✅ Modifier un avis
    public void updateAvis(int id, int etoile, String commentaire) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE avis_tour SET etoile = ?, commentaire = ? WHERE id = ?");
            ps.setInt(1, etoile);
            ps.setString(2, commentaire);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Supprimer un avis
    public void deleteAvis(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM avis_tour WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
