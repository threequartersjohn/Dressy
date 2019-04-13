package com.example.dressy.classes;

public class Photo {

    private String user_id, photo_url, type;

    public Photo() { }

    public Photo(String user_id, String photo_url, String type) {
        this.user_id = user_id;
        this.photo_url = photo_url;
        this.type = type;
    }
}
