package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.User;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button profileButton;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        if (profileButton != null) {
            profileButton.setOnAction(event -> handleProfile());
        }
    }

    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Profil.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ProfilController
            ProfilController profilController = loader.getController();
            profilController.setCurrentUser(currentUser);

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOffers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReservationOffre.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ReservationOffersController
            ReservationOffersController reservationOffersController = loader.getController();
            if (currentUser != null) {
                reservationOffersController.setCurrentUser(currentUser);
                System.out.println("User passed to ReservationOffersController: " + currentUser.getFirstname()); // Debugging
            } else {
                System.out.println("currentUser is NULL!"); // Debugging
            }

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleVol() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightSearch.fxml"));
            Parent root = loader.load();

            // Pass the user data to the ReservationOffersController
            FlightSearchController flightSearchController = loader.getController();
            if (currentUser != null) {
                flightSearchController.setCurrentUser(currentUser);
                System.out.println("User passed to ReservationOffersController: " + currentUser.getFirstname()); // Debugging
            } else {
                System.out.println("currentUser is NULL!"); // Debugging
            }

            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservation_list.fxml"));
            Parent root = loader.load();

            // Get controller
            ReservationListController reservationListController = loader.getController();

            // Pass the logged-in user
            reservationListController.setLoggedInUser(currentUser); // Pass the user

            // Show the scene
            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
