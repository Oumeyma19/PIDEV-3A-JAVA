package services;

import tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TourPhotoService {
    private Connection connection;

    public TourPhotoService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    public void addTourPhoto(int tourId, String photoPath) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO tour_photos (tour_id, photo) VALUES (?, ?)");
            ps.setInt(1, tourId);
            ps.setString(2, photoPath);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
