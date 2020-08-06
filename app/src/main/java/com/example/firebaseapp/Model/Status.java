package com.example.firebaseapp.Model;

public class Status {
    private String id;
    private String imageURL;

    public Status(String id, String imageURL) {
        this.id = id;
        this.imageURL = imageURL;
    }

    public Status() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
