package Controller;

import Models.ProgrammeFidelite;
import Service.ProgrammeFideliteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.List;
import java.util.Optional;

public class afficherlist {

    @FXML
    private TableView<ProgrammeFidelite> programmeTable;

    @FXML
    private TableColumn<ProgrammeFidelite, Integer> colId;

    @FXML
    private TableColumn<ProgrammeFidelite, String> colNom;

    @FXML
    private TableColumn<ProgrammeFidelite, Integer> colPoints;

    @FXML
    private TableColumn<ProgrammeFidelite, Void> colActions;

    private ProgrammeFideliteService service = new ProgrammeFideliteService();
    private ObservableList<ProgrammeFidelite> programmeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomProgramme"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));

        // Ajouter les boutons Supprimer et Modifier
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");

            {
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                updateButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");

                deleteButton.setOnAction(event -> {
                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                    handleDeleteProgramme(programme);
                });

                updateButton.setOnAction(event -> {
                    ProgrammeFidelite programme = getTableView().getItems().get(getIndex());
                    handleUpdateProgramme(programme);
                });
            }

            private final HBox container = new HBox(10, updateButton, deleteButton);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        loadProgrammes();
    }

    void loadProgrammes() {
        List<ProgrammeFidelite> programmes = service.getAllProgrammes();
        programmeList.setAll(programmes);
        programmeTable.setItems(programmeList);
    }

    private void handleDeleteProgramme(ProgrammeFidelite programme) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce programme ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            service.deleteProgramme(programme.getId());
            loadProgrammes();
        }
    }

    private void handleUpdateProgramme(ProgrammeFidelite programme) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pidev/updateprog.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur et lui passer le programme sélectionné
            updateprog controller = loader.getController();
            controller.initData(programme, this);

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Programme");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void handleAddProgramme(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter un Programme");
        dialog.setHeaderText("Ajouter un nouveau programme de fidélité");
        dialog.setContentText("Nom du Programme:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nomProgramme -> {
            ProgrammeFidelite newProgramme = new ProgrammeFidelite(0, nomProgramme, 0); // 0 points par défaut
            service.addProgramme(newProgramme);
            loadProgrammes();
        });
    }
}
