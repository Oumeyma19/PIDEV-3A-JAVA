package Service;

import Models.HistoriquePoint;
import Tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriquePointsService {
    private Connection conn = MyConnection.getConnection();

    // Create (Add HistoriquePoint)
    public void addHistorique(HistoriquePoint historique) {
        String sql = "INSERT INTO historique_points (client_id, points_gagnes, date_transaction) VALUES ( ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, historique.getClientId());

            stmt.setInt(2, historique.getPoints());
            stmt.setString(3, historique.getDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read (Get all historique points)
    public List<HistoriquePoint> getAllHistoriques() {
        List<HistoriquePoint> historiques = new ArrayList<>();
        String sql = "SELECT * FROM historique_points";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                historiques.add(new HistoriquePoint(
                        rs.getInt("id"),
                        rs.getInt("client_id"),

                        rs.getInt("points_gagnes"),
                        rs.getString("date_transaction")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historiques;
    }

    // Update (Modify historique points)
    public void updateHistorique(HistoriquePoint historique) {
        String sql = "UPDATE historique_points SET  points_gagnes = ?, date_transaction = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, historique.getPoints());
            stmt.setString(2, historique.getDate());
            stmt.setInt(3, historique.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete (Remove historique points)
    public void deleteHistorique(int id) {
        String sql = "DELETE FROM historique_points WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}