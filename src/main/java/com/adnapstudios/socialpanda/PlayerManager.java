package com.adnapstudios.socialpanda;

import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerManager {
    private ArrayList<SocialPlayer> players;

    public PlayerManager() {
        players = new ArrayList<>();
    }

    public void playerOnline(String uuid) {
        SocialPlayer player = getPlayerByUUID(uuid);
        players.add(player);
    }

    public void playerOffline(String uuid) {
        players.removeIf(p -> p.getUuid().equalsIgnoreCase(uuid));
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
}
