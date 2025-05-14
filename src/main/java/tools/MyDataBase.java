package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static final String URL = "jdbc:mysql://localhost:3309/integrationjavaweb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static MyDataBase instance;
    private Connection cnx;

    private MyDataBase() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() {
        // Check if connection is closed and reconnect if necessary
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Reconnected to database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnx;
    }

    // Method to close connection explicitly when the application shuts down
    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("Connection closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
