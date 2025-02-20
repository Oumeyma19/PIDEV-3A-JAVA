package controllers;

import exceptions.*;
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
import services.GuideService;

import java.io.IOException;

public class GuidesController {

    @FXML
    private TableView<User> guidesTable;
    @FXML
    private TableColumn<User, String> firstnameColumn;
    @FXML
    private TableColumn<User, String> lastnameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> statusColumn;
    @FXML
    private TableColumn<User, String> activeColumn;
    @FXML
    private TableColumn<User, String> bannedColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField activeField;
    @FXML
    private TextField bannedField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label greetingLabel;

    @FXML
    private ImageView userImage;

    @FXML
    private Text fullnameText;

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
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Clients.fxml: " + e.getMessage());
        }
    }

    private GuideService guideService = GuideService.getInstance();
    private ObservableList<User> guidesList = FXCollections.observableArrayList();

    private static GuidesController instance;

    public GuidesController() {
        instance = this;
    }

    public static GuidesController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        // Initialize the columns
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusGuideDisplay()));
        activeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsActiveDisplay()));
        bannedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsBannedDisplay()));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Load data
        loadGuides();

        // Add listener to table selection
        guidesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showGuideDetails(newValue));
    }

    private void loadGuides() {
        guidesList.setAll(guideService.getUsers());
        guidesTable.setItems(guidesList);
    }

    public void addGuide(User user) {
        guidesList.add(user);
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }

    @FXML
    private void handleAjouterGuide() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AjouterGuide.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Guide");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        try {
            User selectedUser = guidesTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedUser.setLastname(lastnameField.getText());
                selectedUser.setFirstname(firstnameField.getText());
                selectedUser.setEmail(emailField.getText());
                selectedUser.setPhone(phoneField.getText());
                selectedUser.setStatusGuideDisplay(statusField.getText());
                selectedUser.setIsActiveDisplay(activeField.getText());
                selectedUser.setIsBannedDisplay(bannedField.getText());

                guideService.updateUser(selectedUser);
                showMessage("Guide updated successfully!", "green");
                loadGuides();
            } else {
                showMessage("No guide selected.", "red");
            }
        } catch (EmptyFieldException | InvalidEmailException | InvalidPhoneNumberException |
                 IncorrectPasswordException | UserNotFoundException e) {
            showMessage(e.getMessage(), "red");
        } catch (NumberFormatException e) {
            showMessage("Invalid number format.", "red");
        }
    }

    private void clearGuideDetails() {
        idField.clear();
        firstnameField.clear();
        lastnameField.clear();
        emailField.clear();
        phoneField.clear();
        statusField.clear();
        activeField.clear();
        bannedField.clear();
    }

    private void showGuideDetails(User user) {
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            firstnameField.setText(user.getFirstname());
            lastnameField.setText(user.getLastname());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            statusField.setText(user.getStatusGuideDisplay());
            activeField.setText(user.getIsActiveDisplay());
            bannedField.setText(user.getIsBannedDisplay());
        } else {
            clearGuideDetails();
        }
    }

    @FXML
    private void handleSupprimer() {
        User selectedUser = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le guide");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce guide ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        guideService.deleteUser(selectedUser.getId());
                        guidesList.remove(selectedUser);
                        showMessage("Guide deleted successfully!", "green");
                    } catch (UserNotFoundException e) {
                        showMessage(e.getMessage(), "red");
                    }
                }
            });
        } else {
            showMessage("No guide selected.", "red");
        }
    }
}
