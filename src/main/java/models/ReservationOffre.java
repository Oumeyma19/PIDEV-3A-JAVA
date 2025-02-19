package models;

import java.time.LocalDate;

public class ReservationOffre {

    private int id;
    private Offre offre;
    private LocalDate startDate; // New field for start date
    private LocalDate endDate;   // New field for end date
    private String status;
    private User user;           // Reference to User object
    private int numberOfAdults;  // New field for number of adults
    private int numberOfChildren; // New field for number of children

    // Constructor without ID (for creating new reservations)
    public ReservationOffre(Offre offre, LocalDate startDate, LocalDate endDate, String status, User user, int numberOfAdults, int numberOfChildren) {
        this.offre = offre;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.user = user;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }

    // Constructor with ID (for retrieving existing reservations)
    public ReservationOffre(int id, Offre offre, LocalDate startDate, LocalDate endDate, String status, User user, int numberOfAdults, int numberOfChildren) {
        this.id = id;
        this.offre = offre;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.user = user;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }
    public String getOfferTitle() {
        return offre.getTitle();
    }
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    @Override
    public String toString() {
        return "ReservationOffre{" +
                "id=" + id +
                ", offre=" + offre +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", numberOfAdults=" + numberOfAdults +
                ", numberOfChildren=" + numberOfChildren +
                '}';
    }
}