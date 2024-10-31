package org.example.field_localization_demonstration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Error: config.properties file not found.");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static final String URL = "jdbc:mysql://localhost:3306/localization";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
