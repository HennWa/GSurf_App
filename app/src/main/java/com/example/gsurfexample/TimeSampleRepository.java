package com.example.gsurfexample;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class TimeSampleRepository {

    // Attributes
    private Application application;
    // Interfaces and db access related
    private TimeSampleDao timeSampleDao;
    private ProcessedDataDao processedDataDao;
    private SensorDataFetch sensorDataFetch;
    // Data processing
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
    public TimeSampleRepository(Application app){

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
    }

    // Nested classes
    // time_sample_table
    private class SensorDataFetch extends AsyncTask<Void, Void, Void> implements SensorEventListener {

        private float data;
        private long timeElapsed;
        private SensorManager sensorManager;

        private SensorDataFetch(){
            sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
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

            TimeSample timeSample = new TimeSample(System.currentTimeMillis()/1000,
                    measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                    measAccelerometer[0], measAccelerometer[1], measAccelerometer[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1], measBField[2],
                    measBField[0], measBField[1]);
            insert(timeSample);
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
            while(processingPipe.size() > 0){

                Log.i("hh", "closed thread------------------------------------------------------" + Integer.toString(processingPipe.size()));

                processedData = dataProcessor.transferData(getTimeSamplesById(processingPipe.get(0)));
                insert(processedData);

                // remove first entry
                processingPipe.remove(0);

            }
            Log.i("hh", "closed thread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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

        // Fetch sensor data
        if(sensorDataFetch==null){
            sensorDataFetch = new SensorDataFetch();
        }
        sensorDataFetch.execute();

        // Data processing
        if(processingPipe == null){
            processingPipe = new ArrayList<Integer>();
        }
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
        allTimeSamples.observeForever(timeSamplesObserver);
    }

    public void stopSensorDataFetch(){
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
        }
        if(allTimeSamples!=null){
            allTimeSamples.removeObserver(timeSamplesObserver);
        }
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






















