package com.adnapstudios.socialpanda;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class SocialPanda extends Plugin {
    public static Configuration configuration;
    private File file;

    @Override
    public void onEnable() {
        try {
            createConfig();
            readConfig();
            getLogger().info("[Social Panda Plugin] Plugin loaded!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
