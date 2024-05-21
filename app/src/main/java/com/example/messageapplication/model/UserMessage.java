package com.example.messageapplication.model;

public class UserMessage {
    private int id;
    private int imageResource;
    private String userName;
    private String lastMessage;
    private String messageTime;

    public UserMessage(int id, int imageResource, String userName, String lastMessage, String messageTime) {
        this.id = id;
        this.imageResource = imageResource;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
