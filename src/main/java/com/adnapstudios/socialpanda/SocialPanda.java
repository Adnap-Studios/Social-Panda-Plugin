package com.adnapstudios.socialpanda;

import com.adnapstudios.socialpanda.commands.FriendCommand;
import com.adnapstudios.socialpanda.listeners.DisconnectListener;
import com.adnapstudios.socialpanda.listeners.LoginListener;
import com.adnapstudios.socialpanda.managers.DatabaseManager;
import com.adnapstudios.socialpanda.managers.FriendRequestManager;
import com.adnapstudios.socialpanda.managers.PlayerManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.sql.SQLException;

public class SocialPanda extends Plugin {
    public static Configuration configuration;
    private File file;
    private static DatabaseManager databaseManager;
    private static PlayerManager playerManager;
    private static FriendRequestManager friendRequestManager;
    private static SocialPanda instance;

    @Override
    public void onEnable() {
        try {
            instance = (this);
            createConfig();
            readConfig();
            databaseManager = new DatabaseManager();
            playerManager = new PlayerManager();
            friendRequestManager = new FriendRequestManager();
            friendRequestManager.loadFriendRequests();
            getLogger().info("[Social Panda Plugin] Database connection: " + databaseManager.isConnected());
            getProxy().getPluginManager().registerListener(this, new LoginListener());
            getProxy().getPluginManager().registerListener(this, new DisconnectListener());
            getProxy().getPluginManager().registerCommand(this, new FriendCommand());
            getLogger().info("[Social Panda Plugin] Plugin loaded!");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static SocialPanda getInstance() {
        return instance;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static FriendRequestManager getFriendRequestManager() { return friendRequestManager; }

    public void readConfig() throws IOException {
        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    public void saveConfig() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
    }

    private void createConfig() throws IOException {
        file = new File(getDataFolder(), "config.yml");

        if (!getDataFolder().exists()) {
            if (getDataFolder().mkdir()) {
                if (!file.exists()) {
                    if (file.createNewFile()) {
                        readConfig();
                        configuration.set("database.host", "localhost");
                        configuration.set("database.port", "3306");
                        configuration.set("database.username", "username");
                        configuration.set("database.password", "password");
                        saveConfig();
                    }
                }
            }

        }
    }
}
