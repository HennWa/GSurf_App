package com.example.gsurfexample.source.local.live;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.example.gsurfexample.utils.sensors.SensorDataFetch;

import java.util.List;

/**
 * ProcessedDataRepository provides access to Room database and methods to trigger and stop data
 * fetching from sensors.
 */
public class ProcessedDataRepository {

    // Attributes
    // General
    private static final String TAG = "Repository";
    private Application application;
    // Interfaces and db access related
    private ProcessedDataDao processedDataDao;
    // Data fetching
    private SensorDataFetch sensorDataFetch;
    // Live Data
    private LiveData<List<ProcessedData>> allProcessedData;
    private LiveData<ProcessedData> lastProcessedDataSample;

    /**
     * Constructor.
     * @param app   Application object for connecting to Room database
     */
    public ProcessedDataRepository(Application app) {
        // Initialization
        application = app;
        TimeSampleDataBase timeSampleDataBase = TimeSampleDataBase.getInstance(application);
        processedDataDao = timeSampleDataBase.processedDataDao();
        // Live Data
        allProcessedData = processedDataDao.getAllProcessedData();
        lastProcessedDataSample = processedDataDao.getLastProcessedData();
    }

    // AsyncTasks for operations on processed_data_table
    /**
     * Nested class InsertProcessedDataAsyncTask provides insert operation.
     */
    private static class InsertProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        /**
         * Constructor.
         * @param processedDataDao Dao for connecting to database
         */
        private InsertProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        /**
         * Insert Operation.
         * @param processedData Data object to be inserted.
         */
        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.insert(processedData[0]);
            return null;
        }
    }

    /**
     * Nested class UpdateProcessedDataAsyncTask provides update operation.
     */
    private static class UpdateProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        /**
         * Constructor.
         * @param processedDataDao Dao for connecting to database
         */
        private UpdateProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        /**
         * Insert Operation.
         * @param processedData Data object to be updated.
         */
        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.update(processedData[0]);
            return null;
        }
    }

    /**
     * Nested class DeleteProcessedDataAsyncTask provides delete operation.
     */
    private static class DeleteProcessedDataAsyncTask extends AsyncTask<ProcessedData, Void, Void> {
        private ProcessedDataDao processedDataDao;

        /**
         * Constructor.
         * @param processedDataDao Dao for connecting to database
         */
        private DeleteProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        /**
         * Delete Operation.
         */
        @Override
        protected Void doInBackground(ProcessedData... processedData) {
            processedDataDao.delete(processedData[0]);
            return null;
        }
    }

    /**
     * Nested class DeleteAllProcessedDataAsyncTask provides delete all operation.
     */
    private static class DeleteAllProcessedDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProcessedDataDao processedDataDao;

        /**
         * Constructor.
         * @param processedDataDao Dao for connecting to database
         */
        private DeleteAllProcessedDataAsyncTask(ProcessedDataDao processedDataDao){
            this.processedDataDao = processedDataDao;
        }

        /**
         * Delete all Operation.
         */
        @Override
        protected Void doInBackground(Void... voids){
            processedDataDao.deleteAllProcessedData();
            return null;
        }
    }

    // Methods
    /**
     * Triggers the fetching of sensor and location sensor data by instantiating a new
     * SensorDataFetch object.
     */
    public void sensorDataFetch(){
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
        }
        sensorDataFetch = new SensorDataFetch(application,this);
        sensorDataFetch.execute();
    }

    /**
     * Stops the fetching of sensor and location sensor data by by unregistering all listener.
     */
    public void stopSensorDataFetch(){
        if(sensorDataFetch!=null){
            sensorDataFetch.unregisterListener();
        }
    }


    // Dao operations on processed_data_table
    /**
     * Insert operation.
     * @param processedData Data object to be inserted.
     */
    public void insert(ProcessedData processedData){
        new InsertProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    /**
     * Update operation.
     * @param processedData Data object to be updated.
     */
    public void update(ProcessedData processedData){
        new UpdateProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    /**
     * Delete operation.
     * @param processedData Data object to be deleted.
     */
    public void delete(ProcessedData processedData){
        new DeleteProcessedDataAsyncTask(processedDataDao).execute(processedData);
    }

    /**
     * Delete all operation.
     */
    public void deleteAllProcessedData(){
        new DeleteAllProcessedDataAsyncTask(processedDataDao).execute();
    }

    /**
     * Get all data in database.
     * @return Data in database as LiveData.
     */
    public LiveData<List<ProcessedData>> getAllProcessedData() {
        return allProcessedData;
    }

    /**
     * Get last entry in database.
     * @return Last entry in database as LiveData.
     */
    public LiveData<ProcessedData> getLastProcessedDataSample() {
        return lastProcessedDataSample;
    }

    /**
     * Get entry by id in database.
     * @return Specific entry in database not as LiveData.
     */
    public ProcessedData getProcessedDataSamplesById(int id) {
        return processedDataDao.getProcessedDataSamplesById(id);
    }
}






















