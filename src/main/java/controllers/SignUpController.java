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
import services.SessionManager; // Importer SessionManager
import services.UserService;
import services.ValidationService;
import util.Type;

import java.io.IOException;

public class SignUpController {
    private UserService userService = UserService.getInstance();
    private ClientService clientService = ClientService.getInstance();

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField lastnameField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button signInButton;

    private ValidationService validationService = new ValidationService();

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }

    @FXML
    public void handleRegister() {
        System.out.println("handleRegister appelé");
        try {
            String firstname = firstnameField.getText().trim();
            String lastname = lastnameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Set role to CLIENT
            Type role = Type.CLIENT;

            // Validate fields
            validateFields(firstname, lastname, email, phone, password, confirmPassword);

            // Create a new user
            User user = new User();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setRoles(role);
            user.setIsActive(true); // Set default value
            user.setIsBanned(false); // Set default value

            // Add the user to the client service
            clientService.addUser(user);

            // Retrieve the user from the database to ensure it is correctly linked
            User savedUser = clientService.getUserbyEmail(email);

            // Save session
            SessionManager.saveSession(savedUser.getEmail(), savedUser.getRoles().toString());

            // Success message
            errorLabel.setText("Inscription réussie !");
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setVisible(true);

            // Redirect based on the role
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            try {
                                redirectToHome(savedUser); // Redirect to Home.fxml
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                },
                2000 // 2-second delay before redirection
            );

        } catch (EmptyFieldException | InvalidEmailException | InvalidPhoneNumberException | IncorrectPasswordException | PasswordMismatchException e) {
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

    @FXML
    public void redirectToHome(User user) throws IOException {
        System.out.println("Redirection vers Home.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
        Parent root = loader.load();

        // Pass user data to HomeController
        HomeController homeController = loader.getController();
        homeController.setCurrentUser(user);

        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void handleSignIn() throws IOException {
        System.out.println("handleSignIn appelé"); // Debug log
        redirectToSignIn();
    }

    @FXML
    public void redirectToSignIn() throws IOException {
        try {
            // Redirect to the sign-in page
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

    private void validateFields(String firstname, String lastname, String email, String phone, String password, String confirmPassword)
        throws EmptyFieldException, InvalidEmailException, InvalidPhoneNumberException, IncorrectPasswordException, PasswordMismatchException {
        // Check that required fields are not empty
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new EmptyFieldException("Veuillez remplir tous les champs obligatoires.");
        }

        // Validate email format
        if (!validationService.isValidEmail(email)) {
            throw new InvalidEmailException("Format d'email invalide.");
        }

        // Validate phone number format (if provided)
        if (!phone.isEmpty() && !validationService.isValidPhoneNumber(phone)) {
            throw new InvalidPhoneNumberException("Format de numéro de téléphone invalide.");
        }

        // Validate password format
        if (!validationService.isValidPassword(password)) {
            throw new IncorrectPasswordException("Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et faire au moins 6 caractères.");
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException("Les mots de passe ne correspondent pas.");
        }
    }
}
