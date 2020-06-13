package com.broeders.festifyapp.models;

public class RoomItem {
    private int roomID;
    private int accountID;
    private String locatie;
    private String roomName;

    public RoomItem(int roomID, int accountID, String locatie, String roomName) {
        this.roomID = roomID;
        this.accountID = accountID;
        this.locatie = locatie;
        this.roomName = roomName;
    }

    public int getRoomID() {
        return this.roomID;
    }
    public int getaccountID() {
        return this.accountID;
    }
    public String getLocatie(){return this.locatie;}
    public  String getRoomName(){return this.roomName;}
}
