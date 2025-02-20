package Models;

public class Recompense {
    private int id;
    private int programmeId;
    private String nom;
    private String description;
    private int pointsRequis;
    private String photo;
    private int id_user;
    private int status;

    public Recompense(int id, int programmeId, String nom, int pointsRequis,String photo) {
        this.id = id;
        this.programmeId = programmeId;
        this.nom = nom;
        this.description = description;
        this.pointsRequis = pointsRequis;
        this.photo = photo;
        this.id_user = 0;
        this.status = 1;

    }
    public Recompense( int programmeId, String nom,String description , int pointsRequis,String photo) {
        this.programmeId = programmeId;
        this.nom = nom;
        this.description = description;
        this.pointsRequis = pointsRequis;
        this.photo = photo;

    }

    public Recompense(int id, int programmeId, String description, int pointsRequis, Object o, String photo) {
    }

    public Recompense(int id, int programmeId, String nom, String Description, int pointsRequis, String photo) {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProgrammeId(int programmeId) {
        this.programmeId = programmeId;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Integer.parseInt(status);
    }

    public int getId() { return id; }
    public int getProgrammeId() { return programmeId; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public int getPointsRequis() { return pointsRequis; }

    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setPointsRequis(int pointsRequis) { this.pointsRequis = pointsRequis; }
}