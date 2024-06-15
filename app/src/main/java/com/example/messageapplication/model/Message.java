package com.example.messageapplication.model;

public class Message {
    private String senderId;
    private String messageContent;
    private String timestamp;

    public Message(String senderId, String messageContent, String timestamp) {
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
