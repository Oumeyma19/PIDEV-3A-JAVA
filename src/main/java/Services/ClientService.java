package Services;

import Exceptions.*;
import Interfaces.UserInterface;
import Models.User;
import Tools.MyDataBase;
import Util.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientService implements UserInterface {
    private Connection connection = MyDataBase.getInstance().getCnx();

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
    public void addUser(User p) throws EmptyFieldException, InvalidEmailException, IncorrectPasswordException, InvalidPhoneNumberException, CustomIllegalStateException {

    }

    @Override
    public void updateUser(User p) throws EmptyFieldException, InvalidPhoneNumberException, InvalidEmailException, IncorrectPasswordException, UserNotFoundException {

    }

    @Override
    public void deleteUser(int id) throws UserNotFoundException {

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
                        resultSet.getString("nivfid"),
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
        String nivfid = resultSet.getString(8);
        String roleString = resultSet.getString("roles");
        Type roles = null;
        try {
            roles = Type.valueOf(roleString);
        } catch (IllegalArgumentException ignored) {
        }
        boolean is_banned = resultSet.getBoolean(9);
        boolean is_active = resultSet.getBoolean(10);
        return new User(id, firstname, lastname, email, phone, password, pointsfid, nivfid, roles, is_banned,is_active);
    }
}