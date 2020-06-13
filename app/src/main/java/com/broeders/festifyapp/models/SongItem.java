package com.broeders.festifyapp.models;

public class SongItem {
    private int songID;
    private String songTitle;
    private String songArtist;

    public SongItem(int songID, String songTitle, String songArtist) {
     this.songID = songID;
     this.songTitle = songTitle;
     this.songArtist = songArtist;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }
}
