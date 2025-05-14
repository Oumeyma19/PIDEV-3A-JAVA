package models;

import java.time.LocalDate;

public class ReservationOffre {

    private int id;
    private Offre offre;
    private LocalDate start_date; // New field for start date
    private LocalDate end_date;   // New field for end date
    private String status;
    private User user;           // Reference to User object
    private int numberOfAdults;  // New field for number of adults
    private int numberOfChildren; // New field for number of children

    // Constructor without ID (for creating new reservations)
    public ReservationOffre(Offre offre, LocalDate start_date, LocalDate end_date, String status, User user, int numberOfAdults, int numberOfChildren) {
        this.offre = offre;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.user = user;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }

    // Constructor with ID (for retrieving existing reservations)
    public ReservationOffre(int id, Offre offre, LocalDate start_date, LocalDate end_date, String status, User user, int numberOfAdults, int numberOfChildren) {
        this.id = id;
        this.offre = offre;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.user = user;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }

    public ReservationOffre() {

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

    public LocalDate getstart_date() {
        return start_date;
    }

    public void setstart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getend_date() {
        return end_date;
    }

    public void setend_date(LocalDate end_date) {
        this.end_date = end_date;
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
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", numberOfAdults=" + numberOfAdults +
                ", numberOfChildren=" + numberOfChildren +
                '}';
    }
}