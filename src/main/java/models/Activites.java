package models;

public class Activites {
    private int id;
    private String nomActivite;    // Name of the activity
    private String dateDebut;      // Start date of the activity
    private String dateFin;        // End date of the activity
    private String localisation;   // Location of the activity
    private String photo;          // Photo URL or path for the activity
    private String description;    // Description of the activity
    private Tour tour;             // Associated Tour object

    // Constructor for creating an activity with all attributes
    public Activites(int id, String nomActivite, String dateDebut, String dateFin, String localisation, String photo, String description, Tour tour) {
        this.id = id;
        this.nomActivite = nomActivite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.localisation = localisation;
        this.photo = photo;
        this.description = description;
        this.tour = tour;
    }

    // Constructor for creating an activity without an ID (useful for inserting into the database)
    public Activites(String nomActivite, String dateDebut, String dateFin, String localisation, String photo, String description, Tour tour) {
        this.nomActivite = nomActivite;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.localisation = localisation;
        this.photo = photo;
        this.description = description;
        this.tour = tour;
    }

    public Activites() {

    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomActivite() { return nomActivite; }
    public void setNomActivite(String nomActivite) { this.nomActivite = nomActivite; }

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    @Override
    public String toString() {
        return "Activites{" +
                "id=" + id +
                ", nomActivite='" + nomActivite + '\'' +
                ", dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", localisation='" + localisation + '\'' +
                ", photo='" + photo + '\'' +
                ", description='" + description + '\'' +
                ", tour=" + tour +
                '}';
    }
}