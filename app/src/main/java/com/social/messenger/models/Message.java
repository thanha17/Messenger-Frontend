package com.social.messenger.models;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    private boolean sentByUser;

    public Message(String id, String senderId, String receiverId, String content, long timestamp, boolean sentByUser) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.sentByUser = sentByUser;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSentByUser() {
        return sentByUser;
    }
}
