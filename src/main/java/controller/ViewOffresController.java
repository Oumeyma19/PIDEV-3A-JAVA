package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Offre;
import tools.MyDataBase;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewOffresController {

    @FXML private TableView<Offre> tableView;
    @FXML private TableColumn<Offre, Integer> colId;
    @FXML private TableColumn<Offre, String> colTitle;
    @FXML private TableColumn<Offre, String> colDescription;
    @FXML private TableColumn<Offre, Double> colPrice;
    @FXML private TableColumn<Offre, String> colStartDate;
    @FXML private TableColumn<Offre, String> colEndDate;
    @FXML private TableColumn<Offre, String> colImage; // ✅ Image column
    @FXML private Button btnModifier;

    private ObservableList<Offre> offresList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        // ✅ Set up Image column
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

        loadOffers();
        btnModifier.setOnAction(event -> openUpdateOffre());
    }

    private void loadOffers() {
        offresList.clear();
        String query = "SELECT * FROM offers";

        try (Connection connection = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                offresList.add(new Offre(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("image_path") // ✅ Ensure image is loaded
                ));
            }
            tableView.setItems(offresList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void refreshList() {
        loadOffers();
    }

    private void openUpdateOffre() {
        Offre selectedOffer = tableView.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            System.out.println("Veuillez sélectionner une offre !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateOffre.fxml"));
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
}
