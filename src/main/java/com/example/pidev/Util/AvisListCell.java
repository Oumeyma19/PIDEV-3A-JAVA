package com.example.pidev.Util;

import com.example.pidev.models.AvisHebergement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.Rating;

public class AvisListCell extends ListCell<AvisHebergement> {

    private final VBox vBox;
    private final Text text;
    private final Rating rating;

    public AvisListCell() {
        text = new Text();
        rating = new Rating(5);
        rating.setPartialRating(true);
        rating.setDisable(true);
        rating.setUpdateOnHover(false);
        vBox = new VBox(text, rating);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);
    }

    @Override
    protected void updateItem(AvisHebergement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            text.textProperty().unbind();
            text.setText("");
            rating.setRating(0);
        } else {
            text.textProperty().bindBidirectional(new SimpleStringProperty(item.getComment()));
            rating.ratingProperty().bindBidirectional(new SimpleDoubleProperty(item.getReview()));
        }
        setGraphic(vBox);
    }
}
