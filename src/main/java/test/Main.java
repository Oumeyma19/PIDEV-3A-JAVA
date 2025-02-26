package test;
import services.FlightService;
import models.Airport;
import models.Flight;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


public class Main {

    public static void main(String args[]) throws SQLException {

        FlightService flightService = new FlightService();

        // Create Airport objects
        Airport departureAirport = new Airport(1, "JFK International Airport");
        Airport arrivalAirport = new Airport(2, "Los Angeles International Airport");

        // Create a Flight object
        Flight flight = new Flight(
                1000, // Flight ID
                "New York",
                "Los Angeles",
                Timestamp.valueOf("2025-02-20 08:00:00"),
                Timestamp.valueOf("2025-02-20 11:00:00"),
                300, // Price
                "AA1000",
                departureAirport,
                arrivalAirport,
                100
        );

        try {
            // Test Add Flight
            System.out.println("Adding flight...");
            flightService.ajouter(flight);

            // Test Display Flights
            System.out.println("Retrieving all flights...");
            List<Flight> flights = flightService.afficher();
            for (Flight f : flights) {
                System.out.println(f);
            }

            // Test Modify Flight
            System.out.println("Modifying flight...");
            flight.setPrice(350);
            flightService.modifier(flight, "AA100");

            // Test Delete Flight
            System.out.println("Deleting flight...");
            flightService.supprimer(flight);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
