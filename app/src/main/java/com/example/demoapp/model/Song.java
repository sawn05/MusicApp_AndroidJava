package com.example.demoapp.model;

public class Song {
    private int id;
    private int imageSong;
    private String nameSong;
    private String artist;
    private String album;
    private String duration;
    private int resId;
    private boolean favorite;

    public Song(int id, int imageSong, String nameSong, String artist, String album, String duration, int resId, boolean favorite) {
        this.id = id;
        this.imageSong = imageSong;
        this.nameSong = nameSong;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.resId = resId;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageSong() {
        return imageSong;
    }

    public void setImageSong(int imageSong) {
        this.imageSong = imageSong;
    }

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
