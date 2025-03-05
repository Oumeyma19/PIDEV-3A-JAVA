package models;

public class AvisHebergement {
    private int idAvis;
    private User user;
    private Hebergements hebergements;
    private String comment;
    private float review;

    public AvisHebergement() {
    }

    public AvisHebergement(String comment, int idAvis, Hebergements hebergements, User user, float review) {
        this.comment = comment;
        this.idAvis = idAvis;
        this.hebergements = hebergements;
        this.user = user;
        this.review = review;
    }

    public AvisHebergement(String comment, float review, User user, Hebergements hebergements) {
        this.comment = comment;
        this.review = review;
        this.user = user;
        this.hebergements = hebergements;
    }

    public String getComment() {
        return comment;
    }

    public int getIdAvis() {
        return idAvis;
    }

    public Hebergements getHebergements() {
        return hebergements;
    }

    public User getUser() {
        return user;
    }

    public float getReview() {
        return review;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setIdAvis(int idAvis) {
        this.idAvis = idAvis;
    }

    public void setHebergements(Hebergements hebergements) {
        this.hebergements = hebergements;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReview(float review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "AvisHebergement{" +
                "comment='" + comment + '\'' +
                ", idAvis=" + idAvis +
                ", idUser=" + user +
                ", idHeberg=" + hebergements +
                ", review=" + review +
                '}';
    }
}
