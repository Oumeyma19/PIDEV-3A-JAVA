package controllers;

import services.PasswordResetService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RandomCodeController {

    @FXML
    private Label InvalidCode;

    @FXML
    private TextField digit1, digit2, digit3, digit4;

    @FXML
    private Label phoneNumberLabel;

    private PasswordResetService passwordResetService = PasswordResetService.getInstance();
    private String phoneNumber;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        phoneNumberLabel.setText("Code envoyé à : " + phoneNumber);
    }

    @FXML
    private void submitBtn(ActionEvent event) throws IOException {
        String calculatedCode = digit1.getText() + digit2.getText() + digit3.getText() + digit4.getText();

        if (passwordResetService.verifySMSCode(phoneNumber, calculatedCode)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChangeMdp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            InvalidCode.setText("Code de vérification invalide");
            InvalidCode.setVisible(true);
        }
    }
}