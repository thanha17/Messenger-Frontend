package com.social.messenger.models;

public class GroupMessage {
    private String id;
    private String groupId;
    private String senderId;
    private String text;
    private String fileUrl;
    private String type; // "image" hoặc "video"
    private long timestamp;

    // Constructors, Getters, Setters...
    public GroupMessage() {
    }

    public GroupMessage(String id, String groupId, String senderId, String text, String fileUrl, String mediaType, long timestamp) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.text = text;
        this.fileUrl = fileUrl;
        this.type = mediaType;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
