package models;

public class Recompense {
    private int id;
    private int programmeId;
    private String nom;
    private String description;
    private int pointsRequis;
    private String photo;
    private int userId; // Add this field
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Constructor
    public Recompense(int id, int programmeId, String nom, String description, int pointsRequis, String photo) {
        this.id = id;
        this.programmeId = programmeId;
        this.nom = nom;
        this.description = description;
        this.pointsRequis = pointsRequis;
        this.photo = photo;
    }
    public Recompense( int programmeId, String nom, String description, int pointsRequis, String photo) {
        this.programmeId = programmeId;
        this.nom = nom;
        this.description = description;
        this.pointsRequis = pointsRequis;
        this.photo = photo;
        this.status = 1;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgrammeId() {
        return programmeId;
    }

    public void setProgrammeId(int programmeId) {
        this.programmeId = programmeId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsRequis() {
        return pointsRequis;
    }

    public void setPointsRequis(int pointsRequis) {
        this.pointsRequis = pointsRequis;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}