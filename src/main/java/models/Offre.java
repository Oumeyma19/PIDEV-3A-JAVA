package models;

import java.util.List;
import java.util.stream.Collectors;

public class Offre {
    private int id;
    private String title, description;
    private double price; // Discounted price
    private double originalPrice; // Original price (sum of hebergements, tours, and flights)
    private String startDate, endDate;
    private String imagePath;
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

    public Offre(int id, String title, String description, double price, String startDate, String endDate, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imagePath = imagePath;
        this.originalPrice = calculateOriginalPrice(); // Calculate original price when object is created
    }

    public Offre(String title, String description, double price, String startDate, String endDate) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.originalPrice = calculateOriginalPrice(); // Calculate original price when object is created
    }

    public Offre(int id) {
        this.id = id;
    }

    public Offre(int id, String title, String description, double price, String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
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
            return "No hebergements";
        }
        return hebergements.stream()
                .map(Hebergements::getNomHeberg) // Replace getNomHeberg() with the correct method
                .collect(Collectors.joining(", "));
    }

    public String getToursStr() {
        if (tours == null || tours.isEmpty()) {
            return "No tours";
        }
        return tours.stream()
                .map(Tour::getTitle) // Replace getTitle() with the correct method
                .collect(Collectors.joining(", "));
    }

    public String getFlightsStr() {
        if (flights == null || flights.isEmpty()) {
            return "No flights";
        }
        return flights.stream()
                .map(Flight::getFlightNumber) // Replace getFlightNumber() with the correct method
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

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

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
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", imagePath='" + imagePath + '\'' +
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