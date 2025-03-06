package controllers;

import models.Recompense;
import models.User;
import services.RecompenseService;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RewardsManagementController {

    @FXML
    private TableView<Recompense> recompensesTable;

    @FXML
    private TableColumn<Recompense, Integer> idColumn;

    @FXML
    private TableColumn<Recompense, String> nomColumn;

    @FXML
    private TableColumn<Recompense, String> descriptionColumn;

    @FXML
    private TableColumn<Recompense, Integer> pointsRequiredColumn;

    @FXML
    private TableColumn<Recompense, Void> actionsColumn;


    @FXML
    private Button ajouterRecompenseButton;

    private final RecompenseService service = new RecompenseService();
    private final ObservableList<Recompense> recompenseList = FXCollections.observableArrayList();
    private User currentUser; // Added current user field

    @FXML
    public void initialize() {
        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        pointsRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("pointsRequired"));
        pointsRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("pointsRequired"));

// Par celle-ci:
        pointsRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("pointsRequis"));

        // Configurer la colonne des actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifierButton = new Button("Modifier");
            private final Button supprimerButton = new Button("Supprimer");

            {
                modifierButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                supprimerButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                modifierButton.setOnAction(event -> {
                    Recompense recompense = getTableView().getItems().get(getIndex());
                    handleModifierRecompense(recompense);
                });

                supprimerButton.setOnAction(event -> {
                    Recompense recompense = getTableView().getItems().get(getIndex());
                    handleSupprimerRecompense(recompense);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, modifierButton, supprimerButton);
                    setGraphic(actions);
                }
            }
        });

        // Charger les récompenses
        loadRecompenses();
    }

    private void loadRecompenses() {
        List<Recompense> recompenses = service.getAllRecompenses();
        recompenseList.setAll(recompenses);
        recompensesTable.setItems(recompenseList);
    }

    @FXML
    public void handleAjouterRecompense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouter_recompense.fxml"));
            Parent root = loader.load();
            AjouterRecompenseController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Récompense");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue d'ajout.", Alert.AlertType.ERROR);
        }
    }

    private void handleModifierRecompense(Recompense recompense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_recompense.fxml"));
            Parent root = loader.load();
            UpdateRecompenseController controller = loader.getController();
            controller.setParentController(this);
            controller.setRecompense(recompense);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une Récompense");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue de modification.", Alert.AlertType.ERROR);
        }
    }

    private void handleSupprimerRecompense(Recompense recompense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette récompense ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.deleteRecompense(recompense.getId());
            loadRecompenses(); // Recharger la liste après suppression
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

    // Méthode publique pour permettre aux contrôleurs enfants de rafraîchir la liste
    public void refreshRecompensesList() {
        loadRecompenses();
    }

    // Ajout de la méthode refreshUI pour compatibilité avec les autres contrôleurs
    public void refreshUI() {
        loadRecompenses();
    }

    // Getter and setter for current user
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}