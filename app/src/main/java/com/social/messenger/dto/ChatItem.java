package com.social.messenger.dto;

import com.social.messenger.models.User;

public class ChatItem {
    private String id;
    private User user;
    private String lastMessage;
    private long timestamp;
    private int unreadCount;


    public ChatItem() {}

    public ChatItem(String id, User user, String lastMessage, long timestamp, int unreadCount) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
