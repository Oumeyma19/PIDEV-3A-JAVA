package models;

public class AvisTour {
    private int id;
    private int clientId;
    private int tourId;
    private int etoile;
    private String commentaire;

    public AvisTour(int id, int clientId, int tourId, int etoile, String commentaire) {
        this.id = id;
        this.clientId = clientId;
        this.tourId = tourId;
        this.etoile = etoile;
        this.commentaire = commentaire;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getTourId() { return tourId; }
    public void setTourId(int tourId) { this.tourId = tourId; }

    public int getEtoile() { return etoile; }
    public void setEtoile(int etoile) { this.etoile = etoile; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
