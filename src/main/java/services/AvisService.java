    package services;

    import models.AvisTour;
    import tools.MyConnection;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class AvisService {

            private Connection connection;

            public AvisService() {
                this.connection = MyConnection.getInstance().getConnection();
            }


            // ✅ Vérifier si le client a réservé ce tour
            private boolean clientAReserveTour(int clientId, int tourId) {
                try {
                    PreparedStatement ps = connection.prepareStatement(
                            "SELECT COUNT(*) FROM reservationstours WHERE client_id = ? AND tour_id = ? AND status = 'confirmed'"
                    );
                    ps.setInt(1, clientId);
                    ps.setInt(2, tourId);
                    ResultSet rs = ps.executeQuery();
                    return rs.next() && rs.getInt(1) > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }


        // ✅ Ajouter un avis (seulement si le client a réservé)
        public boolean addAvis(AvisTour avis) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO avis_tour (clientId, tourId, etoile, commentaire) VALUES (?, ?, ?, ?)"
                );
                ps.setInt(1, avis.getClientId());
                ps.setInt(2, avis.getTourId());
                ps.setInt(3, avis.getEtoile());
                ps.setString(4, avis.getCommentaire());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }




        // ✅ Récupérer tous les avis
        public List<AvisTour> getAllAvis() {
            List<AvisTour> avisList = new ArrayList<>();
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM avis_tour");
                while (rs.next()) {
                    avisList.add(new AvisTour(rs.getInt("id"), rs.getInt("clientId"), rs.getInt("tourId"), rs.getInt("etoile"), rs.getString("commentaire")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return avisList;
        }

        // ✅ Modifier un avis
        public boolean updateAvis(AvisTour avis) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE avis_tour SET etoile = ?, commentaire = ? WHERE id = ?"
                );
                ps.setInt(1, avis.getEtoile());
                ps.setString(2, avis.getCommentaire());
                ps.setInt(3, avis.getId());
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0; // Return true if the update was successful
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        // ✅ Supprimer un avis
        public boolean deleteAvis(int avisId) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM avis_tour WHERE id = ?"
                );
                ps.setInt(1, avisId);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

    }
