package com.social.messenger.dto;

import com.social.messenger.models.User;

import java.util.List;

public class GroupDTO{
    private String id;
    private String name;
    private List<User> userList;
    private String lastMessage;
    private long timestamp;
    private int unreadCount;

    public GroupDTO() {
    }

    public GroupDTO(String id, String name, List<User> userList, String lastMessage, long timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.userList = userList;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
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
