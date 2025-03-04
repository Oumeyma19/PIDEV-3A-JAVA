package services;

import interfaces.IService;
import models.Flight;
import models.Airport;
import models.Hebergements;
import models.Offre;
import tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightService implements IService<Flight> {

    private Connection cnx;

    public FlightService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Flight p) throws SQLException {
        String sql = "INSERT INTO flight (idFlight, departure, destination, departureTime, arrivalTime, price, flight_number, departure_Airport_id, arrival_Airport_id , numbre_place) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, p.getIdFlight());
            pst.setString(2, p.getDeparture());
            pst.setString(3, p.getDestination());
            pst.setTimestamp(4, p.getDepartureTime()); // Assuming it's a Timestamp
            pst.setTimestamp(5, p.getArrivalTime());   // Assuming it's a Timestamp
            pst.setDouble(6, p.getPrice());
            pst.setString(7, p.getFlightNumber());
            pst.setInt(8, p.getDepartureAirport().getIdAirport());
            pst.setInt(9, p.getArrivalAirport().getIdAirport());
            pst.setInt(10, p.getNumbre_place());

            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Flight added successfully.");
            } else {
                System.out.println("Error adding flight.");
            }
        }
    }

    @Override
    public void supprimer(Flight p) {
        String sql = "DELETE FROM flight WHERE idFlight = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, p.getIdFlight());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Flight deleted successfully.");
            } else {
                System.out.println("No flight found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Flight p, String flightNumber) {
        String sql = "UPDATE flight SET departure = ?, destination = ?, departureTime = ?, arrivalTime = ?, price = ?, flight_number = ?, departure_Airport_id = ?, arrival_Airport_id = ? , numbre_place = ? WHERE flight_number = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, p.getDeparture());
            pst.setString(2, p.getDestination());
            pst.setTimestamp(3, p.getDepartureTime());
            pst.setTimestamp(4, p.getArrivalTime());
            pst.setDouble(5, p.getPrice());
            pst.setString(6, p.getFlightNumber());
            pst.setInt(7, p.getDepartureAirport().getIdAirport());
            pst.setInt(8, p.getArrivalAirport().getIdAirport());
            pst.setInt(9, p.getNumbre_place());
            pst.setString(10, flightNumber);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Flight updated successfully.");
            } else {
                System.out.println("No flight found with the given flight number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Flight> afficher() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, da.nameAirport AS departureAirportName, da.location AS departureLocation, da.code AS departureCode, " +
                "aa.nameAirport AS arrivalAirportName, aa.location AS arrivalLocation, aa.code AS arrivalCode " +
                "FROM flight f " +
                "JOIN airport da ON f.departure_Airport_id = da.idAirport " +
                "JOIN airport aa ON f.arrival_Airport_id = aa.idAirport";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Create departure airport object
                Airport departureAirport = new Airport(
                        rs.getInt("departure_Airport_id"),
                        rs.getString("departureAirportName"),
                        rs.getString("departureLocation"),
                        rs.getString("departureCode")
                );

                // Create arrival airport object
                Airport arrivalAirport = new Airport(
                        rs.getInt("arrival_Airport_id"),
                        rs.getString("arrivalAirportName"),
                        rs.getString("arrivalLocation"),
                        rs.getString("arrivalCode")
                );

                // Create flight object
                Flight flight = new Flight(
                        rs.getInt("idFlight"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getTimestamp("departureTime"),
                        rs.getTimestamp("arrivalTime"),
                        rs.getInt("price"),
                        rs.getString("flight_number"),
                        departureAirport,
                        arrivalAirport,
                        rs.getInt("numbre_place")
                );
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    @Override
    public void modifier(Offre o) throws SQLException {

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

    public List<Flight> searchFlights(String departure, String arrival, Timestamp departureTimestamp, Timestamp arrivalTimestamp) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, da.nameAirport AS departureAirportName, da.location AS departureLocation, da.code AS departureCode, " +
                "aa.nameAirport AS arrivalAirportName, aa.location AS arrivalLocation, aa.code AS arrivalCode " +
                "FROM flight f " +
                "JOIN airport da ON f.departure_Airport_id = da.idAirport " +
                "JOIN airport aa ON f.arrival_Airport_id = aa.idAirport " +
                "WHERE f.departure = ? AND f.destination = ? " +
                "AND f.departureTime >= ? AND f.arrivalTime <= ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            // Set the query parameters
            pst.setString(1, departure);
            pst.setString(2, arrival);
            pst.setTimestamp(3, departureTimestamp);
            pst.setTimestamp(4, arrivalTimestamp);

            // Execute the query
            ResultSet rs = pst.executeQuery();

            // Process the result set
            while (rs.next()) {
                // Create departure airport object
                Airport departureAirport = new Airport(
                        rs.getInt("departure_Airport_id"),
                        rs.getString("departureAirportName"),
                        rs.getString("departureLocation"),
                        rs.getString("departureCode")
                );

                // Create arrival airport object
                Airport arrivalAirport = new Airport(
                        rs.getInt("arrival_Airport_id"),
                        rs.getString("arrivalAirportName"),
                        rs.getString("arrivalLocation"),
                        rs.getString("arrivalCode")
                );

                // Create flight object
                Flight flight = new Flight(
                        rs.getInt("idFlight"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getTimestamp("departureTime"),
                        rs.getTimestamp("arrivalTime"),
                        rs.getInt("price"),
                        rs.getString("flight_number"),
                        departureAirport,
                        arrivalAirport,
                        rs.getInt("numbre_place")
                );

                // Add the flight to the list
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    public Flight getFlightById(int idFlight) {
        Flight flight = null;
        String sql = "SELECT f.*, da.idAirport AS dep_airport_id, da.nameAirport AS dep_airport_name, " +
                "aa.idAirport AS arr_airport_id, aa.nameAirport AS arr_airport_name " +
                "FROM flight f " +
                "LEFT JOIN airport da ON f.departure_Airport_id = da.idAirport " +
                "LEFT JOIN airport aa ON f.arrival_Airport_id = aa.idAirport " +
                "WHERE f.idFlight = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, idFlight);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // Create departure airport object
                    Airport departureAirport = new Airport(
                            rs.getInt("dep_airport_id"),
                            rs.getString("dep_airport_name"),
                            null, // Add location if needed
                            null  // Add code if needed
                    );

                    // Create arrival airport object
                    Airport arrivalAirport = new Airport(
                            rs.getInt("arr_airport_id"),
                            rs.getString("arr_airport_name"),
                            null, // Add location if needed
                            null  // Add code if needed
                    );

                    // Create flight object
                    flight = new Flight(
                            rs.getInt("idFlight"),
                            rs.getString("departure"),
                            rs.getString("destination"),
                            rs.getTimestamp("departureTime"),
                            rs.getTimestamp("arrivalTime"),
                            rs.getInt("price"),
                            rs.getString("flight_number"),
                            departureAirport,
                            arrivalAirport,
                            rs.getInt("numbre_place")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving flight with ID " + idFlight + ": " + e.getMessage());
            e.printStackTrace();
        }

        return flight;
    }

    public List<Flight> getFlight() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT f.*, da.idAirport AS dep_airport_id, da.nameAirport AS dep_airport_name, " +
                "aa.idAirport AS arr_airport_id, aa.nameAirport AS arr_airport_name " +
                "FROM flight f " +
                "LEFT JOIN airport da ON f.departure_Airport_id = da.idAirport " +
                "LEFT JOIN airport aa ON f.arrival_Airport_id = aa.idAirport";

        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // Create departure airport object
                Airport departureAirport = new Airport(
                        rs.getInt("dep_airport_id"),
                        rs.getString("dep_airport_name"),
                        null, // Add location if needed
                        null  // Add code if needed
                );

                // Create arrival airport object
                Airport arrivalAirport = new Airport(
                        rs.getInt("arr_airport_id"),
                        rs.getString("arr_airport_name"),
                        null, // Add location if needed
                        null  // Add code if needed
                );

                // Create flight object
                Flight flight = new Flight(
                        rs.getInt("idFlight"),
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getTimestamp("departureTime"),
                        rs.getTimestamp("arrivalTime"),
                        rs.getInt("price"),
                        rs.getString("flight_number"),
                        departureAirport,
                        arrivalAirport,
                        rs.getInt("numbre_place")
                );

                // Add flight to the list
                flights.add(flight);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving flights: " + e.getMessage());
            e.printStackTrace();
        }

        return flights;
    }



    public List<String> getCities() {
        List<String> cities = new ArrayList<>();
        String query = "SELECT DISTINCT location FROM airport";

        try (PreparedStatement statement = cnx.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cities.add(resultSet.getString("location"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }








}
