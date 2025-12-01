package com.example.demoapp.model;

public class Playlist {
    private String name;
    private int imageResId;

    public Playlist(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
    public void setName(String name) {
        this.name = name;
    }
}

