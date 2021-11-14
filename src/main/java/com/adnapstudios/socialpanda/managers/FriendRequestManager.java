package com.adnapstudios.socialpanda.managers;

import com.adnapstudios.socialpanda.models.FriendRequest;
import com.adnapstudios.socialpanda.SocialPanda;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendRequestManager {
    private ArrayList<FriendRequest> friendRequests;

    public FriendRequestManager() {
        friendRequests = new ArrayList<>();
    }

    public void loadFriendRequests() {
        try {
            friendRequests = SocialPanda.getDatabaseManager().getAllFriendRequests();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FriendRequest> getFriendRequestsFromPlayer(String uuid) {
        ArrayList<FriendRequest> playerFriendRequest = new ArrayList<>();

        for (FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getReceiver().getUuid().equalsIgnoreCase(uuid) ||
                    friendRequest.getSender().getUuid().equalsIgnoreCase(uuid)) {
                playerFriendRequest.add(friendRequest);
            }
        }

        return playerFriendRequest;
    }

    public void add(FriendRequest friendRequest) {
        try {
            SocialPanda.getDatabaseManager().addFriendRequest(friendRequest.getSender().getUuid(), friendRequest.getReceiver().getUuid());
            friendRequests.add(friendRequest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void accept(FriendRequest friendRequest) {
        try {
            SocialPanda.getDatabaseManager().removeFriendRequest(friendRequest);
            friendRequests.removeIf(r ->
                    r.getSender().getUuid().equals(friendRequest.getSender().getUuid()) &&
                            r.getReceiver().getUuid().equals(friendRequest.getReceiver().getUuid()));
            friendRequest.accept();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decline(FriendRequest friendRequest) {
        try {
            SocialPanda.getDatabaseManager().removeFriendRequest(friendRequest);
            friendRequests.removeIf(r ->
                    r.getSender().getUuid().equals(friendRequest.getSender().getUuid()) &&
                            r.getReceiver().getUuid().equals(friendRequest.getReceiver().getUuid()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
