package com.example.dressy.classes;

public class Photo {

    private String photo_url;
    private String type;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public Photo(){ }

    public Photo(String photo_url, String type) {
        this.photo_url = photo_url;
        this.type = type;
    }
}
