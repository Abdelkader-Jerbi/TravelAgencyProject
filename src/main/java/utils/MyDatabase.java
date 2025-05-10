package utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database utility class that provides access to database connections.
 * This class uses a connection pool to improve performance.
 */
public class MyDatabase {
    private static MyDatabase instance;
    private final DatabaseConnectionPool connectionPool;

    /**
     * Private constructor to initialize the database connection
     */
    private MyDatabase() {
        connectionPool = DatabaseConnectionPool.getInstance();
    }

    /**
     * Get the singleton instance of the database utility
     * 
     * @return The database utility instance
     */
    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    /**
     * Get a database connection from the pool
     * 
     * @return A database connection
     */
    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            throw new RuntimeException("Failed to get database connection", e);
        }
    }
    
    /**
     * Release a connection back to the pool
     * 
     * @param connection The connection to release
     */
    public void releaseConnection(Connection connection) {
        connectionPool.releaseConnection(connection);
    }
}
