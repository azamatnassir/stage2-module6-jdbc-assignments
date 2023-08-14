package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
    private Connection connection;
    public Connection getConnection(String url) {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return connection;
    }

    public Connection getConnection(String url, String user, String password)  {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return connection;
    }
}
