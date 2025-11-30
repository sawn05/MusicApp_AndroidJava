package com.example.demoapp.model;

public class RecentSong {
    private int imageSong;
    private String nameSong;
    private String artist;
    private String duration;
    private int songResId;

    public RecentSong(int imageSong, String nameSong, String artist, String duration, int songResId) {
        this.imageSong = imageSong;
        this.nameSong = nameSong;
        this.artist = artist;
        this.duration = duration;
        this.songResId = songResId;
    }
    public int getImageSong() {
        return imageSong;
    }
    public String getNameSong() {
        return nameSong;
    }
    public String getArtist() {
        return artist;
    }
    public String getDuration() {
        return duration;
    }

    public int getSongResId() { return songResId; }
}
