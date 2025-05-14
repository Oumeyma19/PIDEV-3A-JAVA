package controllers;

import util.Helpers;
import util.TypeHebergement;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Hebergements;
import models.ReservationHebergement;
import models.User;
import services.HebergementService;
import services.NotificationService;
import services.ReservHebergService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AjouterHebergController implements Initializable {

    @FXML
    private TextField nomHeberg;

    @FXML
    private TextField descrp;

    @FXML
    private TextField adresseHeberg;

    @FXML
    private ComboBox<TypeHebergement> typeHeberg;

    @FXML
    private ImageView image;
    @FXML
    private ImageView img;

    @FXML
    private TextField nbrC;

    @FXML
    private TextField prix;

    @FXML
    private Button btnAdd;

    // TableView and TableColumn references
    @FXML
    private TableView<Hebergements> tableView;

    @FXML
    private TableView<ReservationHebergement> tableViewReservations;

    @FXML
    private TableColumn<Hebergements, String> nomHebergCol;

    @FXML
    private TableColumn<Hebergements, String> typeHebergCol;

    @FXML
    private TableColumn<Hebergements, Integer> nbrCCol;

    @FXML
    private TableColumn<Hebergements, Float> prixCol;

    // RESERVATION TABLE COLS --
    @FXML
    private TableColumn<ReservationHebergement, String> nomHebergCol1;

    @FXML
    private TableColumn<ReservationHebergement, String> clientHebergCol;

    @FXML
    private TableColumn<ReservationHebergement, String> typeHebergCol1;

    @FXML
    private TableColumn<ReservationHebergement, Integer> nbrCCol1;

    @FXML
    private TableColumn<ReservationHebergement, String> dateICCol1;

    @FXML
    private TableColumn<ReservationHebergement, String> dateOCCol1;

    // RESERVATION TABLE COLS --

    @FXML
    private Button btnSupprimer;


    private Hebergements hebergementActuel;

    private final HebergementService hebergementService = HebergementService.getInstance();

    private final ReservHebergService reservHebergService = ReservHebergService.getInstance();

    private final ObservableList<TypeHebergement> typeHebergementList = FXCollections.observableArrayList(Arrays.asList(TypeHebergement.values()));

    private final ObservableList<Hebergements> hebergementList = FXCollections.observableArrayList();

    private final NotificationService notificationService = NotificationService.getInstance();

    private final ObservableList<ReservationHebergement> reservationsList = FXCollections.observableArrayList();

    private String selectedImagePath;

    private User currentUser;

    @FXML
    private Text fullnameText;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            fullnameText.setText("Bonjour, " + user.getFirstname());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeHeberg.setItems(typeHebergementList);

        loadHebergementsData();

        loadReservationsData();
    }

    public void refreshList() throws SQLException {
        hebergementList.setAll(hebergementService.recuperer());
    }

    @FXML
    private void handleUserImageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Profil.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuidesClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Guides.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            GuidesController guidesController = loader.getController();
            guidesController.setCurrentUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true); // Set the stage to fullscreen
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Guides.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleClientsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Clients.fxml"));
            Parent root = loader.load();

            // Get the controller and set the current user
            ClientsController clientsController = loader.getController();
            clientsController.setCurrentUser(currentUser);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true); // Set the stage to fullscreen
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Clients.fxml: " + e.getMessage());
        }
    }

    private void loadReservationsData() {

        nomHebergCol1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getHebergements().getNomHeberg()));

        clientHebergCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getUser().getFirstname() + " " + p.getValue().getUser().getLastname()));

        typeHebergCol1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getHebergements().getTypeHeberg()));

        nbrCCol1.setCellValueFactory(new PropertyValueFactory<>("nbPersonnes"));
        dateICCol1.setCellValueFactory(new PropertyValueFactory<>("dateCheckIn"));
        dateOCCol1.setCellValueFactory(new PropertyValueFactory<>("dateCheckOut"));

        try {
            reservationsList.setAll(reservHebergService.recuperer());
            tableViewReservations.setItems(reservationsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load data into TableView
    private void loadHebergementsData() {

        nomHebergCol.setCellValueFactory(new PropertyValueFactory<>("nomHeberg"));
        typeHebergCol.setCellValueFactory(new PropertyValueFactory<>("typeHeberg"));
        nbrCCol.setCellValueFactory(new PropertyValueFactory<>("nbrClient"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixHeberg"));

        try {
            hebergementList.setAll(hebergementService.recuperer());
            tableView.setItems(hebergementList);

            tableView.setRowFactory(tv -> {

                TableRow<Hebergements> row = new TableRow<>();

                row.setOnMouseClicked(event -> {

                    if ((event.getClickCount() == 2) && (!row.isEmpty())) {

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/detailsHeberg.fxml"));
                            Parent root = loader.load();
                            btnAdd.getScene().setRoot(root);

                            DetailHebergController dhc = loader.getController();
                            final Hebergements selectedItem = tableView.getSelectionModel().getSelectedItem();

                            try {
                                dhc.setHebergementDetails(selectedItem);
                            } catch (Exception n) {
                                System.out.println(selectedItem);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(AjouterHebergController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if ((event.getClickCount() == 1) && (!row.isEmpty())) {
                        this.hebergementActuel = tableView.getSelectionModel().getSelectedItem();
                        btnSupprimer.setDisable(false);
                    }
                });

                return row;
            });

        } catch (SQLException e) {
            Helpers.showAlert("Error", "Erreur lors de la récupération des données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // 📌 Handle Image Selection
    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            image.setImage(new Image("file:///" + selectedImagePath));
        }
    }

    // 📌 Handle Adding Hebergement
    @FXML
    private void ajouterHebergement(ActionEvent event) {
        try {
            // ✅ Récupération des valeurs des champs
            String name = nomHeberg.getText().trim();
            String description = descrp.getText().trim();
            String address = adresseHeberg.getText().trim();
            String nbrClientText = nbrC.getText().trim();
            String prixText = prix.getText().trim();
            TypeHebergement typeHebergement = typeHeberg.getValue();

            // ✅ Validation des champs vides
            if (name.isEmpty() || description.isEmpty() || address.isEmpty() ||
                    nbrClientText.isEmpty() || prixText.isEmpty() || typeHebergement == null) {
                Helpers.showAlert("Error", "Veuillez remplir tous les champs!", Alert.AlertType.ERROR);
                return;
            }


            int nbrClient;
            try {
                nbrClient = Integer.parseInt(nbrClientText);
                if (nbrClient <= 0) {
                    Helpers.showAlert("Error", "Le nombre de clients doit être supérieur à 0!", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                Helpers.showAlert("Error", "Veuillez entrer un nombre valide pour les clients!", Alert.AlertType.ERROR);
                return;
            }

            float prixHeberg;
            try {
                prixHeberg = Float.parseFloat(prixText);
                if (prixHeberg < 0) {
                    Helpers.showAlert("Error", "Le prix ne peut pas être négatif!", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                Helpers.showAlert("Error", "Veuillez entrer un prix valide!", Alert.AlertType.ERROR);
                return;
            }


            // ✅ Création de l'objet Hebergement
            Hebergements newHebergement = new Hebergements(
                    name, typeHebergement.name(), address, description, nbrClient,
                    image.getImage().getUrl(), prixHeberg, false
            );

            if (hebergementService.existsByNameAndAddress(name, address)) {
                Helpers.showAlert("Error", "Duplication d'hébergements !", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Ajout à la base de données
            boolean success = hebergementService.ajouter(newHebergement);
            if (success) {
                Helpers.showAlert("Success", "Hébergement ajouté avec succès!", Alert.AlertType.INFORMATION);
                notificationService.showNotification("Hébergement ajouté", "Vous avez ajouté un nouvel hébergement : " + name);
                refreshList();
            } else {
                Helpers.showAlert("Error", "Échec de l'ajout de l'hébergement. Veuillez réessayer.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            Helpers.showAlert("Error", "Erreur de base de données : " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            Helpers.showAlert("Error", "Une erreur inattendue est survenue.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerHebergement() {

        if (hebergementActuel == null) {
            Helpers.showAlert("Erreur", "Aucun hébergement sélectionné", Alert.AlertType.ERROR);
            return;
        }
        // ✅ Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cet hébergement ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    HebergementService hebergementService = HebergementService.getInstance();

                    if (!hebergementService.supprimer(hebergementActuel.getIdHebrg())) {
                        Helpers.showAlert("Erreur", "Échec de la suppression de l'hébergement.", Alert.AlertType.ERROR);
                    } else {
                        Helpers.showAlert("Succès", "Hébergement supprimé avec succès!", Alert.AlertType.INFORMATION);
                        notificationService.showNotification("Hébergement supprimé", "Vous avez supprimé l'hébergement : " + hebergementActuel.getNomHeberg());

                        refreshList();
                    }

                } catch (Exception e) {
                    Helpers.showAlert("Erreur", "Une erreur est survenue lors de la suppression.", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

}

