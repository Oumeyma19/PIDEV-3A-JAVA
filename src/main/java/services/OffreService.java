    package services;

    import interfaces.IService;
    import models.Flight;
    import models.Hebergements;
    import models.Offre;
    import models.Tour;
    import tools.MyDataBase;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class OffreService implements IService<Offre> {
        private Connection cnx;

        public OffreService() {
            cnx = MyDataBase.getInstance().getCnx();
        }

        public void ajouter(Offre o, List<Integer> hebergementIds, List<Integer> tourIds, List<Integer> flightIds) throws SQLException {
            if (isOfferExists(o.getTitle())) {
                throw new SQLException("An offer with this title already exists.");
            }

            // Insert into `offers` table
            String sql = "INSERT INTO offers (title, description, price, start_date, end_date, image_path) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement st = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, o.getTitle());
            st.setString(2, o.getDescription());
            st.setDouble(3, o.getPrice()); // Set calculated price
            st.setString(4, o.getStartDate());
            st.setString(5, o.getEndDate());
            st.setString(6, o.getImagePath());
            st.executeUpdate();

            // Get the generated offer ID
            ResultSet rs = st.getGeneratedKeys();
            int offerId = -1;
            if (rs.next()) {
                offerId = rs.getInt(1);
            }

            // Insert into `offre_hebergement`
            for (Integer hId : hebergementIds) {
                String sqlH = "INSERT INTO offre_hebergement (offre_id, hebergement_id) VALUES (?, ?)";
                PreparedStatement stH = cnx.prepareStatement(sqlH);
                stH.setInt(1, offerId);
                stH.setInt(2, hId);
                stH.executeUpdate();
            }

            // Insert into `offre_tour`
            for (Integer tId : tourIds) {
                String sqlT = "INSERT INTO offre_tour (offre_id, tour_id) VALUES (?, ?)";
                PreparedStatement stT = cnx.prepareStatement(sqlT);
                stT.setInt(1, offerId);
                stT.setInt(2, tId);
                stT.executeUpdate();
            }

            // Insert into `offre_flight`
            for (Integer fId : flightIds) {
                String sqlF = "INSERT INTO offre_flight (offre_id, flight_id) VALUES (?, ?)";
                PreparedStatement stF = cnx.prepareStatement(sqlF);
                stF.setInt(1, offerId);
                stF.setInt(2, fId);
                stF.executeUpdate();
            }

            System.out.println("Offre ajoutée avec ses relations.");
        }



        public boolean isOfferExists(String title) throws SQLException {
            String sql = "SELECT COUNT(*) FROM offers WHERE title = ?";
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setString(1, title);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if count > 0 (offer exists)
            }
            return false;
        }

        @Override
        public void ajouter(Offre p) throws SQLException {

        }

        @Override
        public void supprimer(Offre o) throws SQLException {
            String sql = "DELETE FROM offers WHERE id = ?";
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setInt(1, o.getId());
            st.executeUpdate();
            System.out.println("Offre supprimée");
        }

        @Override
        public void modifier(Offre p, String nom) {

        }



        @Override
        public void modifier(Offre o) throws SQLException {
            String sql = "UPDATE offers SET title = ?, description = ?, price = ?, start_date = ?, end_date = ? WHERE id = ?";
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setString(1, o.getTitle());
            st.setString(2, o.getDescription());
            st.setDouble(3, o.getPrice());
            st.setString(4, o.getStartDate());
            st.setString(5, o.getEndDate());
            st.setInt(6, o.getId());
            st.executeUpdate();
            System.out.println("Offre modifiée");
        }

        @Override
        public void modifier(Hebergements H) {

        }

        @Override
        public List<Hebergements> recuperer() throws SQLException {
            return List.of();
        }

        @Override
        public void modifier2(Hebergements p, String s) throws SQLException {

        }


        @Override
        public List<Offre> afficher() throws SQLException {
            String sql = "SELECT * FROM offers";
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            List<Offre> offres = new ArrayList<>();

            while (rs.next()) {
                Offre o = new Offre();
                o.setId(rs.getInt("id"));
                o.setTitle(rs.getString("title"));
                o.setDescription(rs.getString("description"));
                o.setPrice(rs.getDouble("price"));
                o.setStartDate(rs.getString("start_date"));
                o.setEndDate(rs.getString("end_date"));
                o.setImagePath(rs.getString("image_path"));

                // Fetch Hebergements related to this offer
                List<Hebergements> hebergements = new ArrayList<>();
                String sqlH = "SELECT h.* FROM hebergements h JOIN offre_hebergement oh ON h.idHberg = oh.hebergement_id WHERE oh.offre_id = ?";
                PreparedStatement stH = cnx.prepareStatement(sqlH);
                stH.setInt(1, o.getId());
                ResultSet rsH = stH.executeQuery();
                while (rsH.next()) {
                    Hebergements h = new Hebergements();
                    h.setIdHebrg(rsH.getInt("idHberg"));
                    h.setNomHeberg(rsH.getString("nomHebrg"));
                    hebergements.add(h);
                }
                o.setHebergements(hebergements);

                // Fetch Tours related to this offer
                List<Tour> tours = new ArrayList<>();
                String sqlT = "SELECT t.* FROM tours t JOIN offre_tour ot ON t.id = ot.tour_id WHERE ot.offre_id = ?";
                PreparedStatement stT = cnx.prepareStatement(sqlT);
                stT.setInt(1, o.getId());
                ResultSet rsT = stT.executeQuery();
                while (rsT.next()) {
                    Tour t = new Tour();
                    t.setId(rsT.getInt("id"));
                    t.setTitle(rsT.getString("title"));
                    tours.add(t);
                }
                o.setTours(tours);

                // Fetch Flights related to this offer
                List<Flight> flights = new ArrayList<>();
                String sqlF = "SELECT f.* FROM flight f JOIN offre_flight ofl ON f.idFlight = ofl.flight_id WHERE ofl.offre_id = ?";
                PreparedStatement stF = cnx.prepareStatement(sqlF);
                stF.setInt(1, o.getId());
                ResultSet rsF = stF.executeQuery();
                while (rsF.next()) {
                    Flight f = new Flight();
                    f.setIdFlight(rsF.getInt("idFlight"));
                    f.setFlightNumber(rsF.getString("flight_number"));
                    f.setDeparture(rsF.getString("departure"));
                    flights.add(f);
                }
                o.setFlights(flights);

                // Add offer to the list
                offres.add(o);
            }

            return offres;
        }


        public int getOfferIdByTitle(String title) throws SQLException {
            String sql = "SELECT id FROM offers WHERE title = ?";
            PreparedStatement st = cnx.prepareStatement(sql);
            st.setString(1, title);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Offer with title '" + title + "' not found.");
            }
        }
    }
