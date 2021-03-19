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
    private String description;
    private int priority;
    //private Bitmap image;


    public SurfSession(String sessionID, String title, String description, int priority) {
        this.sessionID = sessionID;
        this.title = title;
        this.description = description;
        this.priority = priority;
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

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    //public Bitmap getImage(){ return image; }

}
