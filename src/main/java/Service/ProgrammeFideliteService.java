package Service;

import Models.ProgrammeFidelite;
import Tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeFideliteService {
    private Connection connection;

    public ProgrammeFideliteService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // Create (Add Programme)
    public void addProgramme(ProgrammeFidelite programme) {
        String sql = "INSERT INTO programme_fidelite ( nom_programme, points) VALUES ( ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, programme.getNomProgramme());
            stmt.setInt(2, programme.getPoints());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read (Get all programmes)
    public List<ProgrammeFidelite> getAllProgrammes() {
        List<ProgrammeFidelite> programmes = new ArrayList<>();
        String sql = "SELECT * FROM programme_fidelite";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                programmes.add(new ProgrammeFidelite(
                        rs.getInt("id"),

                        rs.getString("nom_programme"),
                        rs.getInt("points")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return programmes;
    }

    // Update (Modify programme)
    // Update (Modify programme)
    public boolean updateProgramme(ProgrammeFidelite programme) {
        String sql = "UPDATE programme_fidelite SET nom_programme = ?, points = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, programme.getNomProgramme());  // Nom du programme
            stmt.setInt(2, programme.getPoints());            // Points du programme
            stmt.setInt(3, programme.getId());                // ID du programme (paramètre manquant dans votre code précédent)

            int rowsAffected = stmt.executeUpdate();          // Exécution de la mise à jour
            return rowsAffected > 0;                          // Retourner true si une ligne a été mise à jour
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Retourner false en cas d'échec
    }

    public boolean existsById(int programmeId) {
        String query = "SELECT COUNT(*) FROM programme_fidelite WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, programmeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count > 0, the programme exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in your application
        }
        return false; // Default to false if an error occurs
    }

    // Delete (Remove programme)
    public void deleteProgramme(int id) {
        String sql = "DELETE FROM programme_fidelite WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}