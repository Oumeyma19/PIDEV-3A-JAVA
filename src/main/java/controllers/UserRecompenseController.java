package controllers;

import models.ProgrammeFidelite;
import models.Recompense;
import models.User;
import services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserRecompenseController implements Initializable {


    @FXML
    private Label nomUserLabel;


    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private FlowPane claimedRecompensesFlowPane;

    @FXML
    private Text totalPointsText;


    @FXML
    private Text rankText;

    @FXML
    private Label rewardsCountLabel;
    private ProgrammeFideliteService programmeFideliteService = new ProgrammeFideliteService();
    private User currentUser;
    private RecompenseService recompenseService = new RecompenseService();

    // Store original list for filtering
    private List<Recompense> originalRecompensesList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize filter options
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "All Rewards",
                "Points: Low to High",
                "Points: High to Low",
                "Newest First",
                "Oldest First"
        );
        filterComboBox.setItems(filterOptions);
        filterComboBox.setValue("All Rewards");

        // Initialize events
        searchField.setOnKeyReleased(this::handleSearch);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        nomUserLabel.setText(user.getFirstname() + " " + user.getLastname());
// Add to your setCurrentUser method:
        totalPointsText.setText(String.valueOf(user.getPointsfid()));
        // Get the user's program ID (you'll need to add this to your User model)
        int programId = user.getNivfid();

        // Get program name from service
        ProgrammeFidelite programme = programmeFideliteService.getProgrammeById(programId);

        // Update UI elements
        if (programme != null) {
            rankText.setText(programme.getNomProgramme());
        } else {
            rankText.setText("Standard");
        }
        // Load claimed recompenses
        loadClaimedRecompenses();
    }


    private void loadClaimedRecompenses() {
        if (claimedRecompensesFlowPane == null) return;
        claimedRecompensesFlowPane.getChildren().clear();

        if (currentUser != null) {
            originalRecompensesList = recompenseService.getRecompensesByUserId(currentUser.getId());
            updateRecompensesDisplay(originalRecompensesList);
        }
    }

    private void updateRecompensesDisplay(List<Recompense> recompenses) {
        claimedRecompensesFlowPane.getChildren().clear();

        for (Recompense recompense : recompenses) {
            VBox recompenseContainer = createRecompenseContainer(recompense);
            claimedRecompensesFlowPane.getChildren().add(recompenseContainer);
        }

        // Update the count label
        rewardsCountLabel.setText(recompenses.size() + " rewards");
    }

    private VBox createRecompenseContainer(Recompense recompense) {
        VBox recompenseContainer = new VBox(10);
        recompenseContainer.getStyleClass().add("recompense-card");
        recompenseContainer.setMaxWidth(300);
        recompenseContainer.setMinWidth(300);

        // Display the photo
        if (recompense.getPhoto() != null && !recompense.getPhoto().isEmpty()) {
            File imageFile = new File(recompense.getPhoto());
            Image image = imageFile.exists()
                    ? new Image(imageFile.toURI().toString(), 280, 180, true, true)
                    : new Image(getClass().getResourceAsStream("/views/placeholder.png"), 280, 180, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            imageView.getStyleClass().add("image-view");
            recompenseContainer.getChildren().add(imageView);
        }

        // Display the name
        Text recompenseName = new Text(recompense.getNom());
        recompenseName.getStyleClass().add("recompense-name");
        recompenseContainer.getChildren().add(recompenseName);

        // Display the points required
        Text pointsText = new Text("Points: " + recompense.getPointsRequis());
        pointsText.getStyleClass().add("recompense-points");
        recompenseContainer.getChildren().add(pointsText);

        // Set click event (optional)
        return recompenseContainer;
    }

    @FXML
    void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            updateRecompensesDisplay(originalRecompensesList);
            return;
        }

        List<Recompense> filteredList = originalRecompensesList.stream()
                .filter(recompense ->
                        recompense.getNom().toLowerCase().contains(searchText) ||
                                String.valueOf(recompense.getPointsRequis()).contains(searchText))
                .collect(Collectors.toList());

        updateRecompensesDisplay(filteredList);
    }

    @FXML
    void handleFilter(ActionEvent event) {
        String filterOption = filterComboBox.getValue();
        List<Recompense> filteredList = new ArrayList<>(originalRecompensesList);

        switch (filterOption) {
            case "Points: Low to High":
                filteredList.sort(Comparator.comparingInt(Recompense::getPointsRequis));
                break;
            case "Points: High to Low":
                filteredList.sort((r1, r2) -> r2.getPointsRequis() - r1.getPointsRequis());
                break;
        }

        updateRecompensesDisplay(filteredList);
    }

    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            ProfilController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not return to profile.", e.getMessage());
        }
    }



    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}