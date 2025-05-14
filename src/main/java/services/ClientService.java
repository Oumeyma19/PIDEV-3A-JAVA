package services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import exceptions.*;
import interfaces.UserInterface;
import models.User;
import tools.MyConnection;
import util.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientService implements UserInterface {
    private Connection connection = MyConnection.getInstance().getConnection();
    ValidationService validationService = new ValidationService();
    private static ClientService instance;

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public ClientService() {
    }

    public static ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
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

        // Requête SQL sans l'id (auto-incrémenté)
        String request = "INSERT INTO `user`(`firstname`, `lastname`, `email`, `phone`, `password`, `pointsfid`, `nivfid`, `roles`, `is_active`, `is_banned`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setString(5, cryptPassword(user.getPassword()));
            preparedStatement.setInt(6, user.getPointsfid());
            preparedStatement.setInt(7, user.getNivfid());
            preparedStatement.setString(8, user.getRoles().toString());
            preparedStatement.setBoolean(9, user.getIsActive());
            preparedStatement.setBoolean(10, user.getIsBanned());

            preparedStatement.executeUpdate();

            // Récupérer l'id généré automatiquement
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                user.setId(id); // Mettre à jour l'id dans l'objet User
            }

            System.out.println("Client added successfully !");
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
        String request = "UPDATE user SET password = ? WHERE id = ? AND roles = 'CLIENT'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, encryptedPassword);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("Aucun client trouvé avec l'ID : " + userId);
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
        String request = "UPDATE user SET firstname = ?, lastname = ?, email = ?, phone = ?, pointsfid = ?, nivfid = ?, is_banned = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setInt(5, user.getPointsfid());
            preparedStatement.setInt(6, user.getNivfid());
            preparedStatement.setBoolean(7, user.getIsBanned());
            preparedStatement.setBoolean(8, user.getIsActive());
            preparedStatement.setInt(9, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Client updated successfully!");
            } else {
                System.out.println("Failed to update client. Client not found or no changes made.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating client: " + ex.getMessage());
        }
    }

    public void updateBasicClientInfo(User user) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException {
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
                System.out.println("Client basic info updated successfully!");
            } else {
                System.out.println("Failed to update client basic info.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating client basic info: " + ex.getMessage());
        }
    }

    @Override
    public void deleteUser(int id) throws UserNotFoundException {
        User user = getUserbyID(id);
        String request = "DELETE FROM `user` WHERE `Id` =" + user.getId() + ";";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(request);
            System.out.println("Client is deleted successfully");

        } catch (SQLException ex) {
            System.err.println();
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            String request = "SELECT * FROM user WHERE roles = 'CLIENT'";
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
                    resultSet.getInt("pointsfid"),
                    resultSet.getInt("nivfid"),
                    Type.CLIENT,
                    resultSet.getBoolean("is_banned"),
                    resultSet.getBoolean("is_active")
                );
                users.add(user);
            }

            if (users.isEmpty()) {
                System.out.println("⚠ Aucun client trouvé dans la base de données.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des clients : " + ex.getMessage());
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
            preparedStatement.setString(2, Type.CLIENT.name());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
            } else {
                throw new UserNotFoundException("No CLIENT user found with ID: " + id);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving CLIENT user with ID " + id + ": " + ex.getMessage());
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
            statement.setString(2, Type.CLIENT.name());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
                setLoggedInUser(user); // Set the logged-in user
            } else {
                throw new UserNotFoundException("No CLIENT user found with email: " + email);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving CLIENT user by email " + email + ": " + ex.getMessage());
        }
        return user;
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String firstname = resultSet.getString(2);
        String lastname = resultSet.getString(3);
        String email = resultSet.getString(4);
        String phone = resultSet.getString(5);
        String password = resultSet.getString(6);
        int pointsfid = resultSet.getInt(7);
        int nivfid = resultSet.getInt(8);
        String roleString = resultSet.getString("roles");
        Type roles = null;
        try {
            roles = Type.valueOf(roleString);
        } catch (IllegalArgumentException ignored) {
        }
        boolean is_banned = resultSet.getBoolean(9);
        boolean is_active = resultSet.getBoolean(10);
        return new User(id, firstname, lastname, email, phone, password, pointsfid, nivfid, roles, is_banned, is_active);
    }

    public void banUser(int userId) throws PermissionException, UserNotFoundException {
        // Vérifier si l'utilisateur actuel a les permissions nécessaires
        if (SessionService.getInstance().getCurrentUser().getRoles() != Type.ADMIN) {
            throw new PermissionException("You don't have permission to ban a client.");
        }

        // Vérifier si l'utilisateur existe
        User user = getUserbyID(userId);
        if (user == null) {
            throw new UserNotFoundException("Client with ID " + userId + " not found.");
        }

        // Bannir l'utilisateur
        String query = "UPDATE user SET is_banned = true WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Client with ID " + userId + " has been banned.");
            } else {
                throw new UserNotFoundException("Failed to ban client with ID " + userId);
            }
        } catch (SQLException ex) {
            System.err.println("Error banning client: " + ex.getMessage());
        }
    }

    public void unbanUser(int userId) throws PermissionException, UserNotFoundException {
        // Vérifier si l'utilisateur actuel a les permissions nécessaires
        if (SessionService.getInstance().getCurrentUser().getRoles() != Type.ADMIN) {
            throw new PermissionException("You don't have permission to unban a client.");
        }

        // Vérifier si l'utilisateur existe
        User user = getUserbyID(userId);
        if (user == null) {
            throw new UserNotFoundException("Client with ID " + userId + " not found.");
        }

        // Débannir l'utilisateur
        String query = "UPDATE user SET is_banned = false WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Client with ID " + userId + " has been unbanned.");
            } else {
                throw new UserNotFoundException("Failed to unban client with ID " + userId);
            }
        } catch (SQLException ex) {
            System.err.println("Error unbanning client: " + ex.getMessage());
        }
    }

    public int getClientsCount() {
        String query = "SELECT COUNT(*) FROM user WHERE roles = 'CLIENT'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving clients count: " + ex.getMessage());
        }
        return 0;
    }

    public int getActiveClientsCount() {
        String query = "SELECT COUNT(*) FROM user WHERE roles = 'CLIENT' AND is_active = true";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving active clients count: " + ex.getMessage());
        }
        return 0;
    }

    public int getInactiveClientsCount() {
        String query = "SELECT COUNT(*) FROM user WHERE roles = 'CLIENT' AND is_active = false";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving inactive clients count: " + ex.getMessage());
        }
        return 0;
    }
}
