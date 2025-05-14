package models;

import java.util.List;
import java.util.stream.Collectors;

public class Offre {
    private int id;
    private String title, description;
    private double price; // Discounted price
    private double originalPrice; // Original price (sum of hebergements, tours, and flights)
    private String start_date, end_date;
    private String image_path;
    private List<Hebergements> hebergements;
    private List<Tour> tours;
    private List<Flight> flights;
    private List<ReservationOffre> reservations;


    // Constructors
    public Offre() {
    }

    public Offre(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Offre(int id, String title, String description, double price, String start_date, String end_date, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.image_path = imagePath;
        this.originalPrice = calculateOriginalPrice(); // Calculate original price when object is created
    }

    public Offre(String title, String description, double price, String start_date, String end_date) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.originalPrice = calculateOriginalPrice(); // Calculate original price when object is created
    }

    public Offre(int id) {
        this.id = id;
    }

    public Offre(int id, String title, String description, double price, String start_date, String end_date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.originalPrice = calculateOriginalPrice(); // Calculate original price when object is created
    }

    // Getters and Setters
    public List<Hebergements> getHebergements() { return hebergements; }
    public void setHebergements(List<Hebergements> hebergements) {
        this.hebergements = hebergements;
        this.originalPrice = calculateOriginalPrice(); // Recalculate original price when hebergements are updated
    }

    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) {
        this.tours = tours;
        this.originalPrice = calculateOriginalPrice(); // Recalculate original price when tours are updated
    }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) {
        this.flights = flights;
        this.originalPrice = calculateOriginalPrice(); // Recalculate original price when flights are updated
    }

    public String getHebergementsStr() {
        if (hebergements == null || hebergements.isEmpty()) {
            return "None";  // Instead of an empty string
        }
        return hebergements.stream()
                .map(Hebergements::getNomHeberg)  // Replace with your actual attribute
                .collect(Collectors.joining(", "));
    }

    public String getToursStr() {
        if (tours == null || tours.isEmpty()) {
            return "None";  // Instead of an empty string
        }
        return tours.stream()
                .map(Tour::getTitle)  // Replace with your actual attribute
                .collect(Collectors.joining(", "));
    }

    public String getFlightsStr() {
        if (flights == null || flights.isEmpty()) {
            return "None";  // Instead of an empty string
        }
        return flights.stream()
                .map(Flight::getFlightNumber)  // Replace with your actual attribute
                .collect(Collectors.joining(", "));
    }


    public List<ReservationOffre> getReservations() { return reservations; }
    public void setReservations(List<ReservationOffre> reservations) { this.reservations = reservations; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStartDate() { return start_date; }
    public void setStartDate(String start_date) { this.start_date = start_date; }

    public String getEndDate() { return end_date; }
    public void setEndDate(String end_date) { this.end_date = end_date; }

    public String getImagePath() { return image_path; }
    public void setImagePath(String imagePath) { this.image_path = imagePath; }

    public double getOriginalPrice() {
        return originalPrice;
    }

    // Method to calculate the original price
    private double calculateOriginalPrice() {
        double total = 0;

        // Add hebergements prices
        if (hebergements != null) {
            for (Hebergements h : hebergements) {
                total += h.getPrixHeberg();
            }
        }

        // Add tours prices
        if (tours != null) {
            for (Tour t : tours) {
                total += t.getPrice();
            }
        }

        // Add flights prices
        if (flights != null) {
            for (Flight f : flights) {
                total += f.getPrice();
            }
        }

        return total;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", image_path='" + image_path + '\'' +
                ", hebergements=" + hebergements +
                ", tours=" + tours +
                ", flights=" + flights +
                ", reservations=" + reservations +
                '}';
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

}