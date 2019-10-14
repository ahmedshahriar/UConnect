package com.sakib.uconnect.model;

public class User {
    private String id;
    private String name;
    private String imageUrl;

    public User() {
    }

    public User(String id, String userName, String imageUrl) {
        this.id = id;
        this.name = userName;
        this.imageUrl = imageUrl;
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
