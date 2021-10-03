package com.adnapstudios.socialpanda;

import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public DatabaseManager() throws SQLException {
        readConfig();
        connect();
    }

    public void readConfig() {
        Configuration config = SocialPanda.configuration;
        host = config.getString("database.host");
        port = String.valueOf(config.getInt("database.port"));
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");
    }

    public void connect() throws SQLException {
        if (!isConnected()) {
            String connectionString = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true", host, port, database);
            connection = DriverManager.getConnection(connectionString, username, password);
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }


}
