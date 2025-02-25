package Util;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AvisProperties {
    private final StringProperty comment;
    private final FloatProperty rating;

    public AvisProperties(String comment, Float rating) {
        this.comment = new SimpleStringProperty(comment);
        this.rating = new SimpleFloatProperty(rating);
    }

    public String getComment() {
        return comment.get();
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public Float getRating() {
        return rating.get();
    }

    public FloatProperty ratingProperty() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating.set(rating);
    }

    @Override
    public String toString() {
        return "AvisProperties{" +
                "comment=" + comment +
                ", rating=" + rating +
                '}';
    }
}
