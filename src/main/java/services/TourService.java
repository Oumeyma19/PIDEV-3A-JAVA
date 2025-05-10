package services;

import interfaces.TService;
import models.Activites;
import models.Tour;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourService implements TService<Tour> {

    private Connection connection;

    public TourService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public boolean ajouter(Tour tour) throws SQLException {
        try {
            // Insert the tour into the `tours` table
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tours (title, description, price, location, date, guide_id, created_at, photo, nbPlaceDispo, nbPlaceReserver, categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setDouble(3, tour.getPrice());
            ps.setString(4, tour.getLocation());
            ps.setString(5, tour.getDate());
            ps.setInt(6, tour.getGuideId());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setString(8, tour.getPhoto());
            ps.setInt(9, tour.getNbPlaceDisponible());
            ps.setInt(10, tour.getNbPlaceReserver());
            ps.setString(11, tour.getType() != null ? tour.getType().name() : null);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated tour ID
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int tourId = rs.getInt(1);

                    // Insert activities into the `activites` table
                    for (Activites activity : tour.getActivities()) {
                        addActivityToTour(tourId, activity);
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean modifier(Tour tour) {
        try {
            String sql = "UPDATE tours SET title = ?, description = ?, price = ?, location = ?, date = ?, photo = ?, nbPlaceDispo = ?, categorie = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setDouble(3, tour.getPrice());
            ps.setString(4, tour.getLocation());
            ps.setString(5, tour.getDate());
            ps.setString(6, tour.getPhoto()); // Single photo path
            ps.setInt(7, tour.getNbPlaceDisponible()); // Update available places
            ps.setString(8, tour.getType() != null ? tour.getType().name() : null);
            ps.setInt(9, tour.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean supprimer(Tour tour) {
        try {
            // Step 1: Delete dependent rows in the `avis_tour` table
            String deleteAvisTourSQL = "DELETE FROM avis_tour WHERE tourId = ?";
            PreparedStatement deleteAvisTourPS = connection.prepareStatement(deleteAvisTourSQL);
            deleteAvisTourPS.setInt(1, tour.getId());
            deleteAvisTourPS.executeUpdate();

            // Step 2: Delete the tour
            String deleteTourSQL = "DELETE FROM tours WHERE id = ?";
            PreparedStatement deleteTourPS = connection.prepareStatement(deleteTourSQL);
            deleteTourPS.setInt(1, tour.getId());
            deleteTourPS.executeUpdate();

            return true; // Return true if deletion is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    @Override
    public List<Tour> afficher() throws SQLException {
        List<Tour> tours = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tours";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Tour tour = new Tour(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("location"),
                        rs.getString("date"),
                        rs.getInt("guide_id"),
                        rs.getString("photo"), // Single photo path
                        rs.getInt("nbPlaceDispo"),
                        rs.getInt("nbPlaceReserver"),
                        Tour.TourType.valueOf(rs.getString("categorie"))
                );
                tours.add(tour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tours;
    }

    // ✅ Get tours by location (additional method, not in interface but useful)
    public List<Tour> getToursByLocation(String location) throws SQLException {
        List<Tour> filteredTours = new ArrayList<>();
        String sql = "SELECT * FROM tours WHERE location LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + location + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Tour tour = new Tour(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getString("date"),
                    rs.getInt("guide_id"),
                    rs.getString("photo"), // Single photo path
                    rs.getInt("nbPlaceDispo"),
                    rs.getInt("nbPlaceReserver"),
                    Tour.TourType.valueOf(rs.getString("categorie"))
            );

            filteredTours.add(tour);
        }

        return filteredTours;
    }

    public boolean addActivityToTour(int tourId, Activites activity) throws SQLException {
        String query = "INSERT INTO activites (nom_activite, date_debut, date_fin, localisation, photo, description, tour_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, activity.getNomActivite());
        ps.setString(2, activity.getDateDebut()); // Already in YYYY-MM-DD format
        ps.setString(3, activity.getDateFin());
        ps.setString(4, activity.getLocalisation());
        ps.setString(5, activity.getPhoto());
        ps.setString(6, activity.getDescription());
        ps.setInt(7, tourId);
        return ps.executeUpdate() > 0;
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

    public List<Tour> getToursByGuideId(int guideId) throws SQLException {
        List<Tour> tours = new ArrayList<>();
        String sql = "SELECT * FROM tours WHERE guide_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, guideId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Tour tour = new Tour(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getString("date"),
                    rs.getInt("guide_id"),
                    rs.getString("photo"),
                    rs.getInt("nbPlaceDispo"),
                    rs.getInt("nbPlaceReserver"),
                    Tour.TourType.valueOf(rs.getString("categorie"))
            );
            tours.add(tour);
        }
        return tours;
    }

    public Tour getTourById(int tourId) throws SQLException {
        String sql = "SELECT * FROM tours WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, tourId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Tour(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getString("date"),
                    rs.getInt("guide_id"),
                    rs.getString("photo"),
                    rs.getInt("nbPlaceDispo"),
                    rs.getInt("nbPlaceReserver"),
                    Tour.TourType.valueOf(rs.getString("categorie"))
            );
        }

        return null; // Return null if no tour is found with the given ID
    }
    public List<Tour> recuperer() throws SQLException {
        String sql = "select * from tours";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Tour> Tour = new ArrayList<>();
        while (rs.next()) {
            Tour T = new Tour();

            T.setId(rs.getInt("id"));
            T.setGuideId(rs.getInt("guide_id"));
            T.setTitle(rs.getString("title"));
            T.setDescription(rs.getString("description"));
            T.setPrice(rs.getInt("price"));
            T.setDate(rs.getString("date"));
            T.setLocation(rs.getString("location"));


            Tour.add(T);
        }
        return Tour;
    }
}