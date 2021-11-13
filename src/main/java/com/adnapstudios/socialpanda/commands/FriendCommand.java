package com.adnapstudios.socialpanda.commands;

import com.adnapstudios.socialpanda.SocialPanda;
import com.adnapstudios.socialpanda.models.FriendRequest;
import com.adnapstudios.socialpanda.models.SocialPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "socialpanda.user.friendrequest", "f");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            TextComponent wrong = new TextComponent("Usage: /friend add/remove [player], /friend accept/decline [player], /friend list");
            commandSender.sendMessage(wrong);
        } else {
            if (strings[0].equalsIgnoreCase("add")) {
                if (strings.length < 2) {
                    TextComponent wrong = new TextComponent("Usage: /friend add [player]");
                    commandSender.sendMessage(wrong);
                    return;
                }

                SocialPlayer sender = SocialPanda.getPlayerManager()
                        .getPlayerByUUID(((ProxiedPlayer) commandSender).getUniqueId().toString());

                ProxiedPlayer proxiedReceiver = SocialPanda.getInstance().getProxy().getPlayer(strings[1]);

                if (proxiedReceiver == null) {
                    TextComponent playerNotFound = new TextComponent("This player does not exist or is not online.");
                    commandSender.sendMessage(playerNotFound);
                    return;
                }

                SocialPlayer receiver = SocialPanda.getPlayerManager()
                        .getPlayerByUUID(proxiedReceiver.getUniqueId().toString());

                if (sender.getUuid().equals(receiver.getUuid())) {
                    TextComponent wrong = new TextComponent("You cannot send yourself a friend request.");
                    commandSender.sendMessage(wrong);
                    return;
                }

                try {
                    ArrayList<SocialPlayer> friends = SocialPanda.getDatabaseManager().getFriendsByUuid(sender.getUuid());
                    SocialPlayer friend = friends.stream().filter(f ->
                            f.getUuid().equalsIgnoreCase(receiver.getUuid())).findFirst().orElse(null);

                    if (friend != null) {
                        TextComponent alreadyFriends = new TextComponent("You are already friends with " + receiver.getName());
                        commandSender.sendMessage(alreadyFriends);
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                FriendRequest friendRequest = new FriendRequest(sender, receiver);
                friendRequest.send();

                SocialPanda.getFriendRequestManager().add(friendRequest);

                return;
            }

            if (strings[0].equalsIgnoreCase("remove")) {
                if (strings.length < 2) {
                    TextComponent wrong = new TextComponent("Usage: /friend remove [player]");
                    commandSender.sendMessage(wrong);
                    return;
                } else {
                    String uuid1 = ((ProxiedPlayer) commandSender).getUniqueId().toString();
                    SocialPlayer socialPlayer = SocialPanda.getPlayerManager().getPlayerByName(strings[1]);
                    String uuid2 = socialPlayer.getUuid();

                    SocialPanda.getPlayerManager().removeFriend(uuid1, uuid2);

                    TextComponent removedFriend = new TextComponent(String.format("You have removed %s as a friend.", socialPlayer.getName()));
                    commandSender.sendMessage(removedFriend);
                }

                return;
            }

            if (strings[0].equalsIgnoreCase("accept") || strings[0].equalsIgnoreCase("decline")) {
                if (strings.length < 2) {
                    TextComponent wrong = new TextComponent("Usage: /friend accept/decline [player]");
                    commandSender.sendMessage(wrong);
                    return;
                } else {
                    if (strings[0].equalsIgnoreCase("accept")) {
                        SocialPlayer receiver = SocialPanda.getPlayerManager()
                                .getPlayerByUUID(((ProxiedPlayer) commandSender).getUniqueId().toString());

                        ProxiedPlayer proxiedSender = SocialPanda.getInstance().getProxy().getPlayer(strings[1]);

                        SocialPlayer sender = SocialPanda.getPlayerManager()
                                .getPlayerByUUID(proxiedSender.getUniqueId().toString());

                        try {
                            FriendRequest friendRequest = SocialPanda.getDatabaseManager()
                                    .getFriendRequest(sender.getUuid(), receiver.getUuid());

                            if (friendRequest == null) {
                                friendRequestNotFound((ProxiedPlayer) commandSender);
                                return;
                            }

                            SocialPanda.getFriendRequestManager().accept(friendRequest);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (strings[0].equalsIgnoreCase("decline")) {
                        SocialPlayer sender = SocialPanda.getPlayerManager()
                                .getPlayerByUUID(((ProxiedPlayer) commandSender).getUniqueId().toString());

                        ProxiedPlayer proxiedReceiver = SocialPanda.getInstance().getProxy().getPlayer(strings[1]);

                        SocialPlayer receiver = SocialPanda.getPlayerManager()
                                .getPlayerByUUID(proxiedReceiver.getUniqueId().toString());

                        try {
                            FriendRequest friendRequest = SocialPanda.getDatabaseManager()
                                    .getFriendRequest(sender.getUuid(), receiver.getUuid());

                            if (friendRequest == null) {
                                friendRequestNotFound((ProxiedPlayer) commandSender);
                                return;
                            }

                            SocialPanda.getFriendRequestManager().decline(friendRequest);

                            proxiedReceiver.sendMessage(new TextComponent(String.format("You declined %s's friend request.",
                                    sender.getName())));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (strings[0].equalsIgnoreCase("list")) {
                // TODO
            }
        }
    }

    private void friendRequestNotFound(ProxiedPlayer player) {
        TextComponent textComponent = new TextComponent("This friend request does not exist.");
        textComponent.setColor(ChatColor.RED);
        textComponent.setBold(true);
        player.sendMessage(textComponent);
    }
}
