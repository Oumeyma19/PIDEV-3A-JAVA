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
    private List<String> photos;
    // ✅ Add photo list

    // Constructor for creating a tour with photos
    public Tour(String title, String description, double price, String location, String date, int guideId, List<String> photos) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.date = date;
        this.guideId = guideId;
        this.photos = photos;
    }

    // Constructor for creating a tour without photos
    public Tour(String title, String description, double price, String location, String date, int guideId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.date = date;
        this.guideId = guideId;
    }

    // Constructor for retrieving tours from the database
    public Tour(int id, String title, String description, double price, String location, String date, int guideId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.date = date;
        this.guideId = guideId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public int getGuideId() { return guideId; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }
    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}