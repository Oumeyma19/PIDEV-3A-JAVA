    package services;

    import models.Recompense;
    import tools.MyConnection;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class RecompenseService {
        private final Connection connection;

        public RecompenseService() {
            this.connection = MyConnection.getInstance().getConnection();
        }

        // Create (Add Recompense)
        public void addRecompense(Recompense recompense) {
            String sql = "INSERT INTO recompense (programme_id, nom, description, points_requis, photo, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, recompense.getProgrammeId());
                stmt.setString(2, recompense.getNom());
                stmt.setString(3, recompense.getDescription());
                stmt.setInt(4, recompense.getPointsRequis());
                stmt.setString(5, recompense.getPhoto());
                stmt.setInt(6, recompense.getStatus());
                stmt.executeUpdate();
                System.out.println("Reward added successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("SQLException: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("VendorError: " + e.getErrorCode());
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
                            rs.getString("nom"),
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

        public List<Recompense> getRecompensesByUserId(int userId) {
            List<Recompense> recompenses = new ArrayList<>();
            String sql = "SELECT * FROM recompense WHERE user_id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    recompenses.add(new Recompense(
                            rs.getInt("id"),
                            rs.getInt("programme_id"),
                            rs.getString("nom"),
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

        public List<Recompense> getAllRecompensesStatus() {
            List<Recompense> recompenses = new ArrayList<>();
            String sql = "SELECT * FROM recompense WHERE status = 1"; // Only fetch unclaimed recompenses
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    // Debug: Print column names and values
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Programme ID: " + rs.getInt("programme_id"));
                    System.out.println("Nom: " + rs.getString("nom"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Points Requis: " + rs.getInt("points_requis"));
                    System.out.println("Photo: " + rs.getString("photo"));
                    System.out.println("-----------------------------");

                    recompenses.add(new Recompense(
                            rs.getInt("id"),
                            rs.getInt("programme_id"),
                            rs.getString("nom"),
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
            // Modifier tous les attributs : nom, description, points requis, et photo
            String sql = "UPDATE recompense SET nom = ?, description = ?, points_requis = ?, photo = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, recompense.getNom());         // Mettre à jour le nom
                stmt.setString(2, recompense.getDescription());  // Mettre à jour la description
                stmt.setInt(3, recompense.getPointsRequis());    // Mettre à jour les points requis
                stmt.setString(4, recompense.getPhoto());        // Mettre à jour la photo (URL)
                stmt.setInt(5, recompense.getId());              // Identifier la récompense par son ID
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

        public void claimRecompense(int recompenseId, int userId, int pointsRequis) {
            String sql = "UPDATE recompense SET status = 0, user_id = ? WHERE id = ?"; // Archive the recompense
            String updateUserPointsSql = "UPDATE user SET pointsfid = pointsfid - ? WHERE id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 PreparedStatement updatePointsStmt = connection.prepareStatement(updateUserPointsSql)) {

                // Deduct the points from the user's account
                updatePointsStmt.setInt(1, pointsRequis);
                updatePointsStmt.setInt(2, userId);
                updatePointsStmt.executeUpdate();

                // Archive the recompense
                stmt.setInt(1, userId);
                stmt.setInt(2, recompenseId);
                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }