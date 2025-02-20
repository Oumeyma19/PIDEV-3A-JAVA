package Service;

import Models.Recompense;
import Tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecompenseService {
    private Connection connection;

    public RecompenseService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // Create (Add Recompense)
    public void addRecompense(Recompense recompense) {
        // Update your SQL query to include the 'status' field
        String sql = "INSERT INTO recompense (programme_id, description, points_requis, photo, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recompense.getProgrammeId());
            stmt.setString(2, recompense.getNom());
            stmt.setInt(3, recompense.getPointsRequis());
            stmt.setString(4, recompense.getPhoto());
            stmt.setInt(5, recompense.getStatus());  // Add this line to set the 'status' value
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    // Read (Get all Recompenses)
    public List<Recompense> getAllRecompenses() {
        List<Recompense> recompenses = new ArrayList<>();
        String sql = "SELECT * FROM recompense";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                recompenses.add(new Recompense(
                        rs.getInt("id"),
                        rs.getInt("programme_id"),
                        rs.getString("description"),
                        rs.getInt("points_requis"),
                        rs.getString("photo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recompenses;
    }

    // Update (Modify Recompense)
    public void updateRecompense(Recompense recompense) {
        // Modifier tous les attributs : nom, points requis, et photo
        String sql = "UPDATE recompense SET description = ?, points_requis = ?, photo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, recompense.getNom());         // Mettre à jour le nom
            stmt.setInt(2, recompense.getPointsRequis());  // Mettre à jour les points requis
            stmt.setString(3, recompense.getPhoto());      // Mettre à jour la photo (URL)
            stmt.setInt(4, recompense.getId());            // Identifier la récompense par son ID
            stmt.executeUpdate();  // Exécuter la mise à jour
        } catch (SQLException e) {
            e.printStackTrace();  // Afficher l'erreur si un problème survient
        }
    }

    // Delete (Remove Recompense)
    public void deleteRecompense(int id) {
        String sql = "DELETE FROM recompense WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
