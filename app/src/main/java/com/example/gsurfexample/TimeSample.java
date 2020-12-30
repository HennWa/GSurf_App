package com.example.gsurfexample;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timesample_table")
public class TimeSample {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long timeStamp;
    private float ddx, ddy, ddz;
    private float gFx, gFy, gFz;
    private float Bx, By, Bz;
    private float wx, wy, wz;
    private double lat, lon, height;
    private double xGPS, yGPS;


    public TimeSample(long timeStamp, float ddx, float ddy, float ddz,   // must be like that due to ROOM
                      float gFx, float gFy, float gFz,
                      float Bx, float By, float Bz,
                      float wx, float wy, float wz,
                      double lat, double lon, double height,
                      double xGPS, double yGPS) {


        this.timeStamp = timeStamp;         // in milliseconds
        this.ddx = ddx;
        this.ddy = ddy;
        this.ddz = ddz;
        this.gFx = gFx;
        this.gFy = gFy;
        this.gFz = gFz;
        this.Bx = Bx;
        this.By = By;
        this.Bz = Bz;
        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
        this.lat = 0;
        this.lon = 0;
        this.height = 0;
        this.xGPS = 0;
        this.yGPS = 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getTimeStamp() { return timeStamp; }

    public float getDdx() {
        return ddx; }

    public float getDdy() {
        return ddy;
    }

    public float getDdz() {
        return ddz;
    }

    public float getGFx() {
        return gFx;
    }

    public float getGFy() {
        return gFy;
    }

    public float getGFz() {
        return gFz;
    }

    public float getBx() {
        return Bx;
    }

    public float getBy() {
        return By;
    }

    public float getBz() {
        return Bz;
    }

    public float getWx() {
        return wx;
    }

    public float getWy() {
        return wy;
    }

    public float getWz() {
        return wz;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getHeight() {
        return height;
    }

    public double getXGPS() {
        return xGPS;
    }

    public double getYGPS() {
        return yGPS;
    }

}
