package com.example.pidev.controllers;

import com.example.pidev.Util.Helpers;
import com.example.pidev.Util.TypeHebergement;
import com.example.pidev.services.HebergementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import com.example.pidev.models.Hebergements;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateHebergController implements Initializable {

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnRetour;

    @FXML
    private TextField nomHeberg;

    @FXML
    private TextField adresseHeberg;

    @FXML
    private ComboBox<String> typeHeberg;

    @FXML
    private TextField descrp;

    @FXML
    private TextField nbrC;

    @FXML
    private ImageView image;

    @FXML
    private DatePicker dateI;

    @FXML
    private DatePicker dateO;

    private String selectedImagePath;

    private Hebergements hebergement; // Objet pour stocker les infos actuelles

    private HebergementService hebergementService = HebergementService.getInstance();


    private ObservableList<String> typeHebergementList = FXCollections.observableArrayList(
            Arrays.stream(
                    TypeHebergement.values()).map(Enum::name).toList()
    );


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeHeberg.setItems(typeHebergementList);
    }


    public void setHebergementData(Hebergements hebergement) {
        this.hebergement = hebergement;

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, hebergement.toString());

        nomHeberg.setText(hebergement.getNomHeberg());
        adresseHeberg.setText(hebergement.getAdresse());
        typeHeberg.setValue(hebergement.getTypeHeberg());
        descrp.setText(hebergement.getDescrHeberg());
        nbrC.setText(String.valueOf(hebergement.getNbrClient()));
        dateI.setValue(hebergement.getDateCheckin().toLocalDateTime().toLocalDate());
        dateO.setValue(hebergement.getDateCheckout().toLocalDateTime().toLocalDate());
        image.setImage(new Image(hebergement.getImageHebrg()));
    }

    @FXML
    private void handleUpdate() {
        // Vérification des champs vides
        if (nomHeberg.getText().isEmpty() || adresseHeberg.getText().isEmpty() || typeHeberg.getValue() == null) {
            Helpers.showAlert("Erreur", "Tous les champs doivent être remplis.", AlertType.ERROR);
            return;
        }

        // Mise à jour de l'objet hébergement
        this.hebergement.setNomHeberg(nomHeberg.getText());
        this.hebergement.setAdresse(adresseHeberg.getText());
        this.hebergement.setTypeHeberg(typeHeberg.getValue());
        this.hebergement.setDescrHeberg(descrp.getText());
        this.hebergement.setNbrClient(Integer.parseInt(nbrC.getText()));
        this.hebergement.setDateCheckin(Timestamp.valueOf(dateI.getValue().atStartOfDay()));
        this.hebergement.setDateCheckout(Timestamp.valueOf(dateO.getValue().atStartOfDay()));
        this.hebergement.setImageHebrg(image.getImage().getUrl());

        try {
            hebergementService.modifier(this.hebergement);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/ajouterHeberg.fxml"));
            Parent root = loader.load();
            AjouterHebergController ajouterHebergController = loader.getController();
            ajouterHebergController.refreshList();
            // Simuler la mise à jour dans la base de données (à remplacer avec un vrai appel DB)
            System.out.println("Hébergement modifié : " + hebergement);

            // Afficher une confirmation
            Helpers.showAlert("Succès", "L'hébergement a été mis à jour avec succès !", AlertType.INFORMATION);

            btnRetour(null);
        } catch (Exception e) {
            System.out.println("Hébergement non modifié : " + e);

            // Afficher une confirmation
            Helpers.showAlert("Echec", "Erreure s'est produite !!", AlertType.ERROR);

        }
    }

    @FXML
    void btnRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/detailsHeberg.fxml"));
            Parent root = loader.load();
            btnRetour.getScene().setRoot(root);

            DetailHebergController detailHebergController = loader.getController();
            detailHebergController.setHebergementDetails(hebergement);
        } catch (IOException ex) {
            Logger.getLogger(DetailHebergController.class.getName()).log(Level.SEVERE, null, ex);
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

}
