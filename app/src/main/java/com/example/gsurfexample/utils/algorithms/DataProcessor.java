package com.example.gsurfexample.utils.algorithms;


import android.util.Log;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.TimeSample;

import java.lang.reflect.Array;

public class DataProcessor {

    private final float SAMPLEPERIOD;               // Sample period
    private final float CUTOFF;                     // Cutoff frequency of Butterworth filter
    private final float BETAMADGWICK;               // Parameter of Madgwick algorithm

    private MadgwickAHRS madgwickAHRS;              // Sensor fusion algorithm
    private Integrator integratorAcceleration;      // Integrator for integrating accelerations
    private Integrator integratorVelocities;        // Integrator for integrating velocities

    // Constructor
    public DataProcessor(float samplePeriod, float cutoff, float betaMadgwick) {
        SAMPLEPERIOD = samplePeriod;
        CUTOFF = cutoff;
        BETAMADGWICK = betaMadgwick;

        madgwickAHRS = new MadgwickAHRS(SAMPLEPERIOD, BETAMADGWICK);
        integratorAcceleration = new Integrator(SAMPLEPERIOD);
        integratorVelocities = new Integrator(SAMPLEPERIOD);
    }

    // Methods
    public ProcessedData transferData(TimeSample timeSample){


        Log.i("DATAPROCESSOR", "Into Processing" + Float.toString(timeSample.getDdx()));

        // Data processing chain


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

        // Calculate global velocity and global position from acceleration
        float[] globalVelocities = integratorAcceleration.cumTrapzIntegration(globalAccelerations);
        float[] globalPosition = integratorVelocities.cumTrapzIntegration(globalVelocities);

        // High-pass-filter for Z direction


        // Store in object
        int id = 0;        // here real session id
        ProcessedData processedData = new ProcessedData(id, timeSample.getTimeStamp(),
                globalAccelerations[0], globalAccelerations[1], globalAccelerations[2],
                globalVelocities[0], globalVelocities[1], globalVelocities[2],
                globalPosition[0], globalPosition[1], globalPosition[2],
                0.1f, 0.1f, 0.1f,
                timeSample.getWx(), timeSample.getWy(), timeSample.getWz(),
                quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW(), // w value of quaternion according to python scipy convention (different from Madgwick)
                0.1f, 0.1f);

        return processedData;
    }

}
