package Models;

public class ProgrammeFidelite {
    private int id;

    private String nomProgramme;
    private int points;

    public ProgrammeFidelite(int id, String nomProgramme, int points) {
        this.id = id;

        this.nomProgramme = nomProgramme;
        this.points = points;
    }

    public int getId() { return id; }

    public String getNomProgramme() { return nomProgramme; }
    public int getPoints() { return points; }

    public void setNomProgramme(String nomProgramme) { this.nomProgramme = nomProgramme; }
    public void setPoints(int points) { this.points = points; }
}