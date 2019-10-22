package com.sakib.uconnect.model;

public class Chat {
    private String message;
    private String sender;
    private String receiver;
    private String createdAt;
    private boolean isSeen;

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Chat() {
    }

    public Chat(String message, String sender, String receiver, String createdAt, boolean isSeen) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
