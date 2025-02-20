package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private static final String URL = "jdbc:mysql://localhost:3306/integ";
    private static final String USER = "root";
    private static final String PWD = "";
    private static MyDataBase instance;
    private Connection cnx;

    private MyDataBase() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            System.err.println("Failed to create connection: " + e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        return cnx; // Return the single persistent connection
    }
}