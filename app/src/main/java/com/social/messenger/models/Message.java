package com.social.messenger.models;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String text;
    private String fileUrl;
    private String type; // "image" hoặc "video"
    private long timestamp;
    private String status; // sử dụng enum dạng String

    // Constructors, Getters, Setters...

    public Message() {
    }

    public Message(String id, String senderId, String receiverId, String text, String fileUrl, String fileType, Long timestamp, String status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.fileUrl = fileUrl;
        this.type = fileType;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

