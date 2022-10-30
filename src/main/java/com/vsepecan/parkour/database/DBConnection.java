package com.vsepecan.parkour.database;

import com.vsepecan.parkour.config.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final Connection connection;

    public DBConnection() {
        try {
            String HOST = ConfigManager.getHost();
            int PORT = ConfigManager.getPort();
            String DATABASE = ConfigManager.getDatabase();
            String USER = ConfigManager.getUser();
            String PASSWORD = ConfigManager.getPassword();

            connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
