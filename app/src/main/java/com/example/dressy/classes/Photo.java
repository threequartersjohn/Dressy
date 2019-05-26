package com.example.dressy.classes;

import java.util.List;

public class Photo {

    private String photo_url;
    private List<String> labels;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Photo() {
    }

    public Photo(String photo_url, List<String> labels) {
        this.photo_url = photo_url;
        this.labels = labels;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
