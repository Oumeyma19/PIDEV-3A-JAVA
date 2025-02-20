package com.example.pidev.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.pidev.Exceptions.*;
import com.example.pidev.Util.Type;
import com.example.pidev.Exceptions.interfaces.UserInterface;
import com.example.pidev.models.User;
import com.example.pidev.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserService implements UserInterface {
    private final Connection connection = MyConnection.getInstance().getConnection();
    ValidationService validationService = new ValidationService();
    private static UserService instance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    @Override
    public void addUser(User user) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException,
            IncorrectPasswordException {
        // Vérifier que les champs obligatoires ne sont pas vides
        if (user.getFirstname().isEmpty() || user.getLastname().isEmpty() || user.getEmail().isEmpty() ||
                user.getPassword().isEmpty()) {
            throw new EmptyFieldException("Please fill in all required fields.");
        }
        // Valider le format de l'email
        if (!validationService.isValidEmail(user.getEmail())) {
            throw new InvalidEmailException("Invalid email, please check your email address.");
        }
        if (isEmailExists(user.getEmail())) {
            throw new InvalidEmailException("Email already exists. Please use a different email.");
        }
        // Valider le format du numéro de téléphone (s'il est fourni)
        if (!user.getPhone().isEmpty() && !validationService.isValidPhoneNumber(user.getPhone())) {
            throw new InvalidPhoneNumberException("Invalid phone number format.");
        }
        // Valider le format du mot de passe
        if (!validationService.isValidPassword(user.getPassword())) {
            throw new IncorrectPasswordException("Password must contain at least one uppercase letter, " +
                    "one lowercase letter, one digit, and be at least 6 characters long.");
        }
        String request = "INSERT INTO `user`(`firstname`, `lastname`, `email`, `phone`, `password`, `roles`, `is_active`, `is_banned`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setString(5, cryptPassword(user.getPassword()));
            preparedStatement.setString(6, user.getRoles().toString());
            preparedStatement.setBoolean(7, true);
            preparedStatement.setBoolean(8, false);

            preparedStatement.executeUpdate();
            System.out.println("User added successfully!");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private String cryptPassword(String passwordToCrypt) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToChar(13, passwordToCrypt.toCharArray());
        return Stream
                .of(bcryptChars)
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }

    public boolean verifyPassword(String passwordToBeVerified, String encryptedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(passwordToBeVerified.toCharArray(), encryptedPassword);
        boolean verified = result.verified;
        if (!verified) {
            System.out.println("Password incorrect. Forgotten your password? ");
        }
        return verified;
    }



    private boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("Error checking email existence: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public void updateUser(User user) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException, IncorrectPasswordException, UserNotFoundException {
        // Check if the user object is null
        if (user == null) {
            throw new UserNotFoundException("This account doesn't exist. Cannot update account.");
        }

        // Check if the user ID is valid
        if (user.getId() <= 0) {
            throw new UserNotFoundException("This account doesn't exist. Cannot update account.");
        }

        // Check if any of the mandatory fields are empty
        if (user.getFirstname().isEmpty() || user.getLastname().isEmpty() || user.getEmail().isEmpty()) {
            throw new EmptyFieldException("Please fill in all required fields.");
        }

        // Validate email format
        if (!validationService.isValidEmail(user.getEmail())) {
            throw new InvalidEmailException("Invalid email address.");
        }

        // Validate phone number format (if provided)
        if (!user.getPhone().isEmpty() && !validationService.isValidPhoneNumber(user.getPhone())) {
            throw new InvalidPhoneNumberException("Invalid phone number format.");
        }

        if (!validationService.isValidPassword(user.getPassword())) {
            throw new IncorrectPasswordException("Password must contain at least one uppercase letter, one lowercase letter, one digit, and be at least 6 characters long.");
        }

        // Prepare SQL update statement
        String request = "UPDATE user SET firstname = ?, lastname = ?, email = ?, phone = ?, is_banned = ?, is_active = ? WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setBoolean(5, user.getIsBanned());
            preparedStatement.setBoolean(6, user.getIsActive());
            preparedStatement.setInt(7, user.getId());

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user. User not found or no changes made.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    @Override
    public void deleteUser(int id)throws UserNotFoundException {
        User user = getUserbyID(id);
        String request = "DELETE FROM `user` WHERE `Id` =" + user.getId() + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(request);
            System.out.println("User is deleted successfully");

        } catch (SQLException ex) {
            System.err.println();
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            String request = "SELECT * FROM user WHERE roles = 'ADMIN'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("password"),
                    Type.ADMIN,
                    resultSet.getBoolean("is_banned"),
                    resultSet.getBoolean("is_active")
                );
                users.add(user);
            }

            if (users.isEmpty()) {
                System.out.println("⚠ Aucun administrateur trouvé dans la base de données.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des admins : " + ex.getMessage());
        }
        return users;
    }




    @Override
    public User getUserbyID(int id) throws UserNotFoundException {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
        //    preparedStatement.setString(2, Type.ADMIN.name());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No user found with ID: " + id);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving ADMIN user with ID " + id + ": " + ex.getMessage());
        }
        return user;
    }

    @Override
    public User getUserbyEmail(String email) throws UserNotFoundException {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE email = ? AND roles = ? LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, Type.ADMIN.name());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No ADMIN user found with email: " + email);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving ADMIN user by email " + email + ": " + ex.getMessage());
        }
        return user;
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String firstname = resultSet.getString("firstname");
        String lastname = resultSet.getString("lastname");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        String password = resultSet.getString("password");
        String roleString = resultSet.getString("roles");
        Type roles = null;
        try {
            roles = Type.valueOf(roleString);
        } catch (IllegalArgumentException ignored) {
        }
        boolean is_banned = resultSet.getBoolean("is_banned");
        boolean is_active = resultSet.getBoolean("is_active");
        return new User(id, firstname, lastname, email, phone, password, roles, is_banned, is_active);
    }
}
