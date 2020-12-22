package com.example.gsurfexample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TimeSample.class, ProcessedData.class}, version =1)
public abstract class TimeSampleDataBase extends RoomDatabase {

    // Attributes
    private static TimeSampleDataBase instance;
    public abstract TimeSampleDao timeSampleDao();
    public abstract ProcessedDataDao processedDataDao();


    // Nested classes
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private TimeSampleDao timeSampleDao;

        private PopulateDbAsyncTask(TimeSampleDataBase db){
            timeSampleDao = db.timeSampleDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            timeSampleDao.insert(new TimeSample(0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
            return null;
        }
    }

    private static class PopulateProcessedDataDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private ProcessedDataDao processedDataDao;

        private PopulateProcessedDataDbAsyncTask(TimeSampleDataBase db){
            processedDataDao = db.processedDataDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            processedDataDao.insert(new ProcessedData(0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0));
            return null;
        }
    }


    // Methods
    public static synchronized TimeSampleDataBase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TimeSampleDataBase.class, "timesample_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static Callback roomCallback = new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
            new PopulateProcessedDataDbAsyncTask(instance).execute();
        }
    };
}
