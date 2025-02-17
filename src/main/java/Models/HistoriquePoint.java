package Models;

public class HistoriquePoint {
    private int id;
    private int clientId;

    private int points;
    private String date;

    public HistoriquePoint(int id, int clientId, int points, String date) {
        this.id = id;
        this.clientId = clientId;

        this.points = points;
        this.date = date;
    }

    public int getId() { return id; }
    public int getClientId() { return clientId; }

    public int getPoints() { return points; }
    public String getDate() { return date; }


    public void setPoints(int points) { this.points = points; }
    public void setDate(String date) { this.date = date; }
}