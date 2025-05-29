package utils;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


public class MyDatabase {


    final String URL="jdbc:mysql://localhost:3306/travelagency?serverTimezone=UTC&jdbcCompliantTruncation=false";



    final String USERNAME="root";
    final String PASSWORD="";
    static Connection connection;

    static MyDatabase instance;

    private MyDatabase(){
        try {
            connection= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("MyDatabase: Connexion établie avec " + URL);
        } catch (SQLException e) {
            System.err.println("MyDatabase: ERREUR DE CONNEXION BD: " + e.getMessage());
            // Log the full stack trace for more details during debugging
            e.printStackTrace();
            // Throw a runtime exception to halt initialization if connection fails
            // This makes the problem immediately apparent instead of causing NullPointerExceptions later.
            throw new RuntimeException("Impossible d'établir la connexion à la base de données.", e);
        }
    }

    public static synchronized MyDatabase getInstance(){ // Added synchronized for thread safety in singleton
        if (instance==null){
            instance= new MyDatabase();
        }
        return instance;
    }


    public Connection getConnection() {

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return connection;
    }
}
