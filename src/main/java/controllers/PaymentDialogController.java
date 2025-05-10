package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PaymentDialogController {
    @FXML private TextField txtCardNumber;
    @FXML private TextField txtExpirationDate;
    @FXML private TextField txtCVC;

    public String getCardNumber() {
        return txtCardNumber.getText().replaceAll("\\s+", ""); // Remove spaces
    }

    public String getExpirationDate() {
        return txtExpirationDate.getText();
    }

    public String getCVC() {
        return txtCVC.getText();
    }

    @FXML
    private void handlePayment() {
        // Validate input
        if (getCardNumber().isEmpty() || getExpirationDate().isEmpty() || getCVC().isEmpty()) {
            System.out.println("Please fill in all payment details.");
            return;
        }

        // Simulate payment processing
        System.out.println("Payment processed successfully!");
        System.out.println("Card Number: " + getCardNumber());
        System.out.println("Expiration Date: " + getExpirationDate());
        System.out.println("CVC: " + getCVC());

        // Close the dialog (you can add logic to close the dialog here)
    }
}