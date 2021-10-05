package com.adnapstudios.socialpanda;

import net.md_5.bungee.config.Configuration;

import java.sql.*;
import java.util.ArrayList;

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
            readConfig();
            String connectionString = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true", host, port, database);
            connection = DriverManager.getConnection(connectionString, username, password);
            createTables();
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

    public void createTables() {
        String[] tables = {
                "CREATE TABLE IF NOT EXISTS `socialpanda_users` (" +
                        "`uuid` VARCHAR(100) NOT NULL," +
                        "`name` VARCHAR(100) NOT NULL," +
                        "`friend_requests` BOOLEAN DEFAULT true," +
                        "`last_online` TIMESTAMP NOT NULL," +
                        "PRIMARY KEY (`uuid`)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS `socialpanda_friends` (" +
                        "`uuid1` VARCHAR(100) NOT NULL," +
                        "`uuid2` VARCHAR(100) NOT NULL," +
                        "PRIMARY KEY (`uuid1`,`uuid2`)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS `socialpanda_marriage` (" +
                        "`uuid1` VARCHAR(100) NOT NULL," +
                        "`uuid2` VARCHAR(100) NOT NULL," +
                        "`date` TIMESTAMP NOT NULL," +
                        "PRIMARY KEY (`uuid1`,`uuid2`)" +
                        ");",

                "CREATE TABLE `socialpanda_requests` (" +
                        "`id` INT NOT NULL AUTO_INCREMENT," +
                        "`sender` VARCHAR(100) NOT NULL," +
                        "`receiver` VARCHAR(100) NOT NULL," +
                        "`date` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP," +
                        "PRIMARY KEY (`id`)" +
                        ");"
        };

        try {
            for (String table : tables) {
                connect();
                Statement statement;
                statement = connection.createStatement();
                statement.executeUpdate(table);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addPlayer(String uuid, String name) throws SQLException {
        SocialPlayer dbPlayer = getPlayerByUuid(uuid);
        if (dbPlayer != null) return;

        String query = String.format("INSERT INTO `socialpanda_users` " +
                        "(`uuid`, `name`, `friend_requests`, `last_online`) " +
                        "VALUES ('%s', '%s', '%d', current_timestamp());",
                uuid,
                name,
                1);

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public ArrayList<SocialPlayer> getAllPlayers() throws SQLException {
        String query = "SELECT * FROM `socialpanda_users`";

        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(query);

        ArrayList<SocialPlayer> players = new ArrayList<>();

        while (results.next()) {
            SocialPlayer player = new SocialPlayer();
            player.setUuid(results.getString("uuid"));
            player.setName(results.getString("name"));
            player.setLastOnline(results.getTimestamp("last_online"));

            players.add(player);
        }

        return players;
    }

    public SocialPlayer getPlayerByUuid(String uuid) throws SQLException {
        ArrayList<SocialPlayer> players = getAllPlayers();

        if (players == null || players.size() == 0) return null;

        for (SocialPlayer player : players) {
            if (player.getUuid().equalsIgnoreCase(uuid)) {
                return player;
            }
        }

        return null;
    }

    public ArrayList<String> getListOfPlayers() throws SQLException {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<SocialPlayer> players = getAllPlayers();
        for (SocialPlayer player : players) {
            names.add(player.getName());
        }

        return names;
    }

    public void addFriendRequest(String uuid1, String uuid2) throws SQLException {
        String query = String.format("INSERT INTO `socialpanda_requests` (`id`, `sender`, `receiver`, `date`) " +
                        "VALUES (NULL, '%s', '%s', NULL)",
                uuid1,
                uuid2);

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void updateLastOnline(String uuid) throws SQLException {
        String query = String.format("UPDATE `socialpanda_users` " +
                        "SET `last_online` = CURRENT_TIME() " +
                        "WHERE `socialpanda_users`.`uuid` = '%s';",
                uuid);

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

}
