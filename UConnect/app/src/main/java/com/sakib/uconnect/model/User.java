package com.sakib.uconnect.model;

public class User {
    private String id;
    private String name;
    private String imageUrl;
    private String mobileNumber;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User() {
    }

    public User(String id, String name, String imageUrl, String mobileNumber, String status) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.mobileNumber = mobileNumber;
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
