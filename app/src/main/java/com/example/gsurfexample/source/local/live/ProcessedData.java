package com.example.gsurfexample.source.local.live;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.gsurfexample.source.local.historic.ProcessedDataHistoric;

import java.util.Comparator;

@Entity(tableName = "processed_data_table")
public class ProcessedData {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String session_id;
    public long timeStamp;
    public float ddX, ddY, ddZ;
    public double dX, dY, dZ;
    public double X, Y, Z;
    public double dXFilt, dYFilt;
    public float wx, wy, wz;
    public float q0, q1, q2, q3; // quaternions
    public double lat, lon;
    public int state;

    public ProcessedData(String session_id, long timeStamp,
                         float ddX, float ddY, float ddZ,
                         double dX, double dY, double dZ,
                         double X, double Y, double Z,
                         double dXFilt, double dYFilt,
                         float wx, float wy, float wz,
                         float q0, float q1, float q2, float q3,
                         double lat, double lon, int state) {


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
        this.dXFilt = dXFilt;
        this.dYFilt = dYFilt;
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

/*
    public ProcessedDataHistoric newProcessedDataHistoric(){
        return new ProcessedDataHistoric(session_id, timeStamp,
                ddX, ddY, ddZ,
                dX, dY, dZ,
                X, Y, Z,
                dXFilt, dYFilt,
                wx, wy, wz,
                q0, q1, q2, q3,
                lat, lon, state);
    } */

    public int getId() { return id; }

    public String getSession_id() {
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

    public double getDX() {
        return dX;
    }

    public double getDY() {
        return dY;
    }

    public double getDZ() {
        return dZ;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    public double getDXFilt() { return dXFilt; }

    public double getDYFilt() { return dYFilt; }

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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getState() { return state; }

    public void setId(int id) { this.id = id; }

    public void setSession_id(String session_id) {
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

    public void setdX(double dX) { this.dX = dX; }

    public void setdY(double dY) {
        this.dY = dY;
    }

    public void setdZ(double dZ) {
        this.dZ = dZ;
    }

    public void setDXFilt(double dXFilt) { this.dXFilt = dXFilt; }

    public void setDYFilt(double dYFilt) { this.dYFilt = dYFilt; }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public void setZ(double z) {
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

    /*Comparator for sorting the list by roll no*/
    public static Comparator<ProcessedData> xSort = new Comparator<ProcessedData>() {

        public int compare(ProcessedData pd1, ProcessedData pd2) {

            double pd1X = pd1.getX();
            double pd2X = pd2.getX();

            /*For ascending order*/
            return (int) (pd1X - pd2X);
        }
    };

}
