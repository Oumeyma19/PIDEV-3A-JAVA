package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Offre;
import services.OffreService;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateOffreController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ImageView imageView;
    @FXML private Button btnChooseImage, btnUpdate;

    private final OffreService offreService = new OffreService(); // ✅ Use service
    private Offre selectedOffer;
    private String selectedImagePath;

    public void setOfferData(Offre offer) {
        this.selectedOffer = offer;
        titleField.setText(offer.getTitle());
        descriptionField.setText(offer.getDescription());
        priceField.setText(String.valueOf(offer.getPrice()));
        startDatePicker.setValue(LocalDate.parse(offer.getStartDate()));
        endDatePicker.setValue(LocalDate.parse(offer.getEndDate()));

        if (offer.getImagePath() != null && !offer.getImagePath().isEmpty()) {
            selectedImagePath = offer.getImagePath();
            imageView.setImage(new Image(selectedImagePath));
        }
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedImagePath = selectedFile.toURI().toString();
            imageView.setImage(new Image(selectedImagePath));
        }
    }

    @FXML
    private void updateOffer() {
        if (!validateInput()) return;

        selectedOffer.setTitle(titleField.getText());
        selectedOffer.setDescription(descriptionField.getText());
        selectedOffer.setPrice(Double.parseDouble(priceField.getText()));
        selectedOffer.setStartDate(startDatePicker.getValue().toString());
        selectedOffer.setEndDate(endDatePicker.getValue().toString());
        selectedOffer.setImagePath(selectedImagePath);

        try {
            offreService.modifier(selectedOffer); // ✅ Use service
            showAlert("Succès", "Offre mise à jour avec succès !");
            closeWindow();
        } catch (SQLException e) {
            showAlert("Erreur", "Échec de la mise à jour : " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (titleField.getText().isEmpty() || descriptionField.getText().isEmpty() || priceField.getText().isEmpty() ||
                startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Validation", "Veuillez remplir tous les champs !");
            return false;
        }
        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation", "Le prix doit être un nombre valide !");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnUpdate.getScene().getWindow();
        stage.close();
    }
}
