package models;

public class AvisTour {
    private int id;
    private User user;  // Use User object instead of clientId
    private int tourId;
    private int etoile;
    private String commentaire;

    public AvisTour(int id, User user, int tourId, int etoile, String commentaire) {
        this.id = id;
        this.user = user;
        this.tourId = tourId;
        this.etoile = etoile;
        this.commentaire = commentaire;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {  // Change from getClientId to getUser
        return user;
    }

    public void setUser(User user) {  // Change from setClientId to setUser
        this.user = user;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public int getEtoile() {
        return etoile;
    }

    public void setEtoile(int etoile) {
        this.etoile = etoile;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
