package models;

import java.util.List;

public class Tour {
    private int id;
    private String title;
    private String description;
    private double price;
    private String location;
    private String date;
    private int guideId;
    private String photo; // Changed from List<String> to String
    private int nbPlaceDisponible;
    private int nbPlaceReserver;
    private TourType type;
    private List<Activites> activities; // New field for activities


    public Tour() {
    }

    // Enum for tour type
    public enum TourType {
        Touristique, Académique, Religieux, Esthétique
    }

    // Full Constructor
    public Tour(int id, String title, String description, double price, String location, String date, int guideId, String photo, int nbPlaceDisponible, int nbPlaceReserver, TourType type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.date = date;
        this.guideId = guideId;
        this.photo = photo; // Single photo path
        this.nbPlaceDisponible = nbPlaceDisponible;
        this.nbPlaceReserver = nbPlaceReserver;
        this.type = type;
    }

    public Tour(String title, String description, double price, String location, String date, int guideId, int nbPlaceDisponible, int nbPlaceReserver, String photo, String type) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.date = date;
        this.guideId = guideId;
        this.nbPlaceDisponible = nbPlaceDisponible;
        this.nbPlaceReserver = nbPlaceReserver;
        this.photo = photo; // Single photo path
        this.type = TourType.valueOf(type);
    }

    // Constructor without ID
    public Tour(String title, String description, double price, String location, String date, int guideId, String photo, int nbPlaceDisponible, int nbPlaceReserver, String type) {
        this(0, title, description, price, location, date, guideId, photo, nbPlaceDisponible, nbPlaceReserver, TourType.valueOf(type));
    }

    // Constructor with ID only
    public Tour(int id) {
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }

    public String getPhoto() { return photo; } // Updated getter
    public void setPhoto(String photo) { this.photo = photo; } // Updated setter

    public int getNbPlaceDisponible() { return nbPlaceDisponible; }
    public void setNbPlaceDisponible(int nbPlaceDisponible) { this.nbPlaceDisponible = nbPlaceDisponible; }

    public int getNbPlaceReserver() { return nbPlaceReserver; }
    public void setNbPlaceReserver(int nbPlaceReserver) { this.nbPlaceReserver = nbPlaceReserver; }

    public TourType getType() { return type; }
    public void setType(TourType type) { this.type = type; }
    public List<Activites> getActivities() {
        return activities;
    }

    public void setActivities(List<Activites> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", guideId=" + guideId +
                ", photo='" + photo + '\'' + // Updated to single photo
                ", nbPlaceDisponible=" + nbPlaceDisponible +
                ", nbPlaceReserver=" + nbPlaceReserver +
                ", type=" + type +
                '}';
    }
}