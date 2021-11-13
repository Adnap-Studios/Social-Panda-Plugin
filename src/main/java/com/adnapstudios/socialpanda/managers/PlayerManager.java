package com.adnapstudios.socialpanda.managers;

import com.adnapstudios.socialpanda.SocialPanda;
import com.adnapstudios.socialpanda.models.SocialPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerManager {
    private ArrayList<SocialPlayer> players;

    public PlayerManager() {
        players = new ArrayList<>();
    }

    public void playerOnline(String uuid) {
        SocialPlayer player = getPlayerByUUID(uuid);
        players.add(player);
        notifyFriends(player, true);
    }

    public void playerOffline(String uuid) {
        SocialPlayer player = getPlayerByUUID(uuid);
        players.removeIf(p -> p.getUuid().equalsIgnoreCase(uuid));
        notifyFriends(player, false);
    }

    public ArrayList<SocialPlayer> getOnlinePlayers() {
        return players;
    }

    public SocialPlayer getPlayerByUUID(String uuid) {
        try {
            return SocialPanda.getDatabaseManager().getPlayerByUuid(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void notifyFriends(SocialPlayer player, boolean online) {
        try {
            ArrayList<SocialPlayer> friends = SocialPanda.getDatabaseManager().getFriendsByUuid(player.getUuid());

            TextComponent message;
            TextComponent symbol;

            if (online) {
                message = new TextComponent(String.format("Your friend %s has entered the network.", player.getName()));
                symbol = new TextComponent("+ ");
                symbol.setColor(ChatColor.GREEN);
            } else {
                message = new TextComponent(String.format("Your friend %s has left the network.", player.getName()));
                symbol = new TextComponent("- ");
                symbol.setColor(ChatColor.RED);
            }

            symbol.setBold(true);

            SocialPanda.getInstance().getLogger().info("Friends (" +  friends.size() + ") of " + player.getName());

            for (SocialPlayer friend : friends) {
                ProxiedPlayer proxiedFriend = SocialPanda.getInstance().getProxy().getPlayer(UUID.fromString(friend.getUuid()));
                if (proxiedFriend != null && proxiedFriend.isConnected()) {
                    SocialPanda.getInstance().getLogger().info("Sending message to " + proxiedFriend.getName());
                    proxiedFriend.sendMessage(symbol, message);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFriend(String uuid1, String uuid2) {
        try {
            SocialPanda.getDatabaseManager().removeFriends(uuid1, uuid2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SocialPlayer getPlayerByName(String name) {
        try {
            ArrayList<SocialPlayer> players = SocialPanda.getDatabaseManager().getAllPlayers();
            return players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<SocialPlayer> getPlayerFriendsByUUID(String uuid) {
        try {
            return SocialPanda.getDatabaseManager().getFriendsByUuid(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
