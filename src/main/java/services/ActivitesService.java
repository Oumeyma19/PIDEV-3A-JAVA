package services;

import interfaces.IService;
import models.Activites;
import models.Tour;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivitesService implements IService<Activites> {
    private Connection connection;

    public ActivitesService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(Activites activite) throws SQLException {
        String query = "INSERT INTO activites (nom_activite, date_debut, date_fin, localisation, photo, description, tour_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, activite.getNomActivite());
        ps.setString(2, activite.getDateDebut());
        ps.setString(3, activite.getDateFin());
        ps.setString(4, activite.getLocalisation());
        ps.setString(5, activite.getPhoto());
        ps.setString(6, activite.getDescription());
        ps.setInt(7, activite.getTour().getId());  // Getting tourId using getId() method of Tour class
        ps.executeUpdate();
        return true;  // Return true if the insertion is successful
    }

    @Override
    public boolean supprimer(Activites activite) {
        String query = "DELETE FROM activites WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, activite.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean modifier(Activites activite) {
        String query = "UPDATE activites SET nom_activite = ?, date_debut = ?, date_fin = ?, localisation = ?, photo = ?, description = ?, tour_id = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, activite.getDateDebut());
            ps.setString(2, activite.getDateFin());
            ps.setString(3, activite.getLocalisation());
            ps.setString(4, activite.getPhoto());
            ps.setString(5, activite.getDescription());
            ps.setInt(6, activite.getTour().getId());
            ps.setInt(7, activite.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Activites> afficher() throws SQLException {
        List<Activites> activitesList = new ArrayList<>();
        String query = "SELECT * FROM activites";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Activites activite = new Activites(
                    rs.getInt("id"),
                    rs.getString("nomActivite"),
                    rs.getString("dateDebut"),
                    rs.getString("dateFin"),
                    rs.getString("localisation"),
                    rs.getString("photo"),
                    rs.getString("description"),
                    new Tour(rs.getInt("tourId"))
            );
            activitesList.add(activite);
        }
        return activitesList;
    }
}
