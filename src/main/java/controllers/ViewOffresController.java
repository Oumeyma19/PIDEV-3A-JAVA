package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import models.Offre;
import services.OffreService;

import java.sql.SQLException;

public class ViewOffresController {

    @FXML private TableView<Offre> tableView;
    @FXML private TableColumn<Offre, String> colTitle;
    @FXML private TableColumn<Offre, String> colDescription;
    @FXML private TableColumn<Offre, Double> colPrice;
    @FXML private TableColumn<Offre, String> colStartDate;
    @FXML private TableColumn<Offre, String> colEndDate;
    @FXML private TableColumn<Offre, String> colImage;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    private final OffreService offreService = new OffreService();
    private ObservableList<Offre> offresList = FXCollections.observableArrayList();
    private Timeline timeline; // ✅ Add Timeline for auto-refresh

    @FXML
    public void initialize() throws SQLException {
        setupTableColumns();
        loadOffers();
        setupAutoRefresh(); // ✅ Start auto-refresh
    }

    private void setupTableColumns() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // Setup image column as before
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setCellFactory(new Callback<TableColumn<Offre, String>, TableCell<Offre, String>>() {
            @Override
            public TableCell<Offre, String> call(TableColumn<Offre, String> param) {
                return new TableCell<Offre, String>() {
                    private final ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(String imagePath, boolean empty) {
                        super.updateItem(imagePath, empty);
                        if (empty || imagePath == null || imagePath.isEmpty()) {
                            setGraphic(null);
                        } else {
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            imageView.setImage(new Image(imagePath));
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        btnModifier.setOnAction(event -> openUpdateOffre());
        btnSupprimer.setOnAction(event -> deleteSelectedOffer());
    }

    private void setupAutoRefresh() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            loadOffers(); // Reload offers every 10 seconds
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play(); // Start the auto-refresh
    }

    private void loadOffers() {
        Task<ObservableList<Offre>> loadTask = new Task<>() {
            @Override
            protected ObservableList<Offre> call() throws SQLException {
                ObservableList<Offre> offers = FXCollections.observableArrayList();
                offers.addAll(offreService.Display());
                return offers;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                offresList.clear();
                offresList.addAll(getValue());
                tableView.setItems(offresList);
            }

            @Override
            protected void failed() {
                super.failed();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des offres !");
            }
        };
        new Thread(loadTask).start();
    }

    @FXML
    public void refreshList() throws SQLException {
        loadOffers();
    }

    private void openUpdateOffre() {
        Offre selectedOffer = tableView.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Alerte", "Veuillez sélectionner une offre !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateOffre.fxml"));
            Parent root = loader.load();

            UpdateOffreController updateController = loader.getController();
            updateController.setOfferData(selectedOffer);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Offre");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedOffer() {
        Offre selectedOffer = tableView.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Alerte", "Veuillez sélectionner une offre !");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer l'offre ?");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DeleteOffreController deleteController = new DeleteOffreController();
                    deleteController.deleteOfferById(selectedOffer.getId());

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès !");
                    loadOffers(); // Refresh list after deletion
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression !");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Make sure to stop the timeline when the controller is destroyed to prevent memory leaks
    @FXML
    public void cleanup() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}