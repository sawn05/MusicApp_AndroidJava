package com.example.demoapp.model;

public class Outstanding {
    private String namePlaylist;
    private int imgPlaylist;

    public Outstanding(String namePlaylist, int imgPlaylist) {
        this.namePlaylist = namePlaylist;
        this.imgPlaylist = imgPlaylist;
    }

    public String getNamePlaylist() {
        return namePlaylist;
    }

    public int getImgPlaylist() {
        return imgPlaylist;
    }
}
