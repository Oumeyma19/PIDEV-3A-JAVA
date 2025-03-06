package services;

import exceptions.*;
import models.User;
import tools.MyConnection;
import util.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SessionService {
    Connection connection = MyConnection.getInstance().getConnection();
    private Map<String, Integer> loginAttempts = new HashMap<>();
    private Map<String, Boolean> accountLockStatus = new HashMap<>();
    public final int MAX_LOGIN_ATTEMPTS = 3;

    private static SessionService instance;
    public static User currentUser;


    //Singleton: private constructor to prevent instantiation
    private SessionService() {
    }

    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Set the current logged-in user
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public boolean login(String email, String password) throws EmptyFieldException, InvalidEmailException,
        IncorrectPasswordException, UserNotFoundException, AccountLockedException {

        UserService userService = UserService.getInstance();
        ValidationService validationService = new ValidationService();

        if (email.isEmpty() || password.isEmpty()) {
            throw new EmptyFieldException("Please enter your email and your password.");
        }
        if (!validationService.isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email, please check your email address.");
        }
        if (isAccountLocked(email)) {
            throw new AccountLockedException("Account is locked. Please reset your password to unlock it.");
        }

        User user = userService.getUserbyEmail(email);
        if (user != null) {
            if (userService.verifyPassword(password, user.getPassword())) {
                currentUser = user;
                return true;
            } else {
                throw new IncorrectPasswordException("Password is incorrect.");
            }
        } else {
            throw new UserNotFoundException("User with email " + email + " does not exist, please check your email or" +
                " create an account!");
        }
    }

    public void logout() {
        currentUser = null;
    }

    public boolean attempts(String email, String password) throws AccountLockedException {
        try {
            if (login(email, password)) {
                // Reset login attempts if login is successful
                loginAttempts.put(email, 0);
                System.out.println("You are Logged in !");
                return true;
            } else {
                // Increment login attempts if login fails
                int attempts = loginAttempts.getOrDefault(email, 0) + 1;
                loginAttempts.put(email, attempts);

                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    // Lock the account
                    lockAccount(email);
                    System.out.println("Too many incorrect attempts. Account locked." +
                        " Please contact the admin to unlock your account.");
                    throw new AccountLockedException("Too many incorrect attempts. Account locked.");
                } else {
                    System.out.println("Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));
                    throw new IncorrectPasswordException("Password is incorrect. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts));
                }
            }
        } catch (EmptyFieldException | InvalidEmailException | IncorrectPasswordException | UserNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }




    private void updateStatus(String email, boolean isActive, boolean isBanned) {
        String sql = "SELECT roles FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("roles");
                // Si l'utilisateur est un admin, ne pas le bannir
                if (!Type.ADMIN.name().equals(role)) {
                    String updateSql = "UPDATE user SET is_active = ?, is_banned = ? WHERE email = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                        updateStatement.setBoolean(1, isActive);
                        updateStatement.setBoolean(2, isBanned);
                        updateStatement.setString(3, email);
                        updateStatement.executeUpdate();
                    }
                } else {
                    System.out.println("Admin accounts cannot be banned.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lockAccount(String email) {
        String sql = "SELECT roles FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("roles");
                // Si l'utilisateur est un admin, ne pas le bannir
                if (!Type.ADMIN.name().equals(role)) {
                    updateStatus(email, false, true); // is_active = false, is_banned = true
                    accountLockStatus.put(email, false); // Mettre à jour le statut local
                } else {
                    System.out.println("Admin accounts cannot be locked.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unlockAccount(String email) {
        updateStatus(email, true,false); // Set is_active to true when unlocking the account
        accountLockStatus.put(email, true);
        loginAttempts.put(email, 0); // Reset login attempts
        System.out.println("Congratulations, your account has been unlocked !");
    }

    public boolean isAccountLocked(String email) {
        String sql = "SELECT is_banned, roles FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String role = resultSet.getString("roles");
                // Si l'utilisateur est un admin, ignorer le bannissement
                if (Type.ADMIN.name().equals(role)) {
                    return false; // Les admins ne peuvent jamais être bannis
                }
                // Pour les clients et guides, vérifier si le compte est banni
                return resultSet.getBoolean("is_banned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Par défaut, le compte n'est pas banni
    }
}
