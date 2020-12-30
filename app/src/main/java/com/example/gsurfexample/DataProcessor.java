package com.example.gsurfexample;


import java.lang.reflect.Array;

public class DataProcessor {

    private final float SAMPLEPERIOD;               // Sample period
    private final float CUTOFF;                     // Cutoff frequency of Butterworth filter
    private final float BETAMADGWICK;               // Parameter of Madgwick algorithm

    private MadgwickAHRS madgwickAHRS;              // Sensor fusion algorithm

    // Constructor
    public DataProcessor(float samplePeriod, float cutoff, float betaMadgwick) {
        SAMPLEPERIOD = samplePeriod;
        CUTOFF = cutoff;
        BETAMADGWICK = betaMadgwick;

        madgwickAHRS = new MadgwickAHRS(SAMPLEPERIOD, BETAMADGWICK);
    }

    // Methods
    public ProcessedData transferData(TimeSample timeSample){
        int id = timeSample.getId();

        // Madgwick algorithm to calculate quaternion
        madgwickAHRS.update(timeSample.getWx(), timeSample.getWy(), timeSample.getWz(),
                timeSample.getDdx(), timeSample.getDdy(), timeSample.getDdz(),
                timeSample.getBx(), timeSample.getBy(), timeSample.getBz());

        // Calculate Global Acceleration
        Quaternion quaternion = new Quaternion(Array.getFloat(madgwickAHRS.getQuaternion(), 1),
                Array.getFloat(madgwickAHRS.getQuaternion(), 2),
                Array.getFloat(madgwickAHRS.getQuaternion(), 3),
                Array.getFloat(madgwickAHRS.getQuaternion(), 0)); // Note different index convention

        float[] globalAccelerations = quaternion.rotateVector(new float[] {timeSample.getDdx(),
                timeSample.getDdy(), timeSample.getDdz()});


        // Store in object
        ProcessedData processedData = new ProcessedData(id,
                233, globalAccelerations[0], globalAccelerations[1], globalAccelerations[2],
                0.1f,0.1f, 0.1f, 0.1f, 0.1f,
                0.1f, 0.1f, 0.1f, 0.1f,
                quaternion.getX(),
                quaternion.getY(),
                quaternion.getZ(),
                quaternion.getW(), // w value of quaternion according to python scipy convention (different from Madgwick)
                0.1f, 0.1f);

        return processedData;
    }

}
