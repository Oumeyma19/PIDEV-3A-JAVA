package models;

public class ProgrammeFidelite {
    private int id;
    private String nomProgramme;
    private int points;
    private String photo;

    public ProgrammeFidelite(int id, String nomProgramme, int points, String photo) {
        this.id = id;
        this.nomProgramme = nomProgramme;
        this.points = points;
        this.photo = photo;
    }

    public ProgrammeFidelite() {

    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNomProgramme() { return nomProgramme; }
    public int getPoints() { return points; }
    public String getPhoto() { return photo; }

    public void setId(int id) { this.id = id; }
    public void setNomProgramme(String nomProgramme) { this.nomProgramme = nomProgramme; }
    public void setPoints(int points) { this.points = points; }
    public void setPhoto(String photo) { this.photo = photo; }
}