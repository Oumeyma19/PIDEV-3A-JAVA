package services;

import interfaces.TService;
import models.Activites;
import models.Tour;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivitesService implements TService<Activites> {
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
        return false; // Always returns false
    }

    @Override
    public boolean modifier(Activites activite) {
        String query = "UPDATE activites SET nom_activite = ?, date_debut = ?, date_fin = ?, localisation = ?, photo = ?, description = ?, tour_id = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            // Set parameters in the correct order
            ps.setString(1, activite.getNomActivite());      // nom_activite
            ps.setString(2, activite.getDateDebut());        // date_debut
            ps.setString(3, activite.getDateFin());          // date_fin
            ps.setString(4, activite.getLocalisation());     // localisation
            ps.setString(5, activite.getPhoto());            // photo
            ps.setString(6, activite.getDescription());      // description
            ps.setInt(7, activite.getTour().getId());        // tour_id
            ps.setInt(8, activite.getId());                  // id (for WHERE clause)
            ps.executeUpdate();
            return true;  // Return true if the update is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
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
                    rs.getString("nom_activite"),
                    rs.getString("date_debut"),
                    rs.getString("date_fin"),
                    rs.getString("localisation"),
                    rs.getString("photo"),
                    rs.getString("description"),
                    new Tour(rs.getInt("tour_id"))
            );
            activitesList.add(activite);
        }
        return activitesList;
    }

    public List<Activites> getActivitiesForTour(int tourId) throws SQLException {
        List<Activites> activities = new ArrayList<>();
        String query = "SELECT * FROM activites WHERE tour_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, tourId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Activites activity = new Activites(
                    rs.getInt("id"),
                    rs.getString("nom_activite"),
                    rs.getString("date_debut"),
                    rs.getString("date_fin"),
                    rs.getString("localisation"),
                    rs.getString("photo"),
                    rs.getString("description"),
                    new Tour(rs.getInt("tour_id"))
            );
            activities.add(activity);
        }
        return activities;
    }
}
