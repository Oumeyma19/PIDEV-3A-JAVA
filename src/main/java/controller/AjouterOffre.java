package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tools.MyDataBase;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AjouterOffre {

    @FXML
    private TextField offreNameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField prixField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button btnChooseImage;

    @FXML
    private ImageView imageView;

    private String imagePath = null; // Store selected image path

    private final Connection cnx = MyDataBase.getInstance().getCnx(); // Get DB connection

    @FXML
    public void initialize() {
        // Set button actions
        ajouterButton.setOnAction(event -> ajouterOffre());
        btnChooseImage.setOnAction(event -> chooseImage()); // ✅ Corrected
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imagePath = file.toURI().toString(); // ✅ Store image path
            imageView.setImage(new Image(imagePath)); // ✅ Display selected image
        } else {
            System.out.println("No image selected.");
        }
    }

    private void ajouterOffre() {
        String title = offreNameField.getText();
        String description = descriptionField.getText();
        String prixText = prixField.getText();
        String startDate = (startDatePicker.getValue() != null) ? startDatePicker.getValue().toString() : null;
        String endDate = (endDatePicker.getValue() != null) ? endDatePicker.getValue().toString() : null;

        if (title.isEmpty() || description.isEmpty() || prixText.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        try {
            double price = Double.parseDouble(prixText); // Convert price to double
            saveToDatabase(title, description, price, startDate, endDate, imagePath);
            System.out.println("Offer successfully added!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid price! Please enter a valid number.");
        }
    }

    private void saveToDatabase(String title, String description, double price, String startDate, String endDate, String imagePath) {
        String sql = "INSERT INTO offers (title, description, price, start_date, end_date, image_path) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            stmt.setString(6, imagePath != null ? imagePath : ""); // ✅ Ensure null-safe insertion
            stmt.executeUpdate();
            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving to database!");
        }
    }
}
