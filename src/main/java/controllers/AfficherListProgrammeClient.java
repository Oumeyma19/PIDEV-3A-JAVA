package controllers;

import models.ProgrammeFidelite;
import models.User;
import services.ProgrammeFideliteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.Type;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AfficherListProgrammeClient {

    @FXML
    private TableView<ProgrammeFidelite> programmeTable;

    @FXML
    private TableColumn<ProgrammeFidelite, Integer> colId;

    @FXML
    private TableColumn<ProgrammeFidelite, String> colNom;

    @FXML
    private TableColumn<ProgrammeFidelite, Integer> colPoints;

    @FXML
    private TableColumn<ProgrammeFidelite, String> colPhoto;

    @FXML
    private TableColumn<ProgrammeFidelite, Void> colActions;

    @FXML
    private Button addProgrammeButton; // Bouton pour ajouter un programme

    private final ProgrammeFideliteService service = new ProgrammeFideliteService();
    private final ObservableList<ProgrammeFidelite> programmeList = FXCollections.observableArrayList();
    private User currentUser; // Utilisateur actuel

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize(); // Recharger l'interface avec les permissions appropriées
    }

    @FXML
    public void initialize() {
        // Liaison des colonnes avec les propriétés de ProgrammeFidelite
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomProgramme"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colPhoto.setCellValueFactory(new PropertyValueFactory<>("photo"));

        // Ajout des boutons d'actions (modifier et supprimer) uniquement pour l'admin
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("❌");
            private final Button updateButton = new Button("✏️");
            private final Button buyButton = new Button("Acheter"); // Bouton pour acheter un niveau

            {
                // Style des boutons
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                buyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                // Action pour le bouton de suppression (admin uniquement)
                deleteButton.setOnAction(event -> {
                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                    handleDeleteProgramme(programme);
                });

                // Action pour le bouton de modification (admin uniquement)
                updateButton.setOnAction(event -> {
                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                    handleUpdateProgramme(programme);
                });

                // Action pour le bouton d'achat (client uniquement)
                buyButton.setOnAction(event -> {
                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                    handleBuyProgramme(programme);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container;
                    if (currentUser != null && currentUser.getRoles() == Type.ADMIN) {
                        container = new HBox(10, updateButton, deleteButton); // Admin : modifier et supprimer
                    } else {
                        container = new HBox(10, buyButton); // Client : acheter
                    }
                    setGraphic(container);
                }
            }
        });

        // Masquer le bouton d'ajout pour les clients
        if (currentUser != null && currentUser.getRoles() != Type.ADMIN) {
            addProgrammeButton.setVisible(false);
        }

        // Charger les programmes dans la table
        loadProgrammes();
    }

    // Charger les programmes depuis le service
    void loadProgrammes() {
        List<ProgrammeFidelite> programmes = service.getAllProgrammes();
        programmeList.setAll(programmes);
        programmeTable.setItems(programmeList);
    }

    // Gérer la suppression d'un programme (admin uniquement)
    private void handleDeleteProgramme(ProgrammeFidelite programme) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce programme ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            service.deleteProgramme(programme.getId());
            loadProgrammes(); // Recharger la liste après suppression
        }
    }

    // Gérer la modification d'un programme (admin uniquement)
    private void handleUpdateProgramme(ProgrammeFidelite programme) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateprog.fxml"));
            Parent root = loader.load();
            UpdateProgController controller = loader.getController();
            controller.initData(programme, this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Programme");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue de modification.", Alert.AlertType.ERROR);
        }
    }

    // Gérer l'achat d'un programme (client uniquement)
    private void handleBuyProgramme(ProgrammeFidelite programme) {
        if (currentUser.getPointsfid() >= programme.getPoints()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation d'achat");
            alert.setHeaderText("Voulez-vous acheter ce programme ?");
            alert.setContentText("Coût : " + programme.getPoints() + " points");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                // Mettre à jour les points du client
                currentUser.setPointsfid(currentUser.getPointsfid() - programme.getPoints());
                showAlert("Succès", "Achat réussi !", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Erreur", "Points insuffisants pour acheter ce programme.", Alert.AlertType.ERROR);
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
