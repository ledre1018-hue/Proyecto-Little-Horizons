package com.littlehorizons.erp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton that reads the database settings once and hands out new connections.
 * Each DAO closes the connection it receives with try-with-resources.
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        Properties properties = loadProperties();
        this.url = properties.getProperty("db.url");
        this.user = properties.getProperty("db.user");
        this.password = properties.getProperty("db.password");
        if (url == null || user == null || password == null) {
            throw new DataAccessException("El archivo " + CONFIG_FILE
                    + " debe definir db.url, db.user y db.password.");
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new DataAccessException("No se encontró " + CONFIG_FILE
                        + ". Copie config.example.properties y configure sus credenciales.");
            }
            properties.load(input);
        } catch (IOException e) {
            AppLogger.getInstance().error("DatabaseConnection.loadProperties", "Cannot read " + CONFIG_FILE, e);
            throw new DataAccessException("No fue posible leer el archivo de configuración.", e);
        }
        return properties;
    }
}
