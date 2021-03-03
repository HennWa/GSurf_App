package com.example.gsurfexample.source.local.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "processed_data_table")
public class ProcessedData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int session_id;
    private long timeStamp;
    private float ddX, ddY, ddZ;
    private float dX, dY, dZ;
    private float X, Y, Z;
    private float wx, wy, wz;
    private float q0, q1, q2, q3; // quaternions
    private float lat, lon;
    private int state;


    public ProcessedData(int session_id, long timeStamp,
                         float ddX, float ddY, float ddZ,
                         float dX, float dY, float dZ,
                         float X, float Y, float Z,
                         float wx, float wy, float wz,
                         float q0, float q1, float q2, float q3,
                         float lat, float lon, int state) {


        this.session_id = session_id;
        this.timeStamp = timeStamp;         // in milliseconds
        this.ddX = ddX;
        this.ddY = ddY;
        this.ddZ = ddZ;
        this.dX = dX;
        this.dY = dY;
        this.dZ = dZ;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.wx = wx;
        this.wy = wy;
        this.wz = wz;
        this.q0 = q0;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.lat = lat;
        this.lon = lon;
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSession_id() {
        return session_id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getDdX() {
        return ddX;
    }

    public float getDdY() {
        return ddY;
    }

    public float getDdZ() {
        return ddZ;
    }

    public float getDX() {
        return dX;
    }

    public float getDY() {
        return dY;
    }

    public float getDZ() {
        return dZ;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getZ() {
        return Z;
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

    public float getQ0() {
        return q0;
    }

    public float getQ1() {
        return q1;
    }

    public float getQ2() {
        return q2;
    }

    public float getQ3() {
        return q3;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public int getState() { return state; }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setDdX(float ddX) {
        this.ddX = ddX;
    }

    public void setDdY(float ddY) {
        this.ddY = ddY;
    }

    public void setDdZ(float ddZ) {
        this.ddZ = ddZ;
    }

    public void setdX(float dX) {
        this.dX = dX;
    }

    public void setdY(float dY) {
        this.dY = dY;
    }

    public void setdZ(float dZ) {
        this.dZ = dZ;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

    public void setZ(float z) {
        Z = z;
    }

    public void setWx(float wx) {
        this.wx = wx;
    }

    public void setWy(float wy) {
        this.wy = wy;
    }

    public void setWz(float wz) {
        this.wz = wz;
    }

    public void setQ0(float q0) {
        this.q0 = q0;
    }

    public void setQ1(float q1) {
        this.q1 = q1;
    }

    public void setQ2(float q2) {
        this.q2 = q2;
    }

    public void setQ3(float q3) {
        this.q3 = q3;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setState(int state) {
        this.state = state;
    }

}
