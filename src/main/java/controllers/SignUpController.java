package controllers;

import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;
import services.ValidationService;
import util.Type;

import java.io.IOException;

public class SignUpController {
    private UserService userService = UserService.getInstance();
    private ClientService clientService = ClientService.getInstance();
    private GuideService guideService = GuideService.getInstance();

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private RadioButton clientRadioButton;

    @FXML
    private RadioButton guideRadioButton;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private Button registerButton;

    @FXML
    private Button signInButton;

    @FXML
    private Label errorLabel;

    private ValidationService validationService = new ValidationService();

    @FXML
    public void initialize() {
        roleGroup = new ToggleGroup();
        clientRadioButton.setToggleGroup(roleGroup);
        guideRadioButton.setToggleGroup(roleGroup);
    }

    @FXML
    private void handleRegister() {
        System.out.println("handleRegister appelé");
        try {
            String firstname = firstnameField.getText().trim();
            String lastname = lastnameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();

            Type role;

            // Déterminer le rôle sélectionné
            if (clientRadioButton.isSelected()) {
                role = Type.CLIENT;
            } else {
                role = Type.GUIDE;
            }

            // Valider les champs
            validateFields(firstname, lastname, email, phone, password);

            // Créer un nouvel utilisateur
            User user = new User();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setRoles(role);
            user.setIsActive(true); // Set default value
            user.setIsBanned(false); // Set default value

            // Ajouter l'utilisateur au service approprié
            if (role == Type.CLIENT) {
                clientService.addUser(user);
            } else {
                guideService.addUser(user);
            }

            // Récupérer l'utilisateur depuis la base de données pour s'assurer qu'il est correctement lié
            User savedUser;
            if (role == Type.CLIENT) {
                savedUser = clientService.getUserbyEmail(email);
            } else {
                savedUser = guideService.getUserbyEmail(email);
            }

            // Message de succès
            errorLabel.setText("Inscription réussie !");
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setVisible(true);

            // Redirection en fonction du rôle
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            try {
                                redirectToProfil(savedUser); // Rediriger vers Profil.fxml pour les autres rôles
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                },
                2000 // Délai de 2 secondes avant la redirection
            );

        } catch (EmptyFieldException | InvalidEmailException | InvalidPhoneNumberException | IncorrectPasswordException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Une erreur s'est produite lors de l'inscription.");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    private void redirectToProfil(User user) throws IOException {
        System.out.println("Redirection vers Profil.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
        Parent root = loader.load();

        // Passer les données de l'utilisateur au contrôleur ProfilController
        ProfilController profilController = loader.getController();
        profilController.setCurrentUser(user);

        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleSignIn() throws IOException {
        System.out.println("handleSignIn appelé"); // Log de débogage
        redirectToSignIn();
    }

    private void redirectToSignIn() throws IOException {
        try {
            // Redirection vers la page d'inscription (à remplir plus tard)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignIn.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Impossible de charger la page d'inscription.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true); // Ensure it appears
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void validateFields(String firstname, String lastname, String email, String phone, String password)
        throws EmptyFieldException, InvalidEmailException, InvalidPhoneNumberException, IncorrectPasswordException {
        // Vérifier que les champs obligatoires ne sont pas vides
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new EmptyFieldException("Veuillez remplir tous les champs obligatoires.");
        }

        // Valider le format de l'email
        if (!validationService.isValidEmail(email)) {
            throw new InvalidEmailException("Format d'email invalide.");
        }

        // Valider le format du numéro de téléphone (s'il est fourni)
        if (!phone.isEmpty() && !validationService.isValidPhoneNumber(phone)) {
            throw new InvalidPhoneNumberException("Format de numéro de téléphone invalide.");
        }

        // Valider le format du mot de passe
        if (!validationService.isValidPassword(password)) {
            throw new IncorrectPasswordException("Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et faire au moins 6 caractères.");
        }
    }
}
