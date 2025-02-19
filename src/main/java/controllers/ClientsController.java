package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.User;
import services.ClientService;

import java.io.IOException;

public class ClientsController {

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
    }

    private void loadClients() {
        clientsList.setAll(clientService.getUsers());
        clientsTable.setItems(clientsList);
    }

    public void addClient(User user) {
        clientsList.add(user);
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
}
