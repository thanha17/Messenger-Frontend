package com.social.messenger.models;

public class FriendRequest {
    private String type;
    private String fromUserId;
    private String toUserId;
    private String message;

    // Getters & setters

    public FriendRequest(String type, String fromUserId, String toUserId, String message) {
        this.type = type;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
