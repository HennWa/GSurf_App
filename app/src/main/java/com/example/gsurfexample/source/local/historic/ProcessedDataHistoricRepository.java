package com.example.gsurfexample.source.local.historic;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.ProcessedDataDao;
import com.example.gsurfexample.source.local.live.ProcessedDataRepository;
import com.example.gsurfexample.source.local.live.TimeSampleDataBase;
import com.example.gsurfexample.utils.sensors.SensorDataFetch;

import java.util.List;

/**
 * ProcessedDataHistoricRepository provides access to Room database and methods to trigger and stop data
 * fetching from sensors.
 */
public class ProcessedDataHistoricRepository {

    private final Application application;
    // Interfaces and db access related
    private final ProcessedDataHistoricDao processedDataHistoricDao;


    /**
     * Constructor.
     * @param app   Application object for connecting to Room database
     */
    public ProcessedDataHistoricRepository(Application app) {
        // Initialization
        application = app;
        ProcessedDataHistoricDataBase processedDataHistoricDataBase =
                ProcessedDataHistoricDataBase.getInstance(application);
        processedDataHistoricDao = processedDataHistoricDataBase.processedDataHistoricDao();
    }

    // AsyncTasks for operations on processed_data_table
    /**
     * Nested class InsertProcessedDataHistoricAsyncTask provides insert operation.
     */
    private static class InsertProcessedDataHistoricAsyncTask extends AsyncTask<ProcessedDataHistoric, Void, Void> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private InsertProcessedDataHistoricAsyncTask(ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Insert Operation.
         * @param processedDataHistoric Data object to be inserted.
         */
        @Override
        protected Void doInBackground(ProcessedDataHistoric... processedDataHistoric) {
            processedDataHistoricDao.insert(processedDataHistoric[0]);
            return null;
        }
    }

    /**
     * Nested class UpdateProcessedDataAsyncTask provides update operation.
     */
    private static class UpdateProcessedDataHistoricAsyncTask extends AsyncTask<ProcessedDataHistoric, Void, Void> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private UpdateProcessedDataHistoricAsyncTask(ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Insert Operation.
         * @param processedDataHistoric Data object to be updated.
         */
        @Override
        protected Void doInBackground(ProcessedDataHistoric... processedDataHistoric) {
            processedDataHistoricDao.update(processedDataHistoric[0]);
            return null;
        }
    }

    /**
     * Nested class DeleteProcessedDataAsyncTask provides delete operation.
     */
    private static class DeleteProcessedDataHistoricAsyncTask extends AsyncTask<ProcessedDataHistoric, Void, Void> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private DeleteProcessedDataHistoricAsyncTask(ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Delete Operation.
         */
        @Override
        protected Void doInBackground(ProcessedDataHistoric... processedDataHistoric) {
            processedDataHistoricDao.delete(processedDataHistoric[0]);
            return null;
        }
    }

    /**
     * Nested class DeleteAllProcessedDataAsyncTask provides delete all operation.
     */
    private static class DeleteAllProcessedDataHistoricAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private DeleteAllProcessedDataHistoricAsyncTask(ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Delete all Operation.
         */
        @Override
        protected Void doInBackground(Void... voids){
            processedDataHistoricDao.deleteAllProcessedDataHistoric();
            return null;
        }
    }

    /**
     * Nested class GetAllProcessedDataHistoricSyncAsyncTask for getting data from db.
     */
    private static class GetAllProcessedDataHistoricSyncAsyncTask extends AsyncTask<Void, Void, List<ProcessedDataHistoric>> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private GetAllProcessedDataHistoricSyncAsyncTask(ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Get all data as list.
         */
        @Override
        protected List<ProcessedDataHistoric> doInBackground(Void... voids) {
            return processedDataHistoricDao.getAllProcessedDataHistoricSync();
        }

        @Override
        protected  void onPostExecute(List<ProcessedDataHistoric> result) {
        }
    }

    /**
     * Nested class GetProcessedDataHistoricSamplesSyncBySessionIdAsyncTask for getting
     * data from db with selcetion by id.
     */
    private static class GetProcessedDataHistoricSamplesSyncBySessionIdAsyncTask
            extends AsyncTask<String, Void, List<ProcessedDataHistoric>> {
        private ProcessedDataHistoricDao processedDataHistoricDao;

        /**
         * Constructor.
         * @param processedDataHistoricDao Dao for connecting to database
         */
        private GetProcessedDataHistoricSamplesSyncBySessionIdAsyncTask(
                ProcessedDataHistoricDao processedDataHistoricDao){
            this.processedDataHistoricDao = processedDataHistoricDao;
        }

        /**
         * Get data by id as list.
         */
        @Override
        protected List<ProcessedDataHistoric> doInBackground(String... sessionId) {
            return processedDataHistoricDao.getProcessedDataHistoricSamplesSyncBySessionId(sessionId[0]);
        }

        @Override
        protected  void onPostExecute(List<ProcessedDataHistoric> result) {
        }
    }


    // Methods
    // Dao operations on processed_data_table
    /**
     * Insert operation.
     * @param processedDataHistoric Data object to be inserted.
     */
    public void insert(ProcessedDataHistoric processedDataHistoric){
        new InsertProcessedDataHistoricAsyncTask(processedDataHistoricDao).execute(processedDataHistoric);
    }

    /**
     * Update operation.
     * @param processedDataHistoric Data object to be updated.
     */
    public void update(ProcessedDataHistoric processedDataHistoric){
        new UpdateProcessedDataHistoricAsyncTask(processedDataHistoricDao).execute(processedDataHistoric);
    }

    /**
     * Delete operation.
     * @param processedDataHistoric Data object to be deleted.
     */
    public void delete(ProcessedDataHistoric processedDataHistoric){
        new DeleteProcessedDataHistoricAsyncTask(processedDataHistoricDao).execute(processedDataHistoric);
    }

    /**
     * Delete all operation.
     */
    public void deleteAllProcessedDataHistoric(){
        new DeleteAllProcessedDataHistoricAsyncTask(processedDataHistoricDao).execute();
    }

    /**
     * Get all data in database.
     * @return Data in database as List.
     */
    public List<ProcessedDataHistoric> getAllProcessedDataHistoricSync() throws Exception {
        return new ProcessedDataHistoricRepository.
                GetAllProcessedDataHistoricSyncAsyncTask(processedDataHistoricDao).execute().get();
    }


    /**
     * Get entry by id in database.
     * @return Specific entry in database not as LiveData.
     */
    public List<ProcessedDataHistoric> getProcessedDataHistoricSyncBySessionsId(String sessionId) throws
            Exception{
        return new ProcessedDataHistoricRepository.
                GetProcessedDataHistoricSamplesSyncBySessionIdAsyncTask(processedDataHistoricDao).
                execute(sessionId).get();
    }
}






















