package util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import models.Airport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AirportCSVUtil {

    public static List<Airport> loadAirportsFromCSV(String filePath) {
        List<Airport> airports = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // Skip the header row
            while ((nextLine = reader.readNext()) != null) {
                // Ensure the row has enough columns
                if (nextLine.length >= 14) {
                    int id = Integer.parseInt(nextLine[0]); // id
                    String name = nextLine[3]; // name
                    String location = nextLine[10]; // municipality
                    String code = nextLine[13]; // iata_code

                    airports.add(new Airport(id, name, location, code));
                } else {
                    System.out.println("Skipping invalid row: " + String.join(",", nextLine));
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return airports;
    }
}
