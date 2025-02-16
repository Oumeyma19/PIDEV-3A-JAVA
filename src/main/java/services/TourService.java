package services;

import models.Tour;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourService {

    private Connection connection;

    public TourService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // ✅ Ajouter un tour avec photos
    public int addTour(Tour tour, List<String> imagePaths) {
        int tourId = -1;
        try {
            if (tour.getTitle() == null || tour.getTitle().isEmpty()) {
                System.out.println("Error: Title is null or empty");
                return tourId; // You can handle this case as per your needs.
            }

            // Step 1: Insert the tour
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tours (title, description, price, location, date, guide_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setDouble(3, tour.getPrice());
            ps.setString(4, tour.getLocation());
            ps.setString(5, tour.getDate());
            ps.setInt(6, tour.getGuideId());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

            // Step 2: Get generated tour ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                tourId = rs.getInt(1);
            }

            // Step 3: Insert images into the tour_photos table if there are any images
            if (tourId != -1 && imagePaths != null && !imagePaths.isEmpty()) {
                String photoInsertSQL = "INSERT INTO tour_photos (tour_id, photo) VALUES (?, ?)";
                PreparedStatement photoPs = connection.prepareStatement(photoInsertSQL);

                for (String imagePath : imagePaths) {
                    photoPs.setInt(1, tourId);
                    photoPs.setString(2, imagePath);
                    photoPs.addBatch();  // Add to batch for efficient execution
                }

                // Execute batch insert
                photoPs.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tourId;
    }

    // ✅ Get all tours with one photo
    public List<Tour> getAllToursWithOnePhoto() {
        List<Tour> tours = new ArrayList<>();
        try {
            // Query to fetch tours with their first photo
            String sql = "SELECT t.*, tp.photo " +
                    "FROM tours t " +
                    "LEFT JOIN (SELECT tour_id, MIN(photo) AS photo FROM tour_photos GROUP BY tour_id) tp " +
                    "ON t.id = tp.tour_id";
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
                        rs.getInt("guide_id")
                );

                // Set the first photo (if available)
                String photo = rs.getString("photo");
                if (photo != null) {
                    List<String> photos = new ArrayList<>();
                    photos.add(photo);
                    tour.setPhotos(photos);
                }

                tours.add(tour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tours;
    }

    // ✅ Get photos for a tour
    public List<String> getTourPhotos(int tourId) {
        List<String> photos = new ArrayList<>();
        try {
            String sql = "SELECT photo FROM tour_photos WHERE tour_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tourId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                photos.add(rs.getString("photo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photos;
    }

    // ✅ Delete a tour
    // ✅ Delete a tour and its dependent rows
    public boolean deleteTour(int id) {
        try {
            // Step 1: Delete dependent rows in the `avis_tour` table
            String deleteAvisTourSQL = "DELETE FROM avis_tour WHERE tourId = ?";
            PreparedStatement deleteAvisTourPS = connection.prepareStatement(deleteAvisTourSQL);
            deleteAvisTourPS.setInt(1, id);
            deleteAvisTourPS.executeUpdate();

            // Step 2: Delete the tour
            String deleteTourSQL = "DELETE FROM tours WHERE id = ?";
            PreparedStatement deleteTourPS = connection.prepareStatement(deleteTourSQL);
            deleteTourPS.setInt(1, id);
            int rowsAffected = deleteTourPS.executeUpdate();

            return rowsAffected > 0; // Return true if the tour was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Update a tour
    public boolean updateTour(Tour tour) {
        try {
            String sql = "UPDATE tours SET title = ?, description = ?, price = ?, location = ?, date = ?, guide_id = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tour.getTitle());
            ps.setString(2, tour.getDescription());
            ps.setDouble(3, tour.getPrice());
            ps.setString(4, tour.getLocation());
            ps.setString(5, tour.getDate());
            ps.setInt(6, tour.getGuideId());
            ps.setInt(7, tour.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if the tour was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
