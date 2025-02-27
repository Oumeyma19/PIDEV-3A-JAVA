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

public class UserService implements UserInterface {
    private Connection connection = MyConnection.getInstance().getConnection();
    ValidationService validationService = new ValidationService();
    private static UserService instance;

    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    private List<User> users = new ArrayList<>(); // Example user list

    // Other methods...

   // public int countAdmins() {
     //   return (int) users.stream().filter(user -> user.getRoles() == Type.ADMIN).count();
    //}

    public int countAdmins() {
        // Créer une liste vide pour stocker les administrateurs
        List<User> admins = new ArrayList<>();
        try {
            // Effectuer une requête SQL pour récupérer les utilisateurs ayant le rôle ADMIN
            String query = "SELECT * FROM user WHERE roles = 'ADMIN' AND is_active = true"; // Par exemple, si vous voulez uniquement les admins actifs
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Parcourir les résultats de la requête et les ajouter à la liste des administrateurs
            while (resultSet.next()) {
                User user = new User(
                    resultSet.getInt("id"),
                    resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("email"),
                    resultSet.getString("phone"),
                    resultSet.getString("password"),
                    Type.valueOf(resultSet.getString("roles")),  // On suppose que le rôle est correctement récupéré
                    resultSet.getBoolean("is_banned"),
                    resultSet.getBoolean("is_active")
                );
                admins.add(user);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Error retrieving admins from database: " + ex.getMessage());
        }

        // Retourner le nombre d'administrateurs
        return admins.size();
    }

    @Override
    public void addUser(User user) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException,
        IncorrectPasswordException, CustomIllegalStateException {
        // Check if an admin already exists
        if (user.getRoles() == Type.ADMIN && countAdmins() > 0) {
            throw new CustomIllegalStateException("Un administrateur existe déjà dans la BD.");
        }

        // Validate required fields
        if (user.getFirstname().isEmpty() || user.getLastname().isEmpty() || user.getEmail().isEmpty() ||
            user.getPassword().isEmpty()) {
            throw new EmptyFieldException("Please fill in all required fields.");
        }

        // Validate email format
        if (!validationService.isValidEmail(user.getEmail())) {
            throw new InvalidEmailException("Invalid email, please check your email address.");
        }

        if (isEmailExists(user.getEmail())) {
            throw new InvalidEmailException("Email already exists. Please use a different email.");
        }

        // Validate phone number format (if provided)
        if (!user.getPhone().isEmpty() && !validationService.isValidPhoneNumber(user.getPhone())) {
            throw new InvalidPhoneNumberException("Invalid phone number format.");
        }

        // Validate password format
        if (!validationService.isValidPassword(user.getPassword())) {
            throw new IncorrectPasswordException("Password must contain at least one uppercase letter, " +
                "one lowercase letter, one digit, and be at least 6 characters long.");
        }

        // SQL insertion query
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
            preparedStatement.setBoolean(7, true);  // is_active = true
            preparedStatement.setBoolean(8, false); // is_banned = false

            preparedStatement.executeUpdate();
            System.out.println("User added successfully!");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
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
        String request = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, encryptedPassword);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("Aucun utilisateur trouvé avec l'ID : " + userId);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour du mot de passe : " + ex.getMessage());
        }
    }

    public void updateBasicUserInfo(User user) throws EmptyFieldException, InvalidEmailException, InvalidPhoneNumberException {
        // Validation des champs
        if (user.getFirstname().isEmpty() || user.getLastname().isEmpty() || user.getEmail().isEmpty()) {
            throw new EmptyFieldException("Veuillez remplir tous les champs obligatoires.");
        }

        if (!validationService.isValidEmail(user.getEmail())) {
            throw new InvalidEmailException("L'adresse email est invalide.");
        }

        if (!user.getPhone().isEmpty() && !validationService.isValidPhoneNumber(user.getPhone())) {
            throw new InvalidPhoneNumberException("Le numéro de téléphone est invalide.");
        }

        // Requête SQL pour mettre à jour les informations
        String request = "UPDATE user SET firstname = ?, lastname = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setString(1, user.getFirstname());
            preparedStatement.setString(2, user.getLastname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPhone());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Informations mises à jour avec succès !");
            } else {
                System.out.println("Aucune modification effectuée.");
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour : " + ex.getMessage());
        }
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

    public User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstname(rs.getString("firstname"));
        user.setLastname(rs.getString("lastname"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setRoles(Type.valueOf(rs.getString("roles")));

        // Ensure the boolean value is correctly parsed
        String isActiveStr = rs.getString("is_active");
        boolean isActive = "1".equals(isActiveStr) || "true".equalsIgnoreCase(isActiveStr);
        user.setIsActive(isActive);

        return user;
    }
}
