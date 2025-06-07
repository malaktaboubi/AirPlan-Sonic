package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL = "jdbc:mysql://localhost:3306/transportation" ;
    private final String USER ="root";
    private final String PSW ="";
    private Connection connection;


    private static MyDataBase instance;

    private MyDataBase(){
        try {
            connection = DriverManager.getConnection(URL,USER,PSW);
            System.out.println("Connected!!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if(instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PSW);
                System.out.println("Reconnected to database!");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

}
