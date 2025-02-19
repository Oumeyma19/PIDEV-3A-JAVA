package com.example.pidev.controllers;

import com.example.pidev.Util.Helpers;
import com.example.pidev.Util.TypeHebergement;
import com.example.pidev.models.Hebergements;
import com.example.pidev.services.HebergementService;
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
import java.sql.Timestamp;
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
    private DatePicker dateI, dateO;

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
    private TableColumn<Hebergements, String> nomHebergCol;

    @FXML
    private TableColumn<Hebergements, String> typeHebergCol;

    @FXML
    private TableColumn<Hebergements, Integer> nbrCCol;

    @FXML
    private TableColumn<Hebergements, Float> prixCol;

    @FXML
    private TableColumn<Hebergements, String> dateICCol;

    @FXML
    private TableColumn<Hebergements, String> dateOCCol;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button listes;

    private Hebergements hebergementActuel;

    private HebergementService hebergementService = HebergementService.getInstance();

    private ObservableList<TypeHebergement> typeHebergementList = FXCollections.observableArrayList(Arrays.asList(TypeHebergement.values()));

    private ObservableList<Hebergements> hebergementList = FXCollections.observableArrayList();

    private String selectedImagePath;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeHeberg.setItems(typeHebergementList);

        loadHebergementsData();
    }

    public void refreshList() throws SQLException {
        hebergementList.setAll(hebergementService.recuperer());
    }

    // Method to load data into TableView
    private void loadHebergementsData() {

        nomHebergCol.setCellValueFactory(new PropertyValueFactory<>("nomHeberg"));
        typeHebergCol.setCellValueFactory(new PropertyValueFactory<>("typeHeberg"));
        nbrCCol.setCellValueFactory(new PropertyValueFactory<>("nbrClient"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixHeberg"));
        dateICCol.setCellValueFactory(new PropertyValueFactory<>("dateCheckin"));
        dateOCCol.setCellValueFactory(new PropertyValueFactory<>("dateCheckout"));

        try {
            hebergementList.setAll(hebergementService.recuperer());
            tableView.setItems(hebergementList);

            tableView.setRowFactory(tv -> {

                TableRow<Hebergements> row = new TableRow<>();

                row.setOnMouseClicked(event -> {

                    if ((event.getClickCount() == 2) && (!row.isEmpty())) {

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/detailsHeberg.fxml"));
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
                    nbrClientText.isEmpty() || prixText.isEmpty() || typeHebergement == null ||
                    dateI.getValue() == null || dateO.getValue() == null) {
                Helpers.showAlert("Error", "Veuillez remplir tous les champs!", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Validation du nombre de clients
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

            // ✅ Validation du prix
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

            // ✅ Validation des dates
            Timestamp dateCheckin = Timestamp.valueOf(dateI.getValue().atStartOfDay());
            Timestamp dateCheckout = Timestamp.valueOf(dateO.getValue().atStartOfDay());

            if (dateCheckin.after(dateCheckout)) {
                Helpers.showAlert("Error", "La date de sortie doit être après la date d'entrée!", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Création de l'objet Hebergement
            Hebergements newHebergement = new Hebergements(
                    name, typeHebergement.name(), address, description, nbrClient,
                    image.getImage().getUrl(), dateCheckin, dateCheckout, prixHeberg
            );

            // ✅ Ajout à la base de données
            boolean success = hebergementService.ajouter(newHebergement);
            if (success) {
                Helpers.showAlert("Success", "Hébergement ajouté avec succès!", Alert.AlertType.INFORMATION);
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
    private void afficherListeHebergements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/listesHeberg.fxml"));
            Parent root = loader.load();
            listes.getScene().setRoot(root);

        } catch (IOException e) {
            Helpers.showAlert("Erreur", "Impossible de charger la liste des hébergements.", Alert.AlertType.ERROR);
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

