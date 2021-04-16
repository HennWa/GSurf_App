package com.example.gsurfexample.ui;

public class PolylineIdentifier {
    public int type;
    public int waveIdentifier;
    public int startIndex;
    public int endIndex;

    public PolylineIdentifier(int type, int waveIdentifier, int startIndex, int endIndex) {
        this.type = type;
        this.waveIdentifier = waveIdentifier;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getType() {
        return type;
    }

    public int getWaveIdentifier() {
        return waveIdentifier;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setWaveIdentifier(int waveIdentifier) {
        this.waveIdentifier = waveIdentifier;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

}
