package controllers;

import models.ProgrammeFidelite;
import models.User;
import services.ProgrammeFideliteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgrammeFideliteController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<ProgrammeFidelite> programmesTable;
    @FXML
    private TableColumn<ProgrammeFidelite, Integer> idColumn;
    @FXML
    private TableColumn<ProgrammeFidelite, String> nomColumn;
    @FXML
    private TableColumn<ProgrammeFidelite, Integer> pointsColumn;
    @FXML
    private TableColumn<ProgrammeFidelite, String> photoColumn;
    @FXML
    private TableColumn<ProgrammeFidelite, Void> actionsColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField pointsField;
    @FXML
    private TextField photoField;
    @FXML
    private Label messageLabel;
    @FXML
    private Text fullnameText;
    @FXML
    private ImageView userImage;

    private User currentUser;
    private ProgrammeFideliteService programmeService;
    private final ObservableList<ProgrammeFidelite> programmesList = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            fullnameText.setText("Bonjour, " + user.getFirstname());
        }
    }

    @FXML
    public void initialize() {
        programmeService = new ProgrammeFideliteService();

        // Initialisation des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomProgramme"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));

        // Configuration de la colonne d'actions
        setupActionsColumn();

        // Chargement des données
        loadProgrammes();

        // Ajout d'un écouteur pour la sélection dans le tableau
        programmesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProgrammeDetails(newValue));
    }

    private void setupActionsColumn() {
        Callback<TableColumn<ProgrammeFidelite, Void>, TableCell<ProgrammeFidelite, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<ProgrammeFidelite, Void> call(final TableColumn<ProgrammeFidelite, Void> param) {
                        return new TableCell<>() {
                            private final Button editBtn = new Button("Modifier");
                            private final Button deleteBtn = new Button("Supprimer");
                            private final HBox pane = new HBox(5, editBtn, deleteBtn);

                            {
                                editBtn.getStyleClass().add("edit-button");
                                deleteBtn.getStyleClass().add("delete-button");

                                editBtn.setOnAction(event -> {
                                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                                    showProgrammeDetails(programme);
                                });

                                deleteBtn.setOnAction(event -> {
                                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                                    handleSupprimer(programme);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                setGraphic(empty ? null : pane);
                            }
                        };
                    }
                };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void loadProgrammes() {
        List<ProgrammeFidelite> programmes = programmeService.getAllProgrammes();
        programmesList.setAll(programmes);
        programmesTable.setItems(programmesList);
    }

    private void showProgrammeDetails(ProgrammeFidelite programme) {
        if (programme != null) {
            idField.setText(String.valueOf(programme.getId()));
            nomField.setText(programme.getNomProgramme());
            pointsField.setText(String.valueOf(programme.getPoints()));
            photoField.setText(programme.getPhoto());
        } else {
            clearProgrammeDetails();
        }
    }

    private void clearProgrammeDetails() {
        idField.clear();
        nomField.clear();
        pointsField.clear();
        photoField.clear();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        ObservableList<ProgrammeFidelite> filteredList = programmesList.stream()
                .filter(programme ->
                        String.valueOf(programme.getId()).contains(query) ||
                                programme.getNomProgramme().toLowerCase().contains(query) ||
                                String.valueOf(programme.getPoints()).contains(query) ||
                                (programme.getPhoto() != null && programme.getPhoto().toLowerCase().contains(query)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        programmesTable.setItems(filteredList);
    }

    @FXML
    private void handleAjouterProgramme() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouterProgarmmefidelite.fxml"));
            Parent root = loader.load();

            // Configuration du contrôleur
            AjouterProgrammeController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Programme de Fidélité");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger les programmes après l'ajout
            loadProgrammes();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement de l'interface d'ajout.", "red");
        }
    }

    @FXML
    private void handleModifier() {
        try {
            if (idField.getText().isEmpty()) {
                showMessage("Veuillez sélectionner un programme à modifier.", "red");
                return;
            }

            int id = Integer.parseInt(idField.getText());
            String nom = nomField.getText();
            int points;
            try {
                points = Integer.parseInt(pointsField.getText());
            } catch (NumberFormatException e) {
                showMessage("Les points doivent être un nombre entier.", "red");
                return;
            }
            String photo = photoField.getText();

            if (nom.isEmpty()) {
                showMessage("Le nom du programme ne peut pas être vide.", "red");
                return;
            }

            ProgrammeFidelite programme = new ProgrammeFidelite(id, nom, points, photo);
            boolean success = programmeService.updateProgramme(programme);

            if (success) {
                showMessage("Programme modifié avec succès!", "green");
                loadProgrammes();
                clearProgrammeDetails();
            } else {
                showMessage("Erreur lors de la modification du programme.", "red");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Une erreur est survenue: " + e.getMessage(), "red");
        }
    }

    private void handleSupprimer(ProgrammeFidelite programme) {
        if (programme == null) {
            showMessage("Veuillez sélectionner un programme à supprimer.", "red");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le programme de fidélité");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le programme \"" + programme.getNomProgramme() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = programmeService.deleteProgramme(programme.getId());

            if (success) {
                showMessage("Programme supprimé avec succès!", "green");
                loadProgrammes(); // Recharger les programmes après suppression
                clearProgrammeDetails();
            } else {
                showMessage("Erreur : impossible de supprimer le programme.", "red");
            }
        }
    }


    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }

    // Méthode pour ajouter un programme (appelée depuis AjouterProgrammeController)
    public void addProgramme(ProgrammeFidelite programme) {
        programmeService.addProgramme(programme);
        loadProgrammes();
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
    private void handleDashboardClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Dashboard.fxml");
        }
    }

    @FXML
    private void handleClientsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Clients.fxml"));
            Parent root = loader.load();

            ClientsController clientsController = loader.getController();
            clientsController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Clients.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuidesClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Guides.fxml"));
            Parent root = loader.load();

            GuidesController guidesController = loader.getController();
            guidesController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Guides.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleRecompensesClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RewardsManagement.fxml"));
            Parent root = loader.load();

            RewardsManagementController rewardsController = loader.getController();
            rewardsController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de RewardsManagement.fxml: " + e.getMessage());
        }
    }
}