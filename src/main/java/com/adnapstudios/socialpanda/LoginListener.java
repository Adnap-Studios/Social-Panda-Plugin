package com.adnapstudios.socialpanda;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.SQLException;

public class LoginListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLogin(final PostLoginEvent event) {
        try {
            if (event.getPlayer().isConnected()) {
                ProxiedPlayer player = event.getPlayer();
                SocialPanda.getDatabaseManager().addPlayer(player.getUniqueId().toString(), player.getDisplayName());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
