package services;

import models.User;
import tools.MyDataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private final Connection conn;

    public UserService() {
        conn = MyDataBase.getInstance().getCnx();
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id"),
                    rs.getString("firstname"),
                    rs.getString("email")
            );
        }
        return null;
    }
}
