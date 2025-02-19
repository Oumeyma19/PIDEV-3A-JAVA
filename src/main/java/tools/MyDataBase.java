package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    public final String URL = "jdbc:mysql://localhost:3306/integ";
    public final String USER = "root";
    public final String PWD = "";
    private static MyDataBase instance;

    private MyDataBase() {
        // No need to initialize the connection here
    }

    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getCnx() {
        Connection cnx = null;
        try {
            cnx = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            System.err.println("Failed to create connection: " + e.getMessage());
        }
        return cnx; // Return a new connection each time
    }
}
