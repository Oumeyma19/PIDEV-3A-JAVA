package controllers;

import models.Airport;
import services.AirportService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.AirportAPIUtil;
import util.AirportCSVUtil;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import static util.AirportAPIUtil.searchAirports;

public class AirportView {
    @FXML private TableView<Airport> airportTable;
    @FXML private TableColumn<Airport, String> nameColumn;
    @FXML private TableColumn<Airport, String> locationColumn;
    @FXML private TableColumn<Airport, String> codeColumn;
    @FXML private TableColumn<Airport, Void> actionsColumn;

    private final AirportService airportService = new AirportService(); // AirportService instance

    public void initialize() {
        // Set up columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());

        // Set up actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<Airport, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button updateButton = new Button("Update");
                    updateButton.setOnAction(event -> handleUpdate(getTableView().getItems().get(getIndex())));

                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));

                    HBox hbox = new HBox(10, updateButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });

        // Load data into TableView
        refreshTable();
    }

    // Fetch airports using AirportService
    private void refreshTable() {
        airportTable.getItems().setAll(airportService.afficher());
    }

    private void handleUpdate(Airport airport) {
        // Create a custom dialog
        Dialog<Airport> dialog = new Dialog<>();
        dialog.setTitle("Update Airport");
        dialog.setHeaderText("Edit Airport Details");

        // Set the buttons
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField(airport.getNameAirport());
        TextField locationField = new TextField(airport.getLocation());
        TextField codeField = new TextField(airport.getCode());

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationField, 1, 1);
        grid.add(new Label("Code:"), 0, 2);
        grid.add(codeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert result to Airport object when the Update button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Airport(airport.getIdAirport(), nameField.getText(), locationField.getText(), codeField.getText());
            }
            return null;
        });

        // Show and wait for user input
        dialog.showAndWait().ifPresent(updatedAirport -> {
            airportService.modifier(updatedAirport, airport.getNameAirport());
            refreshTable();
        });
    }


    // Handle delete button click
    private void handleDelete(Airport airport) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Airport");
        alert.setHeaderText("Are you sure you want to delete this airport?");
        alert.setContentText("Airport: " + airport.getNameAirport());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                airportService.supprimer(airport);
                refreshTable();
            }
        });
    }

    @FXML
    private void handleAddAirport() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddAirport.fxml"));
            Parent root = loader.load();

            // Get references to the input fields
            TextField nameField = (TextField) root.lookup("#nameField");
            TextField locationField = (TextField) root.lookup("#locationField");
            TextField codeField = (TextField) root.lookup("#codeField");
            ListView<Airport> suggestionsListView = (ListView<Airport>) root.lookup("#suggestionsListView");

            // Load airports from the CSV file
            List<Airport> airports = AirportCSVUtil.loadAirportsFromCSV("src/main/resources/airports.csv");

            // Set up auto-complete for the name field
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() >= 3) { // Trigger search after 3 characters
                    List<Airport> filteredAirports = searchAirports(newValue, airports);
                    suggestionsListView.getItems().setAll(filteredAirports); // Update the ListView with results
                } else {
                    suggestionsListView.getItems().clear(); // Clear the ListView if the input is too short
                }
            });

            // Handle selection from the ListView
            suggestionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    nameField.setText(newValue.getNameAirport());
                    locationField.setText(newValue.getLocation());
                    codeField.setText(newValue.getCode());
                }
            });

            // Create a new stage
            Stage stage = new Stage();
            stage.setTitle("Add New Airport");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait until the user closes the window

            // Refresh the TableView after closing the AddAirport window
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
