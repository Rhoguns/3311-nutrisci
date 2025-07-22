/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.connector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    private static final String PROPS_FILE = "/db.properties";

    public static Connection getConnection() throws SQLException {
        try {
            Throwable throwable = null;
            Object var1_3 = null;
            try (InputStream in = DatabaseConnector.class.getResourceAsStream(PROPS_FILE);){
                if (in == null) {
                    throw new SQLException("Database properties file not found: /db.properties");
                }
                Properties props = new Properties();
                props.load(in);
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String pass = props.getProperty("db.password");
                return DriverManager.getConnection(url, user, pass);
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException e) {
            throw new SQLException("Unable to load DB properties", e);
        }
    }
}
