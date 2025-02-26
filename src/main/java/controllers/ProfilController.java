package controllers;

import exceptions.EmptyFieldException;
import exceptions.InvalidEmailException;
import exceptions.InvalidPhoneNumberException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;

import java.io.IOException;

public class ProfilController {

    @FXML
    private ImageView logoutImage;

    @FXML
    private Label nomUserLabel;

    @FXML
    private TextField nomField;

    @FXML
    private ImageView homeImage;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private Label errorLabel;

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
        if (user != null) {
            nomUserLabel.setText(user.getFirstname() + " " + user.getLastname());
            nomField.setText(user.getFirstname());
            prenomField.setText(user.getLastname());
            emailField.setText(user.getEmail());
            telephoneField.setText(user.getPhone());
        }
    }

    @FXML
    private void handleLogout(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleReservationO(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservation_list.fxml"));
            Parent root = loader.load();
            ReservationListController ReservationListController = loader.getController();
            ReservationListController.setLoggedInUser(currentUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChangePassword.fxml"));
            Parent root = loader.load();
            ChangePasswordController changePasswordController = loader.getController();
            changePasswordController.setCurrentUser(currentUser);
            Stage stage = (Stage) logoutImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la redirection vers la page de modification du mot de passe : " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (currentUser != null) {
            currentUser.setFirstname(nomField.getText());
            currentUser.setLastname(prenomField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(telephoneField.getText());

            try {
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
                nomUserLabel.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
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

    @FXML
    private void handleDeleteAccount() {
        if (currentUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le compte");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce compte ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        switch (currentUser.getRoles()) {
                            case ADMIN:
                                userService.deleteUser(currentUser.getId());
                                break;
                            case CLIENT:
                                clientService.deleteUser(currentUser.getId());
                                break;
                            case GUIDE:
                                guideService.deleteUser(currentUser.getId());
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown user role: " + currentUser.getRoles());
                        }
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) logoutImage.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        showMessage("Erreur lors de la redirection vers la page de connexion: " + e.getMessage(), "red");
                    } catch (Exception e) {
                        showMessage("Une erreur s'est produite lors de la suppression du compte: " + e.getMessage(), "red");
                    }
                }
            });
        } else {
            showMessage("Aucun utilisateur n'est connecté.", "red");
        }
    }

    private void showMessage(String message, String color) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: " + color + ";");
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleHome(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();
            HomeController homeController = loader.getController();
            homeController.setCurrentUser(currentUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
