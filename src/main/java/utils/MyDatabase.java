package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//for push
public class MyDatabase {

    private final String URL = "jdbc:mysql://localhost:3306/travelagency?serverTimezone=UTC&jdbcCompliantTruncation=false";

    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private static MyDatabase instance;

    private MyDatabase() {
        // Initialisation sans connexion
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            throw e;
        }
    }
}
