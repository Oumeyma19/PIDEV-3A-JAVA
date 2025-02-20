package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import services.GuideService;
import services.ValidationService;
import exceptions.EmptyFieldException;
import exceptions.InvalidEmailException;
import exceptions.InvalidPhoneNumberException;
import exceptions.IncorrectPasswordException;
import models.User;
import util.Type;

public class AjouterGuideController {

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
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<String> activeComboBox;
    @FXML
    private ComboBox<String> banComboBox;
    @FXML
    private Label messageLabel;

    private GuideService guideService = GuideService.getInstance();
    private ValidationService validationService = new ValidationService();

    @FXML
    private void handleEnregistrer() {
        try {
            // Validate input fields
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
                phoneField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                statusComboBox.getValue() == null || activeComboBox.getValue() == null || banComboBox.getValue() == null) {
                throw new EmptyFieldException("All fields must be filled.");
            }

            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = passwordField.getText();
            boolean status = statusComboBox.getValue().equals("Disponible");
            boolean isActive = activeComboBox.getValue().equals("Oui");
            boolean isBanned = banComboBox.getValue().equals("Non");

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

            User newUser = new User(prenom, nom, email, phone, password, status, isBanned, isActive);
            newUser.setRoles(Type.GUIDE); // Set the role to GUIDE
            newUser.setPassword(guideService.cryptPassword(password)); // Encrypt the password

            // Add user using GuideService
            guideService.addUser(newUser);
            showMessage("Guide added successfully!", "green");

            // Update the TableView in GuidesController
            GuidesController.getInstance().addGuide(newUser);
        } catch (EmptyFieldException e) {
            showMessage(e.getMessage(), "red");
        } catch (InvalidEmailException e) {
            showMessage(e.getMessage(), "red");
        } catch (InvalidPhoneNumberException e) {
            showMessage(e.getMessage(), "red");
        } catch (NumberFormatException e) {
            showMessage("Invalid number format for status.", "red");
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
