package com.example.gsurfexample;

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
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;

public class TimeSampleRepository {

    // Attributes
    // General
    private static final String TAG = "Repository";
    private Application application;
    // Manager and listener
    private LocationManager locationManager;
    private LocationListener locationListener;
    // Interfaces and db access related
    private TimeSampleDao timeSampleDao;
    private ProcessedDataDao processedDataDao;
    private SensorDataFetch sensorDataFetch;
    // For data filtering
    private ResampleFilter resampleFilter;
    // Data processing in DataProcessor
    private ProcessDataAsyncTask processDataAsyncTask;
    private Observer<List<TimeSample>> timeSamplesObserver;
    private ArrayList<Integer> processingPipe;
    private DataProcessor dataProcessor;
    // Live Data
    private LiveData<List<TimeSample>> allTimeSamples;
    private LiveData<TimeSample> lastTimeSample;
    private LiveData<List<ProcessedData>> allProcessedData;
    // Buffer for sensor measurements
    private float[] measAccelerometer = new float[3];
    private float[] measLinAcceleration = new float[3];
    private float[] measBField = new float[3];
    private float[] measGyroscope = new float[3];
    private Location location;


    // Constructor
    public TimeSampleRepository(Application app) {

        // Db and context related
        application = app;
        TimeSampleDataBase timeSampleDataBase = TimeSampleDataBase.getInstance(application);
        // Interfaces
        timeSampleDao = timeSampleDataBase.timeSampleDao();
        processedDataDao = timeSampleDataBase.processedDataDao();
        // Live Data
        allTimeSamples = timeSampleDao.getAllTimeSamples();
        lastTimeSample = timeSampleDao.getLastTimeSamples();
        allProcessedData = processedDataDao.getAllProcessedData();
        // Register location listener and listen
        // Shall this run in background thread? (Problem with Looper!)
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {

                // filter/aggregate data and store results in db
                resampleFilter.filterElement(new TimeSample(System.currentTimeMillis(),
                        measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                        measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                        measBField[0], measBField[1], measBField[2],
                        measGyroscope[0], measGyroscope[1], measGyroscope[2],
                        location.getLatitude(), location.getLongitude(), location.getAltitude(),
                        location.getLatitude(), location.getLongitude()));
            }
        };
        // Time sample observer for data processing
        timeSamplesObserver = new Observer<List<TimeSample>>() {
            @Override
            public void onChanged(@Nullable List<TimeSample> timeSamples) {

                // add entry into pipe
                if(timeSamples.size() > 0){
                    processingPipe.add(timeSamples.get(timeSamples.size() - 1).getId());
                }

                // create new dataProcessor
                if(dataProcessor == null){
                    dataProcessor = new DataProcessor(1f,1f,1f);
                }

                // Async task to process sensor data
                if((processDataAsyncTask == null) ||
                        (processDataAsyncTask.getStatus() == AsyncTask.Status.FINISHED)){
                    processDataAsyncTask = new ProcessDataAsyncTask();
                    processDataAsyncTask.execute();
                }
            }
        };
    }

    // Nested classes
    // Filter
    private class ResampleFilter{

        // Attributes
        private final long sampleRate; // in ms
        private long nextSampleTime;
        private float meanDdx, meanDdy, meanDdz;
        private float meanGFx, meanGFy, meanGFz;
        private float meanBx, meanBy, meanBz;
        private float meanWx, meanWy, meanWz;
        private float meanLat, meanLon, meanHeight;
        private float meanXGPS, meanYGPS;

        private List<TimeSample> filterCache;

        // Constructor
        public ResampleFilter(long sampleR) {
            sampleRate = sampleR;
            nextSampleTime = 0;
            meanDdx = meanDdy = meanDdz = 0;
            meanGFx = meanGFy = meanGFz = 0;
            meanBx = meanBy = meanBz = 0;
            meanWx = meanWy = meanWz = 0;
            meanLat = meanLon = meanHeight = 0;
            meanXGPS = meanYGPS = 0;
            filterCache = new ArrayList<>();
        }

        // Methods
        public void filterElement(TimeSample timeSample){
        // filter applies mean of bins

            filterCache.add(timeSample);

            if(filterCache.size()==1){
                nextSampleTime = filterCache.get(0).getTimeStamp() + sampleRate;
            }

            if(timeSample.getTimeStamp() > nextSampleTime){
                int numElem = filterCache.size();
                for(int i = 0; i < numElem-1; i++){
                    meanDdx += (float) filterCache.get(0).getDdx();
                    meanDdy += (float) filterCache.get(0).getDdy();
                    meanDdz += (float) filterCache.get(0).getDdz();
                    meanGFx += (float) filterCache.get(0).getGFx();
                    meanGFy += (float) filterCache.get(0).getGFy();
                    meanGFz += (float) filterCache.get(0).getGFz();
                    meanBx += (float) filterCache.get(0).getBx();
                    meanBy += (float) filterCache.get(0).getBy();
                    meanBz += (float) filterCache.get(0).getBz();
                    meanWx += (float) filterCache.get(0).getWx();
                    meanWy += (float) filterCache.get(0).getWy();
                    meanWz += (float) filterCache.get(0).getWz();
                    meanLat += (float) filterCache.get(0).getLat();
                    meanLon += (float) filterCache.get(0).getLon();
                    meanHeight += (float) filterCache.get(0).getHeight();
                    meanXGPS += (float) filterCache.get(0).getXGPS();
                    meanYGPS += (float) filterCache.get(0).getYGPS();
                    filterCache.remove(0);
                }
                meanDdx /= (numElem-1);
                meanDdy /= (numElem-1);
                meanDdz /= (numElem-1);
                meanGFx /= (numElem-1);
                meanGFy /= (numElem-1);
                meanGFz /= (numElem-1);
                meanBx /= (numElem-1);
                meanBy /= (numElem-1);
                meanBz /= (numElem-1);
                meanWx /= (numElem-1);
                meanWy /= (numElem-1);
                meanWz /= (numElem-1);
                meanLat /= (numElem-1);
                meanLon /= (numElem-1);
                meanHeight /= (numElem-1);
                meanXGPS /= (numElem-1);
                meanYGPS /= (numElem-1);

                insert(new TimeSample( nextSampleTime-sampleRate/2,
                        meanDdx, meanDdy, meanDdz,
                        meanGFx, meanGFy, meanGFz,
                        meanBx, meanBy, meanBz,
                        meanWx, meanWy, meanWz,
                        meanLat, meanLon, meanHeight,
                        meanXGPS, meanYGPS));

                Log.i("FILTER", Long.toString(nextSampleTime-sampleRate/2));

                meanDdx = meanDdy = meanDdz = 0;
                meanGFx = meanGFy = meanGFz = 0;
                meanBx = meanBy = meanBz = 0;
                meanWx = meanWy = meanWz = 0;
                meanLat = meanLon = meanHeight = 0;
                meanXGPS = meanYGPS = 0;
                nextSampleTime += sampleRate;
            }
        }
    }

    // time_sample_table
    private class SensorDataFetch extends AsyncTask<Void, Void, Void> implements SensorEventListener {

        private SensorManager sensorManager;

        private SensorDataFetch() {
            sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Instantiate new ResampleFilter
            resampleFilter = new ResampleFilter(100);

            // Register sensor listener
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
                Log.d(TAG, "onCreate: Registered accelerometer listener");
            } else {
                Toast.makeText(application, "Accelerometer is not supported", Toast.LENGTH_SHORT).show();
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

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //do nothing
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //do nothing
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double a = sensorEvent.values[0];

            Sensor sensor = sensorEvent.sensor;
            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                measAccelerometer = sensorEvent.values;
            }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
                measGyroscope = sensorEvent.values;
            }else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                measBField = sensorEvent.values;
            }

            // filter/aggregate data and store results in db
            resampleFilter.filterElement(new TimeSample(System.currentTimeMillis(),
                    measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                    measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1]));
        }

        protected void unregisterListener() {
            if(sensorManager!=null) {
                sensorManager.unregisterListener(this);
            }
        }
    }

    private class ProcessDataAsyncTask extends AsyncTask<Void, Void, Void> {

        ProcessedData processedData;

        @Override
        protected Void doInBackground(Void... voids) {

            // process all timesamples of pipe till no more elements contained
            // (elements are continuously appended at the end)
            while((processingPipe != null) && (processingPipe.size() > 0)){

                processedData = dataProcessor.transferData(getTimeSamplesById(processingPipe.get(0)));
                insert(processedData);

                // remove first entry
                processingPipe.remove(0);
            }
            return null;
        }
    }

    private static class InsertTimeSampleAsyncTask extends AsyncTask<TimeSample, Void, Void> {
        private TimeSampleDao timeSampleDao;

        private InsertTimeSampleAsyncTask(TimeSampleDao timeSampleDao){
            this.timeSampleDao = timeSampleDao;
        }

        @Override
        protected Void doInBackground(TimeSample... timeSamples){
            timeSampleDao.insert(timeSamples[0]);
            return null;
        }
    }

    private static class UpdateTimeSampleAsyncTask extends AsyncTask<TimeSample, Void, Void> {
        private TimeSampleDao timeSampleDao;

        private UpdateTimeSampleAsyncTask(TimeSampleDao tmeSampleDao){
            this.timeSampleDao = timeSampleDao;
        }

        @Override
        protected Void doInBackground(TimeSample... timeSamples){
            timeSampleDao.update(timeSamples[0]);
            return null;
        }
    }

    private static class DeleteTimeSampleAsyncTask extends AsyncTask<TimeSample, Void, Void> {
        private TimeSampleDao timeSampleDao;

        private DeleteTimeSampleAsyncTask(TimeSampleDao timeSampleDao){
            this.timeSampleDao = timeSampleDao;
        }

        @Override
        protected Void doInBackground(TimeSample... timeSamples){
            timeSampleDao.delete(timeSamples[0]);
            return null;
        }
    }

    private static class DeleteAllTimeSamplesAsyncTask extends AsyncTask<Void, Void, Void> {
        private TimeSampleDao timeSampleDao;

        private DeleteAllTimeSamplesAsyncTask(TimeSampleDao timeSampleDao){
            this.timeSampleDao = timeSampleDao;
        }

        @Override
        protected Void doInBackground(Void... voids){
            timeSampleDao.deleteAllTimeSamples();
            return null;
        }
    }

    // processed_data_table
    private static class InsertProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        private InsertProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.insert(processedData[0]);
            return null;
        }
    }

    private static class UpdateProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        private UpdateProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.update(processedData[0]);
            return null;
        }
    }

    private static class DeleteProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        private DeleteProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.delete(processedData[0]);
            return null;
        }
    }

    private static class DeleteAllProcessedDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProcessedData timeSampleDao;
        private ProcessedDataDao processedDataDao;

        private DeleteAllProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        @Override
        protected Void doInBackground(Void... voids){
            processedDataDao.deleteAllProcessedData();
            return null;
        }
    }


    // Methods
    // time_sample
    public void sensorDataFetch(){
        // Register sensor listener, take data from db, process it and write into new db

        // Fetch sensor data
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
        }
        sensorDataFetch = new SensorDataFetch();
        sensorDataFetch.execute();

        // Fetch location data (should be in background thread?)
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1, locationListener);
        }

        // Data processing (fetch LiveData, process and write to second db)
        if(processingPipe == null){
            processingPipe = new ArrayList<Integer>();
        }
        if (!allTimeSamples.hasObservers()){
            allTimeSamples.observeForever(timeSamplesObserver);
        }
    }

    public void stopSensorDataFetch(){
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
        }
        if(allTimeSamples!=null){
            allTimeSamples.removeObserver(timeSamplesObserver);
        }
        if(locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
        processingPipe = null;
    }

    public void insert(TimeSample timeSample){
        new InsertTimeSampleAsyncTask(timeSampleDao).execute(timeSample);
    }

    public void update(TimeSample timesample){
        new UpdateTimeSampleAsyncTask(timeSampleDao).execute(timesample);
    }

    public void delete(TimeSample timeSample){
        new DeleteTimeSampleAsyncTask(timeSampleDao).execute(timeSample);
    }

    public void deleteAllTimeSamples(){
        new DeleteAllTimeSamplesAsyncTask(timeSampleDao).execute();
    }

    public LiveData<List<TimeSample>> getAllTimeSamples() {
        return allTimeSamples;
    }

    public LiveData<TimeSample> getLastTimeSamples() {
        return lastTimeSample;
    }

    public TimeSample getTimeSamplesById(int id) {
        return timeSampleDao.getTimeSamplesByID(id);
    }

    // processed data
    public void insert(ProcessedData processedData){
        new InsertProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    public void update(ProcessedData processedData){
        new UpdateProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    public void delete(ProcessedData processedData){
        new DeleteProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    public void deleteAllProcessedData(){
        new DeleteAllProcessedDataAsyncTask(processedDataDao).execute();
    }

    public LiveData<List<ProcessedData>> getAllProcessedData() {
        return allProcessedData;
    }
}






















