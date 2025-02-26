package controllers;

import exceptions.EmptyFieldException;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidEmailException;
import exceptions.InvalidPhoneNumberException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import services.ClientService;
import services.ValidationService;
import util.Type;

public class AjouterClientController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private ClientService clientService = ClientService.getInstance();
    private ValidationService validationService = new ValidationService();

    @FXML
    private void handleEnregistrer() {
        try {
            // Validate input fields
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
                phoneField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                throw new EmptyFieldException("All fields must be filled.");
            }

            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = passwordField.getText();

            // Validate email, phone number, and password
            if (!validationService.isValidEmail(email)) {
                throw new InvalidEmailException("Invalid email format.");
            }
            if (!validationService.isValidPhoneNumber(phone)) {
                throw new InvalidPhoneNumberException("Invalid phone number format.");
            }
            if (!validationService.isValidPassword(password)) {
                throw new IncorrectPasswordException("Password does not meet the requirements.");
            }

            // Set default values for points de fidélité, niveau de fidélité, active, and ban
            int pointsFid = 0;
            String nivFid = null;
            boolean isActive = true;
            boolean isBanned = false;

            User newUser = new User(nom, prenom, email, phone, pointsFid, nivFid, isBanned, isActive);
            newUser.setRoles(Type.CLIENT); // Set the role to CLIENT
            newUser.setPassword(clientService.cryptPassword(password)); // Encrypt the password

            // Add user using ClientService
            clientService.addUser(newUser);
            showMessage("Client added successfully!", "green");

            // Update the TableView in ClientsController
            ClientsController.getInstance().addClient(newUser);
        } catch (EmptyFieldException e) {
            showMessage(e.getMessage(), "red");
        } catch (InvalidEmailException e) {
            showMessage(e.getMessage(), "red");
        } catch (InvalidPhoneNumberException e) {
            showMessage(e.getMessage(), "red");
        } catch (IncorrectPasswordException e) {
            showMessage(e.getMessage(), "red");
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
    }
}
