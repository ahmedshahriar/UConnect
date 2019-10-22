package com.sakib.uconnect.model;

public class User {
    private String id;
    private String name;
    private String imageUrl;
    private String mobileNumber;
    private String status;
    private String lastSeenDate;
    private String lastSeenTime;

    public String getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(String lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public String getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(String lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public User() {
    }
    public User(String id, String name, String imageUrl, String mobileNumber, String status, String lastSeenDate, String lastSeenTime) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.mobileNumber = mobileNumber;
        this.status = status;
        this.lastSeenDate = lastSeenDate;
        this.lastSeenTime = lastSeenTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
