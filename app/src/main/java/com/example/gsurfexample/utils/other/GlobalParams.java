package com.example.gsurfexample.utils.other;

public class GlobalParams {

    // ResampleFilter
    private long sampleR = 100;


    // Getter
    public long getSampleR() {
        return sampleR;
    }

    private static GlobalParams instance;

    public static GlobalParams getInstance() {
        if (instance == null)
            instance = new GlobalParams();
        return instance;
    }

    private GlobalParams() { }
}