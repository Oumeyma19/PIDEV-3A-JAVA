package models;

public class AvisHebergement {
    private int idAvis;
    private User idUser;
    private Hebergements idHeberg;
    private String comment;
    private float review;

    public AvisHebergement() {
    }
    public AvisHebergement(String comment, int idAvis, Hebergements idHeberg, User idUser, float review) {
        this.comment = comment;
        this.idAvis = idAvis;
        this.idHeberg = idHeberg;
        this.idUser = idUser;
        this.review = review;
    }

    public AvisHebergement(String comment, float review, User idUser, Hebergements idHeberg) {
        this.comment = comment;
        this.review = review;
        this.idUser = idUser;
        this.idHeberg = idHeberg;
    }

    public String getComment() {
        return comment;
    }

    public int getIdAvis() {
        return idAvis;
    }

    public Hebergements getIdHeberg() {
        return idHeberg;
    }

    public User getIdUser() {
        return idUser;
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

    public void setIdHeberg(Hebergements idHeberg) {
        this.idHeberg = idHeberg;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    public void setReview(float review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "AvisHebergement{" +
                "comment='" + comment + '\'' +
                ", idAvis=" + idAvis +
                ", idUser=" + idUser +
                ", idHeberg=" + idHeberg +
                ", review=" + review +
                '}';
    }
}
