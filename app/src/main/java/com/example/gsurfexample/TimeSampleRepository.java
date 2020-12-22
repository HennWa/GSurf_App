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

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;

public class TimeSampleRepository {

    // Attributes
    private Application application;
    // Interfaces and db access related
    private TimeSampleDao timeSampleDao;
    private SensorDataFetch sensorDataFetch;
    // Live Data
    private LiveData<List<TimeSample>> allTimeSamples;
    private LiveData<TimeSample> lastTimeSample;
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
        // Live Data
        allTimeSamples = timeSampleDao.getAllTimeSamples();
        lastTimeSample = timeSampleDao.getLastTimeSamples();
    }

    // Nested classes
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


    // Methods
    public void sensorDataFetch(){
        if(sensorDataFetch==null){
            sensorDataFetch = new SensorDataFetch();
        }
        sensorDataFetch.execute();
    }

    public void stopSensorDataFetch(){
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
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


    public LiveData<TimeSample> getLastTimeSamples() { return lastTimeSample; }

}






















