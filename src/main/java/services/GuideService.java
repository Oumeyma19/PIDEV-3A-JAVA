package services;
import at.favre.lib.crypto.bcrypt.BCrypt;
import exceptions.*;
import models.User;
import tools.MyDataBase;
import util.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuideService implements UserInterface {
    private Connection connection = MyDataBase.getInstance().getCnx();
    ValidationService validationService = new ValidationService();
    private static GuideService instance;

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    private GuideService() {
    }

    public static GuideService getInstance() {
        if (instance == null) {
            instance = new GuideService();
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
        String request = "INSERT INTO `user`(`firstname`, `lastname`, `email` ,`phone`,`password`,`statusGuide`,`roles`,`is_active`,`is_banned`) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setString(5, cryptPassword(user.getPassword()));
            preparedStatement.setBoolean(6, true);
            preparedStatement.setString(7, user.getRoles().toString());
            preparedStatement.setBoolean(8, true);  // is_active = true
            preparedStatement.setBoolean(9, false);
            preparedStatement.executeUpdate();
            System.out.println("Guide added successfully !");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void updatePassword(int userId, String newPassword) throws UserNotFoundException, IncorrectPasswordException, EmptyFieldException {
        // Vérifier que le nouveau mot de passe n'est pas vide
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new EmptyFieldException("Le nouveau mot de passe ne peut pas être vide.");
        }

        // Valider le format du mot de passe
        if (!validationService.isValidPassword(newPassword)) {
            throw new IncorrectPasswordException("Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et faire au moins 6 caractères.");
        }

        // Crypter le nouveau mot de passe
        String encryptedPassword = cryptPassword(newPassword);

        // Mettre à jour le mot de passe dans la base de données
        String request = "UPDATE user SET password = ? WHERE id = ? AND roles = 'GUIDE'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, encryptedPassword);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("Aucun guide trouvé avec l'ID : " + userId);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour du mot de passe : " + ex.getMessage());
        }
    }

    public String cryptPassword(String passwordToCrypt) {
        char[] bcryptChars = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToChar(13, passwordToCrypt.toCharArray());
        return Stream
                .of(bcryptChars)
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }

    public boolean verifyPassword(String passwordToBeVerified, String encryptedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(passwordToBeVerified.toCharArray(), encryptedPassword);
        return result.verified;
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
        String request = "UPDATE user SET firstname = ?, lastname = ?, email = ?, phone = ?, statusGuide = ?, is_banned = ?, is_active = ? WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setBoolean(5, user.getStatusGuide());
            preparedStatement.setBoolean(6, user.getIsBanned());
            preparedStatement.setBoolean(7, user.getIsActive());
            preparedStatement.setInt(8, user.getId());

            // Execute the update statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Guide updated successfully!");
            } else {
                System.out.println("Failed to update guide. Client not found or no changes made.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
        }
    }

    public void updateBasicGuideInfo(User user) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException {
        // Validation des champs obligatoires
        if (user.getFirstname().isEmpty() || user.getLastname().isEmpty() || user.getEmail().isEmpty()) {
            throw new EmptyFieldException("Please fill in all required fields.");
        }

        // Validation de l'email
        if (!validationService.isValidEmail(user.getEmail())) {
            throw new InvalidEmailException("Invalid email address.");
        }


        // Validation du numéro de téléphone (si fourni)
        if (!user.getPhone().isEmpty() && !validationService.isValidPhoneNumber(user.getPhone())) {
            throw new InvalidPhoneNumberException("Invalid phone number format.");
        }

        // Requête SQL pour mettre à jour les informations de base
        String request = "UPDATE user SET firstname = ?, lastname = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Guide basic info updated successfully!");
            } else {
                System.out.println("Failed to update guide basic info.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating guide basic info: " + ex.getMessage());
        }
    }

    @Override
    public void deleteUser(int id)throws UserNotFoundException {
        User user = getUserbyID(id);
        String request = "DELETE FROM `user` WHERE `Id` =" + user.getId() + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(request);
            System.out.println("Guide is deleted successfully");

        } catch (SQLException ex) {
            System.err.println();
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            String request = "SELECT * FROM user WHERE roles = 'GUIDE'";
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
                        resultSet.getBoolean("statusGuide"),
                        Type.GUIDE,
                        resultSet.getBoolean("is_banned"),
                        resultSet.getBoolean("is_active")
                );
                users.add(user);
            }

            if (users.isEmpty()) {
                System.out.println("⚠ Aucun guide trouvé dans la base de données.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des guides : " + ex.getMessage());
        }
        return users;
    }


    @Override
    public User getUserbyID(int id) throws UserNotFoundException {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE id = ? AND roles = ? LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, Type.GUIDE.name());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No GUIDE user found with ID: " + id);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving GUIDE user with ID " + id + ": " + ex.getMessage());
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
            statement.setString(2, Type.GUIDE.name());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No GUIDE user found with email: " + email);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving GUIDE user by email " + email + ": " + ex.getMessage());
        }
        return user;
    }


    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String firstname = resultSet.getString("firstname");
        String lastname = resultSet.getString("lastname");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        String password = resultSet.getString("password");// Ensure it's a String
        Boolean statusGuide = resultSet.getBoolean("statusGuide"); // Nullable
        String roleString = resultSet.getString("roles");

        Type roles = null;
        try {
            roles = Type.valueOf(roleString.toUpperCase()); // Ensure proper matching
        } catch (IllegalArgumentException ignored) {
            System.err.println("Invalid role type: " + roleString);
        }

        boolean is_banned = resultSet.getBoolean("is_banned");
        boolean is_active = resultSet.getBoolean("is_active");// Ensure the correct column index

        return new User(id, firstname, lastname, email, phone, password, statusGuide, roles, is_banned,is_active);
    }

}