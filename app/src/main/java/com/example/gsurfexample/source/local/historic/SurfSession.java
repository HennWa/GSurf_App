package com.example.gsurfexample.source.local.historic;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_table")
public class SurfSession {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String sessionID;
    private String title;
    private String location;
    private String date;
    //private Bitmap image;


    public SurfSession(String sessionID, String title, String location, String date) {
        this.sessionID = sessionID;
        this.title = title;
        this.location = location;
        this.date = date;
    }

    public void setSessionID(String sessionID) { this.sessionID = sessionID; }

    public String getSessionID() { return sessionID; }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    //public Bitmap getImage(){ return image; }

}
