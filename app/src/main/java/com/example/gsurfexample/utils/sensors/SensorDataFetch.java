package com.example.gsurfexample.utils.sensors;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.ProcessedDataRepository;
import com.example.gsurfexample.source.local.live.TimeSample;
import com.example.gsurfexample.utils.algorithms.DataProcessor;
import com.example.gsurfexample.utils.algorithms.ResampleFilter;
import com.example.gsurfexample.utils.other.GlobalParams;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.R;

/**
 * SensorDataFetch connects to the sensors by calling listener in an AsyncTask and connects
 * to the location sensor by calling a listener in the main thread.
 * Data from the sensors and the location sensor is merged in TimeSample objects which are
 * stored in a cache. A DataProcessor further processes the data by retrieving TimeSamples
 * from the cache in FIFO manner in an AsyncTask. Finally ProcessedData is stored in Room Database.
 */
public class SensorDataFetch extends AsyncTask<Void, Void, Void> implements SensorEventListener {

    // Attributes
    // General
    private final GlobalParams globalParams;
    private static final String TAG = "SensorDataFetch";
    private Application application;
    private ProcessedDataRepository processedDataRepository;
    // Manager and listener
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    // For data filtering
    private ResampleFilter resampleFilter;
    private TimeSample resampledTimeSampleDataOrNull;
    // Data processing in DataProcessor
    private ProcessDataAsyncTask processDataAsyncTask;
    private ArrayList<TimeSample> timeSampleCache;
    private ArrayList<ProcessedData> dataProcessorResultsOrNull;
    private DataProcessor dataProcessor;
    // Buffer for sensor measurements
    private float[] measAccelerometer = new float[3];
    private float[] measLinAccel = new float[3];
    private float[] measBField = new float[3];
    private float[] measGyroscope = new float[3];
    private float[] locationGPS = new float[3];

    /**
     * Constructor initializes managers and also listener for location sensor.
     * @param app   Application object for registering Sensor managers.
     * @param repository Repository for accessing the database to store processed data.
     */
    public SensorDataFetch(Application app, ProcessedDataRepository repository) {
        // Initialization
        application = app;
        globalParams = GlobalParams.getInstance(); // get singleton
        processedDataRepository = repository;
        timeSampleCache = new ArrayList<TimeSample>();
        dataProcessor = new DataProcessor(); // from global config

        // Register sensor manager
        sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);

        // Register location manager and listener and start listening
        // (Problem with Looper when run in background thread!)
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                locationGPS[0] = (float)location.getLatitude();
                locationGPS[1] = (float)location.getLongitude();
                locationGPS[2] = (float)location.getAltitude();

                // Filter/aggregate data and store results in cache (result null or resampled TimeSample)
                resampledTimeSampleDataOrNull = resampleFilter.filterElement(new TimeSample(System.currentTimeMillis(),
                        measLinAccel[0], measLinAccel[1], measLinAccel[2],
                        measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                        measBField[0], measBField[1], measBField[2],
                        measGyroscope[0], measGyroscope[1], measGyroscope[2],
                        locationGPS[0], locationGPS[1], locationGPS[2],
                        locationGPS[0], locationGPS[1]));

                if (resampledTimeSampleDataOrNull!=null){
                    timeSampleCache.add(resampledTimeSampleDataOrNull);
                }
            }
        };
        // Start listening for location changes
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1, locationListener);
        }
    }

    /**
     * Nested class ProcessDataAsyncTask is used process data in cache in asynchronous manner.
     */
    private class ProcessDataAsyncTask extends AsyncTask<Void, Void, Void> {

        /**
         * Continuously takes and deletes piecewise TimeSamples from cache, processes the data and
         * stores in database.
         */
        @Override
        protected Void doInBackground(Void... voids) {
            while((timeSampleCache != null) && (timeSampleCache.size() > 0)) {
                dataProcessorResultsOrNull = dataProcessor.processTimeSample(timeSampleCache.get(0));
                timeSampleCache.remove(0);

                if(dataProcessorResultsOrNull != null){
                    for(int i=0; i<dataProcessorResultsOrNull.size(); i++){
                        processedDataRepository.insert(dataProcessorResultsOrNull.get(i));
                    }
                }
            }
            return null;
        }
    }

    // Methods
    /**
     * Initializes ResampleFilter and registers sensor listeners.
     */
    @Override
    protected Void doInBackground(Void... params) {
        resampleFilter = new ResampleFilter(globalParams.sampleR);  // in global config file

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
        } else {
            Toast.makeText(application, "Accelerometer is not supported", Toast.LENGTH_SHORT).show();
        }

        Sensor linAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (linAcceleration != null) {
            sensorManager.registerListener(this, linAcceleration, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onCreate: Registered linear acceleration listener");
        } else {
            Toast.makeText(application, "Linear acceleration is not supported", Toast.LENGTH_SHORT).show();
        }

        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onCreate: Registered gyroscope listener");
        } else {
            Toast.makeText(application, "Gyroscope is not supported", Toast.LENGTH_SHORT).show();
        }

        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetic != null) {
            sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onCreate: Registered magnetic field listener");
        } else {
            Toast.makeText(application, "Magnetic sensor is not supported", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * Not used.
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        //do nothing
    }

    /**
     * Not used.
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        //do nothing
    }

    /**
     * Not used.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Catches sensor events, stores sensor data in TimeSamples, resamples the TimeSample,
     * stores resampled TimeSamples in cache and triggers processDataAsyncTask.
     * @param sensorEvent   Application object for registering Sensor managers.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            measAccelerometer = sensorEvent.values;
        }else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            measLinAccel = sensorEvent.values;
        }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
            measGyroscope = sensorEvent.values;
        }else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            measBField = sensorEvent.values;
        }
        // filter/aggregate data and store results in cache (result null or resampled TimeSample)
        resampledTimeSampleDataOrNull = resampleFilter.filterElement(new TimeSample(System.currentTimeMillis(),
                measLinAccel[0], measLinAccel[1], measLinAccel[2],
                measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                measBField[0], measBField[1], measBField[2],
                measGyroscope[0], measGyroscope[1], measGyroscope[2],
                locationGPS[0], locationGPS[1], locationGPS[2],
                locationGPS[0], locationGPS[1]));
        if (resampledTimeSampleDataOrNull!=null){
            timeSampleCache.add(resampledTimeSampleDataOrNull);
        }

        // Async task to process data in cache
        if((processDataAsyncTask == null) ||
                (processDataAsyncTask.getStatus() == Status.FINISHED)){
            processDataAsyncTask = new ProcessDataAsyncTask();
            processDataAsyncTask.execute();
        }
    }

    /**
     * Unregister all listener.
     */
    public void unregisterListener() {
        if(sensorManager!=null) {
            sensorManager.unregisterListener(this);
        }
        if(locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}


