package com.example.gsurfexample.utils.algorithms;


import android.util.Log;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.TimeSample;

import java.lang.reflect.Array;

/**
 * Data Processor provides function processTimeSample which takes a TimeSample and
 * conducts several data processing steps on it to derive further data from the TimeSample.
 * Processing steps include
 * - the application of Madgwicks algorithm,
 * - transformation of local accelerations to global accelerations,
 * - integration of global accelerations,
 * - sensor fusion of acceleration data and gps data by applying Kalman filter and
 * - highpass filtering of z position.
 */
public class DataProcessor {

    private final float SAMPLEPERIOD;               // Sample period
    private final float CUTOFF;                     // Cutoff frequency of Butterworth filter
    private final float BETAMADGWICK;               // Parameter of Madgwick algorithm

    private MadgwickAHRS madgwickAHRS;              // Sensor fusion algorithm
    private Integrator integratorAcceleration;      // Integrator for integrating accelerations
    private Integrator integratorVelocities;        // Integrator for integrating velocities

    /**
     * Constructor initializes Madgwick filter, highpass filter and Integretors.
     * @param samplePeriod   [s] Sample period of TimeSamples.
     * @param cutoff [Hz] Cutoff frequncy of highpass filter.
     * @param betaMadgwick [-] Tuning parameter of Madgwick filter.
     */
    public DataProcessor(float samplePeriod, float cutoff, float betaMadgwick) {
        SAMPLEPERIOD = samplePeriod;
        CUTOFF = cutoff;
        BETAMADGWICK = betaMadgwick;

        madgwickAHRS = new MadgwickAHRS(SAMPLEPERIOD, BETAMADGWICK);
        integratorAcceleration = new Integrator(SAMPLEPERIOD);
        integratorVelocities = new Integrator(SAMPLEPERIOD);
    }

    /**
     * Method takes a TimeSample and conducts several data processing steps on it to derive
     * further data from the TimeSample. Processing steps include
     * - the application of Madgwicks algorithm,
     * - transformation of local accelerations to global accelerations,
     * - integration of global accelerations,
     * - sensor fusion of acceleration data and gps data by applying Kalman filter and
     * - highpass filtering of z position.
     *
     * @param timeSample to process.
     * @return Processed data or null since processing steps need to collect timeSamples
     *          over certain period.
     *
     */
    public ProcessedData processTimeSample(TimeSample timeSample){

        // Data processing
        // Madgwick algorithm to calculate quaternion
        madgwickAHRS.update( timeSample.getWx(),   timeSample.getWy(),  timeSample.getWz(),
                            timeSample.getDdx(),  timeSample.getDdy(), timeSample.getDdz(),
                             timeSample.getBx(),   timeSample.getBy(),  timeSample.getBz());

        // Calculate global acceleration
        // Quaternion Object Java: (x,y,z,p) [same as in Scipy.Rotation], but result from madgwick(p,x,y,z)
        Quaternion quaternion = new Quaternion(Array.getFloat(madgwickAHRS.getQuaternion(), 1),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 2),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 3),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 0));

        float[] globalAccelerations = quaternion.rotateVector(new float[] {timeSample.getDdx(),
                                                    timeSample.getDdy(), timeSample.getDdz()});

        Log.i("DATAPROCESSOR", "Into Processing Ddx: " + Float.toString(timeSample.getDdx())
                + " Ddy: " + Float.toString(timeSample.getDdy())
                + " Ddz: " + Float.toString(timeSample.getDdz()));
        Log.i("DATAPROCESSOR", "Calculated DdX: " + Float.toString(globalAccelerations[0])
                + " DdY: " + Float.toString(globalAccelerations[1])
                + " DdZ: " + Float.toString(globalAccelerations[2]));


        // Sensor fusion with KalmanFilter
        /*
        if(k in index_GPS):
        GPS_updated = True
        else:
        GPS_updated = False

        kalman_filter.predict(u[:,k])
        (x_pred, P_pred) = kalman_filter.get_state()

        if(GPS_updated):
        kalman_filter.update(meas[:, k])
        (x, P) = kalman_filter.get_state()

        est_state[k, :] = x
        est_pred[k, :] = x_pred
        est_cov[k, ...] = P

                (x_hat, x_hat2) = kalmanIntervalPostCorrector.correctKalmanResultsInInterval(x, GPS_updated,
                kalman_filter.getDelta(),
                u[:,k])    # */



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
