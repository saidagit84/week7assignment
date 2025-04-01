package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
    
   
    private static final String HOST = "localhost"; 
    private static final String PASSWORD = "Saida@2011!"; 
    private static final String PORT = "3306"; 
    private static final String SCHEMA = "Project"; 
    private static final String USER = "Project_user"; 
    

    
    public static Connection getConnection() {
        String uri = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s" , HOST , PORT, SCHEMA,USER, PASSWORD);

        try {
            Connection connection = DriverManager.getConnection(uri);
            System.out.println("Connection to the database was successful!");
            return connection;
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            throw new DbException("Database connection error", e);
        }
    }
}
