package com.example.gsurfexample.utils.algorithms;


import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.TimeSample;
import com.example.gsurfexample.utils.other.GlobalParams;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

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

    private final String sessionID;
    private final GlobalParams globalParams;
    private int intervalStepCount;
    private int sampleCount;
    private final ArrayList<ProcessedData> processedDataCache;

    private MadgwickAHRS madgwickAHRS;                          // Sensor fusion algorithm
    private KalmanFilter kalmanFilter;                          // KalmanFilter for sensor fusion
    private DigitalButterworthFilter lowPassFilterDX;           // Filter for velocity in X direction
    private DigitalButterworthFilter lowPassFilterDY;           // Filter for velocity in Y direction
    //private ShiftingAverageFilter lowPassFilterDX;              // Filter for velocity in X direction
    //private ShiftingAverageFilter lowPassFilterDY;              // Filter for velocity in Y direction

    private final Matrix Uk;                                    // Input matrix for sensor fusion
    private final Matrix Zk;                                    // Measurement (GPS) matrix for sensor fusion
    private final double[] lastGPSLoc;                          // last timeSample for GPS values comparison
    private boolean newInterval;


    /**
     * Constructor initializes Madgwick filter, highpass filter and Kalman Filter.
     */
    public DataProcessor() {

        sessionID = UUID.randomUUID().toString();
        globalParams = GlobalParams.getInstance(); // get singleton
        sampleCount = 0;
        lastGPSLoc = new double[]{0, 0};
        Uk = new Matrix(2,1);
        Zk = new Matrix(2,1);
        newInterval = false;
        processedDataCache = new ArrayList<>();

        try {
            madgwickAHRS = new MadgwickAHRS(globalParams.sampleRs, globalParams.betaMadgwick);
            kalmanFilter = new KalmanFilter(globalParams.getA(), globalParams.getB(), globalParams.getH(),
                    globalParams.getQ(), globalParams.getR(), globalParams.getX0(), globalParams.getP0(),
                    globalParams.sampleRs, globalParams.maxIntervalLength);
            lowPassFilterDX = new DigitalButterworthFilter(globalParams.nominatorLPVelocity,
                    globalParams.denominatorLPVelocity);
            lowPassFilterDY = new DigitalButterworthFilter(globalParams.nominatorLPVelocity,
                    globalParams.denominatorLPVelocity);
            //lowPassFilterDX = new ShiftingAverageFilter(20); // only temporary
            //lowPassFilterDY = new ShiftingAverageFilter(20);
        }catch(Exception e) {
            e.printStackTrace();
        }
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
     * @return List of processed data or null since processing steps need to collect timeSamples
     *          over certain period.
     */
    public ArrayList<ProcessedData> processTimeSample(TimeSample timeSample){

        // Treat cases
        if(lastGPSLoc[0] == 0 || lastGPSLoc[1] == 0){      // wait for GPS, skip first sample
            lastGPSLoc[0] = timeSample.getLon();
            lastGPSLoc[1] = timeSample.getLat();
            return null;
        }
        if (newInterval){
            processedDataCache.clear(); // Empty cache
            newInterval = false;
        }
        if(sampleCount==0){
            kalmanFilter.Xk_k.setData(SphericalMercator.lon2x(timeSample.getLon()),
                    SphericalMercator.lat2y(timeSample.getLat()), 0, 0);
        }
        sampleCount += 1;

        // Step1: Madgwick algorithm to calculate quaternion
        madgwickAHRS.update( timeSample.getWx(),   timeSample.getWy(),  timeSample.getWz(),
                timeSample.getGFx(),  timeSample.getGFy(), timeSample.getGFz(),
                timeSample.getBx(),   timeSample.getBy(),  timeSample.getBz());

        // Quaternion Object Java: (x,y,z,p) [same as in Scipy.Rotation], but result from madgwick(p,x,y,z)
        Quaternion quaternion = new Quaternion(Array.getFloat(madgwickAHRS.getQuaternion(), 1),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 2),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 3),
                                                Array.getFloat(madgwickAHRS.getQuaternion(), 0));

        // Step2: Calculate global acceleration (from Linear Acceleration Sensor)
        float[] globalAccelerations = quaternion.rotateVector(new float[] {timeSample.getDdx(),
                                                    timeSample.getDdy(), timeSample.getDdz()});

        // Copy results of this sample as available here
        processedDataCache.add(new ProcessedData(sessionID, timeSample.getTimeStamp(),
                globalAccelerations[0], globalAccelerations[1], globalAccelerations[2],
                0f, 0f, 0f, 0f, 0f, 0f, 0f , 0f,  // from later processing steps
                timeSample.getWx(), timeSample.getWy(), timeSample.getWz(),
                quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW(), // w value of quaternion according to python scipy convention (different from Madgwick)
                (float)timeSample.getLat(), (float)timeSample.getLon(), 0));


        // Step 3: High-pass-filter for Z direction
        // Integrator for integrating accelerations
        //Integrator integratorAcceleration = new Integrator(SAMPLEPERIOD);
        // Integrator for integrating velocities
        //Integrator integratorVelocities = new Integrator(SAMPLEPERIOD);
        //float[] globalVelocities = integratorAcceleration.cumTrapzIntegration(globalAccelerations);
        //float[] globalPosition = integratorVelocities.cumTrapzIntegration(globalVelocities);
        //...


        // Step 4: Sensor fusion with KalmanFilter
        // (global accelerations + GPS -> X, Y, Z, dX, dY, dZ
        try {
            Uk.setData(globalAccelerations[0], globalAccelerations[1]);
            kalmanFilter.predict(Uk);
            if((timeSample.getLon() !=  lastGPSLoc[0]) ||
                    (timeSample.getLat() !=  lastGPSLoc[1]) ||
                    intervalStepCount >= globalParams.maxIntervalLength-1){

                intervalStepCount = 0;
                lastGPSLoc[0] = timeSample.getLon();
                lastGPSLoc[1] = timeSample.getLat();

                Zk.setData(SphericalMercator.lon2x(timeSample.getLon()),
                            SphericalMercator.lat2y(timeSample.getLat()));

                // Storage for results from kalman filtering
                double[][] updatedInterval = kalmanFilter.updateAndGetResults(Zk);  // size: states X number of samples

                // Copy results to cache
                for(int m = 0; m < updatedInterval[0].length; m++) {
                    processedDataCache.get(m).setX(updatedInterval[0][m]);
                    processedDataCache.get(m).setY(updatedInterval[1][m]);
                    processedDataCache.get(m).setdX(updatedInterval[2][m]);
                    processedDataCache.get(m).setdY(updatedInterval[3][m]);
                }

                // Step 5: Low pass filtering of velocities (Note: Precision loos due to float)
                float[] dX = new float[updatedInterval[0].length];
                float[] dY = new float[updatedInterval[0].length];
                for(int m = 0; m < updatedInterval[0].length; m++) {
                    dX[m] = (float)processedDataCache.get(m).getDX();
                    dY[m] = (float)processedDataCache.get(m).getDY();
                }
                float[] dXFilt = lowPassFilterDX.filterSequence(dX);
                //noinspection SuspiciousNameCombination
                float[] dYFilt = lowPassFilterDY.filterSequence(dY);

                // Copy results to cache
                for(int m = 0; m < updatedInterval[0].length; m++) {
                    processedDataCache.get(m).setDXFilt(dXFilt[m]);
                    processedDataCache.get(m).setDYFilt(dYFilt[m]);
                }

                // Step 6: State categorization

                // Wave detection
                for(int m = 0; m < updatedInterval[0].length; m++) {
                    // Categorization by velocity
                    if(Math.sqrt(processedDataCache.get(m).getDXFilt() * processedDataCache.get(m).getDXFilt() +
                            processedDataCache.get(m).getDYFilt() * processedDataCache.get(m).getDYFilt()) >
                            globalParams.waveThresholdVelocity){
                        processedDataCache.get(m).setState(GlobalParams.States.valueOf("SURFINGWAVE").ordinal());
                    }
                }

                // Set flag and return processed data in array
                newInterval = true;
                return processedDataCache;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        // Increment counter of interval steps and processed time samples
        intervalStepCount += 1;
        sampleCount += 1;

        return null;
    }
}