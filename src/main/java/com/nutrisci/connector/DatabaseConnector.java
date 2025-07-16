package com.nutrisci.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnector {
    // Define the name of the properties file containing database connection details.
    private static final String PROPS_FILE = "/db.properties";

    /**
     * Establishes and returns a database connection.
     * It reads database credentials from a properties file.
     *
     * @return A Connection object to the database.
     * @throws SQLException If a database access error occurs or the properties file cannot be loaded.
     */
    public static Connection getConnection() throws SQLException {
        // Use try-with-resources to ensure the InputStream is closed automatically.
        try (InputStream in = DatabaseConnector.class.getResourceAsStream(PROPS_FILE)) {
            // Create a Properties object to load key-value pairs from the properties file.
            Properties props = new Properties();
            // Load the properties from the InputStream.
            props.load(in);

            // Retrieve database connection details from the loaded properties.
            String url  = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");
            // Establish and return the database connection using DriverManager.
            return DriverManager.getConnection(url, user, pass);
        } catch (IOException e) {
            // If there's an issue loading the properties file, wrap it in an SQLException and rethrow.
            throw new SQLException("Unable to load DB properties", e);
        }
    }
}
