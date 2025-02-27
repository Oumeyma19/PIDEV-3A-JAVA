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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import services.GuideService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuidesController {

    @FXML
    private TableView<User> guidesTable;
    @FXML
    private TableColumn<User, String> lastnameColumn;
    @FXML
    private TableColumn<User, String> firstnameColumn;
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
    private Label messageLabel;
    @FXML
    private Label greetingLabel;

    @FXML
    private ImageView userImage;

    @FXML
    private Text fullnameText;

    private User currentUser;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> bannedFilter;

    @FXML
    private Pagination pagination;

    private GuideService guideService = GuideService.getInstance();
    private ObservableList<User> guidesList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 6;
    private ObservableList<User> filteredGuidesList = FXCollections.observableArrayList();


    private static GuidesController instance;

    public GuidesController() {
        instance = this;
    }

    public static GuidesController getInstance() {
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            fullnameText.setText("Bonjour, " + user.getFirstname());
        }
    }

    @FXML
    public void initialize() {
        // Initialize the columns
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusGuideDisplay()));
        activeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsActiveDisplay()));
        bannedColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsBannedDisplay()));
        //passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // Load data
        loadGuides();

        // Add listener to table selection
        guidesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showGuideDetails(newValue));
        pagination.setPageFactory(this::createPage);
    }


    private void loadGuides() {
        guidesList.setAll(guideService.getUsers());
        filteredGuidesList.setAll(guidesList); // Initialiser la liste filtrée avec la liste complète
        guidesTable.setItems(filteredGuidesList);
        updatePagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredGuidesList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        updateTableView(0);
    }

    private Node createPage(int pageIndex) {
        updateTableView(pageIndex);
        return new AnchorPane(); // Retournez un Node vide
    }
    private void updateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredGuidesList.size());

        // Mettre à jour les données de la TableView avec la sous-liste filtrée
        List<User> subList = filteredGuidesList.subList(fromIndex, toIndex);
        ObservableList<User> pageData = FXCollections.observableArrayList(subList);
        guidesTable.setItems(pageData);
        guidesTable.refresh();
    }


    public void addGuide(User user) {
        guidesList.add(user);
        guidesTable.refresh();
        updatePagination();
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }

    @FXML
    private void handleFilterByBanned() {
        filterGuides();
    }

    private void filterGuides() {
        String query = searchField.getText().toLowerCase();
        String bannedStatus = bannedFilter.getValue();

        // Filtrer la liste complète
        filteredGuidesList.setAll(guidesList.stream()
            .filter(user -> (user.getLastname().toLowerCase().contains(query) ||
                user.getFirstname().toLowerCase().contains(query) ||
                user.getEmail().toLowerCase().contains(query) ||
                user.getPhone().toLowerCase().contains(query) ||
                user.getStatusGuideDisplay().toLowerCase().contains(query) ||
                user.getIsActiveDisplay().toLowerCase().contains(query) ||
                user.getIsBannedDisplay().toLowerCase().contains(query)) &&
                (bannedStatus.equals("Tous") ||
                    (bannedStatus.equals("Banni") && user.getIsBanned()) ||
                    (bannedStatus.equals("Non Banni") && !user.getIsBanned())))
            .collect(Collectors.toList()));

        // Mettre à jour la TableView avec la liste filtrée
        guidesTable.setItems(filteredGuidesList);

        // Mettre à jour la pagination
        updatePagination();
    }

    @FXML
    private void handleAjouterGuide() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AjouterGuide.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Guide");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre se ferme

            // Recharger les guides après l'ajout
            loadGuides();
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
    }

    private void showGuideDetails(User user) {
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            firstnameField.setText(user.getFirstname());
            lastnameField.setText(user.getLastname());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
        } else {
            clearGuideDetails();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        ObservableList<User> filteredList = guidesList.stream()
            .filter(user -> user.getLastname().toLowerCase().contains(query) ||
                user.getFirstname().toLowerCase().contains(query) ||
                user.getEmail().toLowerCase().contains(query) ||
                user.getPhone().toLowerCase().contains(query) ||
                user.getStatusGuideDisplay().toLowerCase().contains(query) ||
                user.getIsActiveDisplay().toLowerCase().contains(query) ||
                user.getIsBannedDisplay().toLowerCase().contains(query))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));
        guidesTable.setItems(filteredList);

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
             // Set the stage to fullscreen
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Clients.fxml: " + e.getMessage());
        }
    }


}
