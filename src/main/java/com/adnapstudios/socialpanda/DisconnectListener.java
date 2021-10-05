package com.adnapstudios.socialpanda;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.SQLException;

public class DisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDisconnect(final PlayerDisconnectEvent event) {
        try {
            SocialPanda.getDatabaseManager().updateLastOnline(event.getPlayer().getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
