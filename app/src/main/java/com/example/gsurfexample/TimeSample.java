package com.example.gsurfexample;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timesample_table")
public class TimeSample {

    @PrimaryKey(autoGenerate = true)
    private int id;
    long timeStamp;
    private double ddx, ddy, ddz;
    private double gFx, gFy, gFz;
    private double Bx, By, Bz;
    private double wx, wy, wz;
    private double lat, lon, height;
    private double xGPS, yGPS;


    public TimeSample(long timeStamp, double ddx, double ddy, double ddz,   // must be like that due to ROOM
                      double gFx, double gFy, double gFz,
                      double Bx, double By, double Bz,
                      double wx, double wy, double wz,
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

    public double getDdx() {
        return ddx; }

    public double getDdy() {
        return ddy;
    }

    public double getDdz() {
        return ddz;
    }

    public double getGFx() {
        return gFx;
    }

    public double getGFy() {
        return gFy;
    }

    public double getGFz() {
        return gFz;
    }

    public double getBx() {
        return Bx;
    }

    public double getBy() {
        return By;
    }

    public double getBz() {
        return Bz;
    }

    public double getWx() {
        return wx;
    }

    public double getWy() {
        return wy;
    }

    public double getWz() {
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
