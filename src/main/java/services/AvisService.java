package services;

import exceptions.UserNotFoundException;
import models.AvisTour;
import models.User;
import tools.MyConnection;
import interfaces.IService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements IService<AvisTour> {

    private Connection connection;

    public AvisService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // ✅ Vérifier si le client a réservé ce tour
    private boolean clientAReserveTour(int clientId, int tourId) {
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

    // ✅ Ajouter un avis (seulement si le client a réservé)
    @Override
    public boolean ajouter(AvisTour avis) throws SQLException {

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO avis_tour (clientId, tourId, etoile, commentaire) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, avis.getUser().getId()); // Corrected to get the user ID
            ps.setInt(2, avis.getTourId());
            ps.setInt(3, avis.getEtoile());
            ps.setString(4, avis.getCommentaire());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
    }


    // ✅ Supprimer un avis
    @Override
    public boolean supprimer(AvisTour avis) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM avis_tour WHERE id = ?"
            );
            ps.setInt(1, avis.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No review found to delete with id: " + avis.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // ✅ Modifier un avis
    @Override
    public boolean modifier(AvisTour avis) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE avis_tour SET etoile = ?, commentaire = ? WHERE id = ?"
            );
            ps.setInt(1, avis.getEtoile());
            ps.setString(2, avis.getCommentaire());
            ps.setInt(3, avis.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<AvisTour> afficher() throws SQLException {
        List<AvisTour> avisList = new ArrayList<>();
        try {
            String query = "SELECT a.id, u.firstname AS clientName, a.tourId, a.etoile, a.commentaire " +
                    "FROM avis_tour a " +
                    "JOIN user u ON a.clientId = u.id";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                avisList.add(new AvisTour(
                        rs.getInt("id"),
                        new User(rs.getString("clientName")), // Fetch using the alias
                        rs.getInt("tourId"),
                        rs.getInt("etoile"),
                        rs.getString("commentaire")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw exception for handling
        }
        return avisList;
    }


    public List<AvisTour> getAvisByTourId(int tourId) throws SQLException {
        List<AvisTour> avisList = new ArrayList<>();
        String query = "SELECT a.id, u.firstname AS clientName, a.tourId, a.etoile, a.commentaire " +
                "FROM avis_tour a " +
                "JOIN user u ON a.clientId = u.id " +
                "WHERE a.tourId = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, tourId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            avisList.add(new AvisTour(
                    rs.getInt("id"),
                    new User(rs.getString("clientName")), // Fetch using the alias
                    rs.getInt("tourId"),
                    rs.getInt("etoile"),
                    rs.getString("commentaire")
            ));
        }
        return avisList;
    }


}
