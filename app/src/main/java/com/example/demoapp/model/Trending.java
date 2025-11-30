package com.example.demoapp.model;

public class Trending {
    private String nameSong;
    private String nameAuthor;
    private int imgSong;
    private int songResId;

    public Trending(String nameSong, String nameAuthor, int imgSong, int songResId) {
        this.nameSong = nameSong;
        this.nameAuthor = nameAuthor;
        this.imgSong = imgSong;
        this.songResId = songResId;
    }

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public int getImgSong() {
        return imgSong;
    }

    public void setImgSong(int imgSong) {
        this.imgSong = imgSong;
    }

    public int getSongResId() {
        return songResId;
    }

    public void setSongResId(int songResId) {
        this.songResId = songResId;
    }
}
