package com.adnapstudios.socialpanda.listeners;

import com.adnapstudios.socialpanda.SocialPanda;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.SQLException;

public class DisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(final PlayerDisconnectEvent event) {
        try {
            ProxiedPlayer player = event.getPlayer();
            SocialPanda.getDatabaseManager().updateLastOnline(player.getUniqueId().toString());
            SocialPanda.getPlayerManager().playerOffline(player.getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
