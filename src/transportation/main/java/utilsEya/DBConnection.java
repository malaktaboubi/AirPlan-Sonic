package utilsEya;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/airplan";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // or "root" depending on your XAMPP configuration

    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Always try to get a new connection if the existing one is closed or null
            if (connection == null || connection.isClosed()) {
                try {
                    // Load the MySQL JDBC driver
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Connexion à la base réussie !");
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
                } catch (SQLException e) {
                    System.err.println("Erreur de connexion à la base: " + e.getMessage());
                    // Consider throwing a runtime exception here to fail fast
                    throw new RuntimeException("Failed to connect to database", e);
                }
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            throw new RuntimeException("Failed to check connection status", e);
        }
    }
}