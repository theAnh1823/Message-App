package com.example.messageapplication.model;

import android.os.Parcelable;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private String id;
    private String imageResource;
    private String userName;
    private String lastMessage;
    private String messageTime;

    public UserMessage(String id, String imageResource, String userName, String lastMessage, String messageTime) {
        this.id = id;
        this.imageResource = imageResource;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
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
