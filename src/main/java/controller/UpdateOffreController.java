package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Offre;
import tools.MyDataBase;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateOffreController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ImageView imageView;  // ✅ Image Preview
    @FXML private Button btnChooseImage;
    @FXML private Button btnUpdate;

    private Offre selectedOffer;
    private String selectedImagePath; // ✅ Store selected image path

    public void setOfferData(Offre offer) {
        this.selectedOffer = offer;
        titleField.setText(offer.getTitle());
        descriptionField.setText(offer.getDescription());
        priceField.setText(String.valueOf(offer.getPrice()));
        startDatePicker.setValue(java.time.LocalDate.parse(offer.getStartDate()));
        endDatePicker.setValue(java.time.LocalDate.parse(offer.getEndDate()));

        if (offer.getImagePath() != null && !offer.getImagePath().isEmpty()) {
            imageView.setImage(new Image(offer.getImagePath()));
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
            selectedImagePath = selectedFile.toURI().toString(); // ✅ Convert to URI format
            imageView.setImage(new Image(selectedImagePath));
        }
    }

    @FXML
    private void updateOffer() {
        if (selectedOffer == null) {
            System.out.println("Aucune offre sélectionnée !");
            return;
        }

        String query = "UPDATE offers SET title=?, description=?, price=?, start_date=?, end_date=?, image_path=? WHERE id=?";

        try (Connection connection = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, titleField.getText());
            ps.setString(2, descriptionField.getText());
            ps.setDouble(3, Double.parseDouble(priceField.getText()));
            ps.setString(4, startDatePicker.getValue().toString());
            ps.setString(5, endDatePicker.getValue().toString());
            ps.setString(6, selectedImagePath); // ✅ Update image path
            ps.setInt(7, selectedOffer.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Offre mise à jour avec succès !");
                Stage stage = (Stage) btnUpdate.getScene().getWindow();
                stage.close(); // ✅ Close the window after updating
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
