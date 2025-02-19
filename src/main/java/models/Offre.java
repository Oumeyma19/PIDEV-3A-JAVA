package models;

public class Offre {
    private int id;
    private String title, description;
    private double price;
    private String startDate, endDate;
    private String imagePath; // New field

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
    }
    public Offre(String title, String description, double price, String startDate, String endDate) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
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
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
