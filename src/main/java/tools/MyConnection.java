package tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    public final String URL = "jdbc:mysql://localhost:3306/integ";
    public final String USER = "root";
    public final String PASSWORD = "";


    private Connection connection;
    private static MyConnection instance;

    public Connection getConnection() {
        return connection;
    }

    public MyConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }

        return instance;
    }
}