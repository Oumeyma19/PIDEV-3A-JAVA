package controllers;

import  exceptions.*;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import services.ClientService;

import javafx.scene.input.MouseEvent;
import services.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    private ComboBox<String> bannedFilter;


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

    @FXML
    private Pagination pagination;
    private ObservableList<User> filteredClientsList = FXCollections.observableArrayList(); // Liste filtrée



    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        navigateToProfile(event);
    }

    private void navigateToProfile(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProfileDashboard.fxml"));
            Parent root = loader.load();

            ProfileDashboardController profileController = loader.getController();
            profileController.setCurrentAdmin(SessionManager.getCurrentUser());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout(MouseEvent event) {
        SessionManager.clearSession(); // Clear the session
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleFilterByBanned() {
        filterClients();
    }

    private void filterClients() {
        String query = searchField.getText().toLowerCase();
        String bannedStatus = bannedFilter.getValue();

        // Filtrer la liste complète
        filteredClientsList.setAll(clientsList.stream()
            .filter(user -> (user.getLastname().toLowerCase().contains(query) ||
                user.getFirstname().toLowerCase().contains(query) ||
                user.getEmail().toLowerCase().contains(query) ||
                user.getPhone().toLowerCase().contains(query) ||
                String.valueOf(user.getPointsfid()).contains(query) ||
                (user.getNivfid() != null && user.getNivfid().toLowerCase().contains(query)) ||
                (user.getIsActive() ? "disponible" : "indisponible").contains(query) ||
                (user.getIsBanned() ? "oui" : "non").contains(query)) &&
                (bannedStatus.equals("Tous") ||
                    (bannedStatus.equals("Banni") && user.getIsBanned()) ||
                    (bannedStatus.equals("Non Banni") && !user.getIsBanned())))
            .collect(Collectors.toList()));

        // Mettre à jour la TableView avec la liste filtrée
        clientsTable.setItems(filteredClientsList);

        // Mettre à jour la pagination
        updatePagination();
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
             // Set the stage to fullscreen
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Guides.fxml: " + e.getMessage());
        }
    }


    private ClientService clientService = ClientService.getInstance();
    private ObservableList<User> clientsList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 6;

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

        // Load data
        loadClients();

        // Add listener to table selection
        clientsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showClientDetails(newValue));
        pagination.setPageFactory(this::createPage);
    }

    private void loadClients() {
        clientsList.setAll(clientService.getUsers());
        filteredClientsList.setAll(clientsList); // Initialiser la liste filtrée avec la liste complète
        clientsTable.setItems(filteredClientsList);
        updatePagination();
    }
    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredClientsList.size() / ROWS_PER_PAGE);
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
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredClientsList.size());

        // Mettre à jour les données de la TableView avec la sous-liste filtrée
        List<User> subList = filteredClientsList.subList(fromIndex, toIndex);
        ObservableList<User> pageData = FXCollections.observableArrayList(subList);

        // Update the UI on the JavaFX Application Thread
        Platform.runLater(() -> {
            clientsTable.setItems(pageData);
            clientsTable.refresh();
        });
    }

    public void addClient(User user) {
        clientsList.add(user);
        filteredClientsList.setAll(clientsList); // Mettre à jour la liste filtrée
        clientsTable.refresh();
        updatePagination();
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
            stage.centerOnScreen();
            stage.showAndWait(); // Attendre que la fenêtre se ferme

            // Recharger les clients après l'ajout
            loadClients();
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



    private void navigateTo(MouseEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void refreshTable() {
        clientsTable.getItems().setAll(clientService.getUsers());
    }

    @FXML
    private void handleDashboardClick(MouseEvent event) {
        try {
            // Charger le fichier FXML de Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            Parent root = loader.load();



            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Text) event.getSource()).getScene().getWindow();

            // Changer la scène
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Dashboard.fxml");
        }
    }



}
