package Models;

public class Recompense {
    private int id;
    private int programmeId;
    private String nom;
    private int pointsRequis;
    private String photo;

    public Recompense(int id, int programmeId, String nom, int pointsRequis,String photo) {
        this.id = id;
        this.programmeId = programmeId;
        this.nom = nom;
        this.pointsRequis = pointsRequis;
        this.photo = photo;

    }
    public Recompense( int programmeId, String nom, int pointsRequis,String photo) {
        this.programmeId = programmeId;
        this.nom = nom;
        this.pointsRequis = pointsRequis;
        this.photo = photo;

    }

    public Recompense(int id, int programmeId, String description, int pointsRequis, Object o, String photo) {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getId() { return id; }
    public int getProgrammeId() { return programmeId; }
    public String getNom() { return nom; }
    public int getPointsRequis() { return pointsRequis; }

    public void setNom(String nom) { this.nom = nom; }
    public void setPointsRequis(int pointsRequis) { this.pointsRequis = pointsRequis; }
}