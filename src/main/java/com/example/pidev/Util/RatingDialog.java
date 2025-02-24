package com.example.pidev.Util;


import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.Rating;

public class RatingDialog extends Dialog<AvisProperties> {

    private final AvisProperties avis;

    private TextField comment;
    private Rating rating;

    public RatingDialog(AvisProperties avis) {
        super();
        this.setTitle("Ajouter Avis");
        this.avis = avis;
        buildUI();
        setPropertyBindings();
        setResultConverter();
        setResizable(false);
    }

    private void buildUI() {
        Pane pane = createGridPane();
        getDialogPane().setContent(pane);

        getDialogPane().getStylesheets().add(getClass().getResource("/avisDialogStyle.css").toExternalForm());

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button button = (Button) getDialogPane().lookupButton(ButtonType.OK);

        button.addEventFilter(ActionEvent.ACTION, new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if (!validateDialog()) {
                    event.consume();
                }
            }

            private boolean validateDialog() {
                return !comment.getText().isEmpty();
            }
        });

        getDialogPane().expandableContentProperty().set(new Label("This is the expandable ontent area"));
        getDialogPane().setExpanded(true);
    }

    private void setPropertyBindings() {
        comment.textProperty().bindBidirectional(avis.commentProperty());
        rating.ratingProperty().bindBidirectional(avis.ratingProperty());
    }

    private void setResultConverter() {
        Callback<ButtonType, AvisProperties> avisResultConverter = param -> {
            if (param == ButtonType.OK) {
                return avis;
            } else {
                return null;
            }
        };
        setResultConverter(avisResultConverter);
    }

    public Pane createGridPane() {
        VBox content = new VBox(10);

        Label firstNameLabel = new Label("Votre commentaire");
        this.comment = new TextField();
        this.rating = new Rating();
        rating.setUpdateOnHover(true);
        rating.setPartialRating(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.add(firstNameLabel, 0, 0);
        grid.add(comment, 1, 0);
        GridPane.setHgrow(this.comment, Priority.ALWAYS);
        grid.add(rating, 1, 1);
        GridPane.setHgrow(this.rating, Priority.ALWAYS);

        content.getChildren().add(grid);

        return content;
    }
}
