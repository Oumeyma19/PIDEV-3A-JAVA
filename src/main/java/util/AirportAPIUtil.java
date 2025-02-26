package util;

import javafx.scene.control.Alert;
import models.Airport;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AirportAPIUtil {

    private static final String API_KEY = "1ecff28b5511d60af3aef353857b2d33";
    private static final String BASE_URL = "http://api.aviationstack.com/v1/airports";

    public static List<Airport> searchAirports(String query, List<Airport> airports) {
        List<Airport> filteredAirports = new ArrayList<>();

        for (Airport airport : airports) {
            if (airport.getNameAirport().toLowerCase().contains(query.toLowerCase())) {
                filteredAirports.add(airport);
            }
        }

        System.out.println("Airports found: " + filteredAirports.size()); // Debug: Print number of airports found
        return filteredAirports;
    }
   /* public static List<Airport> searchAirports(String query) {
        // Hardcoded data for testing
        List<Airport> airports = List.of(
                new Airport(1, "Tunis-Carthage International Airport", "Tunis", "TUN"),
                new Airport(2, "JFK International Airport", "New York", "JFK"),
                new Airport(3, "Los Angeles International Airport", "Los Angeles", "LAX")
        );
        System.out.println("Using hardcoded data: " + airports.size() + " airports found"); // Debug: Print hardcoded data
        return airports;
    }*/
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}