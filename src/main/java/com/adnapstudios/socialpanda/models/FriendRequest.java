package com.adnapstudios.socialpanda.models;

import com.adnapstudios.socialpanda.SocialPanda;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class FriendRequest {
    private SocialPlayer sender;
    private SocialPlayer receiver;
    private Timestamp date;

    public FriendRequest(SocialPlayer sender, SocialPlayer receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Timestamp(System.currentTimeMillis());
    }

    public FriendRequest(SocialPlayer sender, SocialPlayer receiver, Timestamp date) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public SocialPlayer getSender() {
        return sender;
    }

    public void setSender(SocialPlayer sender) {
        this.sender = sender;
    }

    public SocialPlayer getReceiver() {
        return receiver;
    }

    public void setReceiver(SocialPlayer receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void send() {
        ProxiedPlayer proxiedSender = SocialPanda.getInstance().getProxy().getPlayer(UUID.fromString(sender.getUuid()));
        ProxiedPlayer proxiedReceiver = SocialPanda.getInstance().getProxy().getPlayer(UUID.fromString(receiver.getUuid()));

        BaseComponent[] who = new ComponentBuilder(sender.getName() + " sent you a friend request.\n").color(ChatColor.BLUE).create();
        BaseComponent[] accept = new ComponentBuilder("ACCEPT").color(ChatColor.GREEN).bold(true).create();
        BaseComponent[] decline = new ComponentBuilder("DECLINE").color(ChatColor.RED).bold(true).create();

        accept[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                String.format("/friend accept %s", sender.getName())));

        decline[0].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                String.format("/friend decline %s", sender.getName())));

        proxiedReceiver.sendMessage(who[0], accept[0], new TextComponent(" | "), decline[0]);

        TextComponent requestSent = new TextComponent("Your friend request to " + proxiedReceiver.getDisplayName() + " has been sent.");
        proxiedSender.sendMessage(requestSent);
    }

    public void accept() {
        try {
            SocialPanda.getDatabaseManager().addFriends(this);

            ProxiedPlayer proxiedSender = SocialPanda.getInstance().getProxy().getPlayer(UUID.fromString(sender.getUuid()));
            ProxiedPlayer proxiedReceiver = SocialPanda.getInstance().getProxy().getPlayer(UUID.fromString(receiver.getUuid()));

            TextComponent senderMessage = new TextComponent(proxiedReceiver.getDisplayName() + " accepted your friend request!");
            TextComponent receiverMessage = new TextComponent("Friend request accepted!");

            proxiedSender.sendMessage(senderMessage);
            proxiedReceiver.sendMessage(receiverMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
