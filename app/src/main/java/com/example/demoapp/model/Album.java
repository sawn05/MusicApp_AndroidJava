package com.example.demoapp.model;

public class Album {
    private String nameAlbum;
    private int imageResId;

    public Album(String nameAlbum, int imageResId) {
        this.nameAlbum = nameAlbum;
        this.imageResId = imageResId;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public int getImageResId() {
        return imageResId;
    }
}
