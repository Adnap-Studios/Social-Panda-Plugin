package com.adnapstudios.socialpanda;

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
