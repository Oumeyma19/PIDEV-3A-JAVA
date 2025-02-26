package controllers;

import exceptions.UserNotFoundException;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import services.ClientService;

import java.io.IOException;
import java.util.stream.Collectors;

public class ClientsController {
    @FXML
    private TextField searchField;


    @FXML
    private TableView<User> clientsTable;
    @FXML
    private TableColumn<User, String> nomColumn;
    @FXML
    private TableColumn<User, String> prenomColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, Integer> pointsFidColumn;
    @FXML
    private TableColumn<User, String> nivFidColumn;
    @FXML
    private TableColumn<User, String> isActiveColumn;
    @FXML
    private TableColumn<User, Boolean> isBannedColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label greetingLabel;


    @FXML
    private Text fullnameText;

    @FXML
    private ImageView userImage;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            fullnameText.setText("Bonjour, " + user.getFirstname());
        }
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
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        ObservableList<User> filteredList = clientsList.stream()
            .filter(user -> user.getLastname().toLowerCase().contains(query) ||
                user.getFirstname().toLowerCase().contains(query) ||
                user.getEmail().toLowerCase().contains(query) ||
                user.getPhone().toLowerCase().contains(query) ||
                String.valueOf(user.getPointsfid()).contains(query) ||
                (user.getNivfid() != null && user.getNivfid().toLowerCase().contains(query)) ||
                (user.getIsActive() ? "disponible" : "indisponible").contains(query) ||
                (user.getIsBanned() ? "oui" : "non").contains(query))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));
        clientsTable.setItems(filteredList);
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


    private ClientService clientService = ClientService.getInstance();
    private ObservableList<User> clientsList = FXCollections.observableArrayList();

    private static ClientsController instance;

    public ClientsController() {
        instance = this;
    }

    public static ClientsController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        // Initialize the columns
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        pointsFidColumn.setCellValueFactory(new PropertyValueFactory<>("pointsfid"));
        nivFidColumn.setCellValueFactory(new PropertyValueFactory<>("nivfid"));
        isActiveColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().getIsActive();
            return new SimpleStringProperty(isActive ? "Disponible" : "Indisponible");
        });
        isBannedColumn.setCellValueFactory(new PropertyValueFactory<>("isBanned"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Load data
        loadClients();

        // Add listener to table selection
        clientsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showClientDetails(newValue));
    }

    private void loadClients() {
        clientsList.setAll(clientService.getUsers());
        clientsTable.setItems(clientsList);
    }

    public void addClient(User user) {
        clientsList.add(user);
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }

    @FXML
    private void handleAjouterClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AjouterClient.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Client");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        try {
            int id = Integer.parseInt(idField.getText());
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            User user = clientService.getUserbyID(id);
            if (user != null) {
                user.setLastname(nom);
                user.setFirstname(prenom);
                user.setEmail(email);
                user.setPhone(phone);

                clientService.updateUser(user);
                showMessage("Client modifié avec succès!", "green");
                refreshTable();
            } else {
                showMessage("Client non trouvé.", "red");
            }
        } catch (NumberFormatException e) {
            showMessage("ID invalide.", "red");
        } catch (Exception e) {
            showMessage("Erreur lors de la modification du client.", "red");
            e.printStackTrace();
        }
    }

    private void clearClientDetails() {
        idField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        phoneField.clear();

    }

    private void showClientDetails(User user) {
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            nomField.setText(user.getLastname());
            prenomField.setText(user.getFirstname());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
        } else {
            clearClientDetails();
        }
    }

    @FXML
    private void handleSupprimer() {
        User selectedUser = clientsTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le client");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce client ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        clientService.deleteUser(selectedUser.getId());
                        clientsList.remove(selectedUser);
                        showMessage("Client deleted successfully!", "green");
                    } catch (UserNotFoundException e) {
                        showMessage(e.getMessage(), "red");
                    }
                }
            });
        } else {
            showMessage("No client selected.", "red");
        }
    }
    private void refreshTable() {
        clientsTable.getItems().setAll(clientService.getUsers());
    }


}
