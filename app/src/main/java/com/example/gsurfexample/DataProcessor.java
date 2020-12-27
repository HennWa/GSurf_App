package com.example.gsurfexample;

import java.util.List;

public class DataProcessor {

    private final float SAMPLEPERIOD;               // sample period
    private final float CUTOFF;                     // Cutoff frequency of Butterworth filter
    private final float BETAMADGWICK;               // Parameter of Madgwick algorithm

    // Constructor
    public DataProcessor(float samplePeriod, float cutoff, float betaMadgwick) {
        SAMPLEPERIOD = samplePeriod;
        CUTOFF = cutoff;
        BETAMADGWICK = betaMadgwick;
    }

    public ProcessedData transferData(TimeSample timeSamples){
        int id = timeSamples.getId();
        ProcessedData processedData = new ProcessedData(id,
                233, 0f, 0.1f, 0.1f,
                0.1f,0.1f, 0.1f, 0.1f, 0.1f,
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f,
                0.1f, 0.1f, 0.1f, 0.1f, 0.1f);

        return processedData;
    }

}
