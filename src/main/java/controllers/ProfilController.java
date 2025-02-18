package controllers;

import exceptions.EmptyFieldException;
import exceptions.InvalidEmailException;
import exceptions.InvalidPhoneNumberException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;
import util.Type;

import java.io.IOException;

public class ProfilController {

    @FXML
    private ImageView logoutImage;

    @FXML
    private Label nomUserLabel;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private Label errorLabel; // Référence au Label pour les messages

    private User currentUser;
    private UserService userService = UserService.getInstance();
    private ClientService clientService = ClientService.getInstance();
    private GuideService guideService = GuideService.getInstance();

    @FXML
    public void initialize() {
        // Activer la navigation Tab pour tous les champs
        nomField.setFocusTraversable(true);
        prenomField.setFocusTraversable(true);
        emailField.setFocusTraversable(true);
        telephoneField.setFocusTraversable(true);

        // Remplir les champs avec les informations de l'utilisateur
        if (currentUser != null) {
            nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
            nomField.setText(currentUser.getFirstname());
            prenomField.setText(currentUser.getLastname());
            emailField.setText(currentUser.getEmail());
            telephoneField.setText(currentUser.getPhone());
        }

        // Déplacer le focus vers un autre élément (par exemple, un Label ou un Pane)
        javafx.application.Platform.runLater(() -> {
            Pane rootPane = (Pane) nomField.getParent();
            rootPane.requestFocus();
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initialize();
    }

    @FXML
    private void handleLogout(javafx.scene.input.MouseEvent event) {
        try {
            // Charger le fichier FXML du SignIn
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et changer de scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        if (currentUser != null) {
            // Mettre à jour les informations de l'utilisateur
            currentUser.setFirstname(nomField.getText());
            currentUser.setLastname(prenomField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(telephoneField.getText());

            try {
                // Appeler la méthode updateBasicInfo appropriée en fonction du rôle de l'utilisateur
                switch (currentUser.getRoles()) {
                    case ADMIN:
                        userService.updateBasicUserInfo(currentUser);
                        break;
                    case CLIENT:
                        clientService.updateBasicClientInfo(currentUser);
                        break;
                    case GUIDE:
                        guideService.updateBasicGuideInfo(currentUser);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown user role: " + currentUser.getRoles());
                }

                // Mettre à jour le nomUserLabel avec les nouvelles valeurs
                nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());

                // Afficher un message de succès
                showMessage("Les informations ont été mises à jour avec succès.", "green");
            } catch (EmptyFieldException e) {
                showMessage("Veuillez remplir tous les champs obligatoires.", "red");
            } catch (InvalidEmailException e) {
                showMessage("L'adresse email est invalide.", "red");
            } catch (InvalidPhoneNumberException e) {
                showMessage("Le numéro de téléphone est invalide.", "red");
            } catch (Exception e) {
                showMessage("Une erreur s'est produite lors de la mise à jour des informations: " + e.getMessage(), "red");
            }
        } else {
            showMessage("Aucun utilisateur n'est connecté.", "red");
        }
    }

    private void showMessage(String message, String color) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: " + color + ";");
        errorLabel.setVisible(true);
    }
}
