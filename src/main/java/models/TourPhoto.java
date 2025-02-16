package models;

public class TourPhoto {
    private int id;
    private int tourId;
    private String photo;

    public TourPhoto(int tourId, String photo) {
        this.tourId = tourId;
        this.photo = photo;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTourId() { return tourId; }
    public void setTourId(int tourId) { this.tourId = tourId; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
