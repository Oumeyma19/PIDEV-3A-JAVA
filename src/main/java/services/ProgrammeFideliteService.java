    package services;

    import models.ProgrammeFidelite;
    import tools.MyConnection;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class ProgrammeFideliteService {
        private static Connection connection;

        public ProgrammeFideliteService() {
            connection = MyConnection.getInstance().getConnection();
        }

        // Ajouter un programme de fidélité
        public void addProgramme(ProgrammeFidelite programme) {
            String sql = "INSERT INTO programme_fidelite (nom_programme, points, photo) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, programme.getNomProgramme());
                stmt.setInt(2, programme.getPoints());
                stmt.setString(3, programme.getPhoto());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Récupérer tous les programmes de fidélité
        public List<ProgrammeFidelite> getAllProgrammes() {
            List<ProgrammeFidelite> programmes = new ArrayList<>();
            String sql = "SELECT * FROM programme_fidelite";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    programmes.add(new ProgrammeFidelite(
                            rs.getInt("id"),
                            rs.getString("nom_programme"),
                            rs.getInt("points"),
                            rs.getString("photo")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return programmes;
        }

        public int getFirstProgrammeId() {
            String sql = "SELECT id FROM programme_fidelite LIMIT 1";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    // Si aucun programme n'existe, créez-en un par défaut
                    return createDefaultProgramme();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1; // Retourne -1 en cas d'erreur
            }
        }

        private int createDefaultProgramme() {
            String sql = "INSERT INTO programme_fidelite (nom_programme, points, photo) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, "Programme par défaut");
                stmt.setInt(2, 0); // Valeur par défaut des points
                stmt.setString(3, "default.jpg"); // Image par défaut

                stmt.executeUpdate();

                // Récupérer l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Échec de la création du programme, aucun ID obtenu.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }

        // Mettre à jour un programme de fidélité
        public boolean updateProgramme(ProgrammeFidelite programme) {
            String sql = "UPDATE programme_fidelite SET nom_programme = ?, points = ?, photo = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, programme.getNomProgramme());
                stmt.setInt(2, programme.getPoints());
                stmt.setString(3, programme.getPhoto());
                stmt.setInt(4, programme.getId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Retourne true si au moins une ligne a été mise à jour
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Supprimer un programme de fidélité par son ID
        public boolean deleteProgramme(int id) {
            // D'abord supprimer les récompenses associées
            String deleteRecompensesSql = "DELETE FROM recompense WHERE programme_id = ?";

            try (PreparedStatement deleteRecompensesStmt = connection.prepareStatement(deleteRecompensesSql)) {
                deleteRecompensesStmt.setInt(1, id);
                deleteRecompensesStmt.executeUpdate();

                // Ensuite supprimer le programme
                String deleteProgrammeSql = "DELETE FROM programme_fidelite WHERE id = ?";
                try (PreparedStatement deleteProgrammeStmt = connection.prepareStatement(deleteProgrammeSql)) {
                    deleteProgrammeStmt.setInt(1, id);
                    int rowsAffected = deleteProgrammeStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Récupérer un programme de fidélité par son ID
        public ProgrammeFidelite getProgrammeById(int id) {
            String sql = "SELECT * FROM programme_fidelite WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new ProgrammeFidelite(
                                rs.getInt("id"),
                                rs.getString("nom_programme"),
                                rs.getInt("points"),
                                rs.getString("photo")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null; // Retourne null si aucun programme n'est trouvé
        }
    }
