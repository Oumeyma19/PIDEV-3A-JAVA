package test;

import exceptions.*;
import models.User;
import services.ClientService;
import services.GuideService;
import services.UserService;
import util.Type;

public class Main {

    public static void main(String[] args) throws IncorrectPasswordException, InvalidPhoneNumberException, InvalidEmailException, EmptyFieldException, UserNotFoundException ,CustomIllegalStateException{
            // Singleton usage to create user instantiation
            UserService userService = UserService.getInstance();
        ClientService clientService = ClientService.getInstance();
        GuideService guideService;
        guideService = GuideService.getInstance();

            // **CRUD test:**

            // **add new user**
            User user = new User("Guide", "Guide", "Guide@gmail.com", "29923207", "Guide123", Type.GUIDE);
            guideService.addUser(user);

            // **update existing user**
             //User userUpdated = new User(43, "oumeyma", "zaafrane", "82@gmail.com", "12345678","Nadou000",20,"cfgvhb", Type.CLIENT, true, false);
             //clientService.updateUser(userUpdated);

            // **delete user**
             //guideService.deleteUser(41); // Replace 27 with the actual user ID

            // **get user by ID**
            // User retrievedUser = guideService.getUserbyID(31); // Replace 29 with the actual user ID
             //System.out.println(retrievedUser);

            // **get user by email**
            // User userByEmail = userService.getUserbyEmail("zaafranino@gmail.com");
            // System.out.println(userByEmail);

            // **get all users**
            // System.out.println(userService.getUsers());


    }
}
