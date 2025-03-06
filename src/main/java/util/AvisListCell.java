package util;

import models.AvisHebergement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.Rating;

import java.util.function.Consumer;

public class AvisListCell extends ListCell<AvisHebergement> {

    private final VBox ratingContainer;
    private final Text text;
    private final Rating rating;
    private final Button btnSupp;
    private final Button btnModif;
    private final HBox container;
    private final VBox btnContainer;
    private final int ownerId;

    public AvisListCell(
            int ownerId,
            Consumer<AvisHebergement> deleteCallback,
            Consumer<AvisHebergement> updateCallback
    ) {
        this.ownerId = ownerId;

        text = new Text();
        rating = new Rating(5);

        btnSupp = new Button("Supprimer");
        btnModif = new Button("Modifier");

        btnSupp.setOnAction(e -> {
            if (!isEmpty()) {
                deleteCallback.accept(getItem());
            }
        });
        btnModif.setOnAction(e -> {
            if (!isEmpty()) {
                updateCallback.accept(getItem());
            }
        });

        rating.setPartialRating(true);
        rating.setDisable(true);
        rating.setUpdateOnHover(false);

        ratingContainer = new VBox(rating, text);
        ratingContainer.setSpacing(10);

        btnContainer = new VBox(btnModif, btnSupp);
        btnContainer.setSpacing(10);

        container = new HBox(ratingContainer, btnContainer);
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setSpacing(10);
    }

    @Override
    protected void updateItem(AvisHebergement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            text.textProperty().unbind();
            text.setText("");
            rating.setRating(0);
        } else {

            if (item.getUser().getId() != ownerId) {
                btnSupp.setVisible(false);
                btnModif.setVisible(false);
            }
            else {
                btnSupp.setVisible(true);
                btnModif.setVisible(true);}

            text.textProperty().bindBidirectional(new SimpleStringProperty(item.getComment()));
            rating.ratingProperty().bindBidirectional(new SimpleDoubleProperty(item.getReview()));
        }
        setGraphic(container);
    }
}
