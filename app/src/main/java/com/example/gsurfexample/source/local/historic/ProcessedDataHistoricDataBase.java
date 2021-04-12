package com.example.gsurfexample.source.local.historic;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import java.util.UUID;

@Database(entities = {ProcessedDataHistoric.class}, version =2)
public abstract class ProcessedDataHistoricDataBase extends RoomDatabase {

    // Attributes
    private static ProcessedDataHistoricDataBase instance;
    public abstract ProcessedDataHistoricDao processedDataHistoricDao();


    // Nested classes
    private static class PopulateProcessedDataHistoricDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private ProcessedDataHistoricDao processedDataHistoricDao;

        private PopulateProcessedDataHistoricDbAsyncTask(ProcessedDataHistoricDataBase db){
            processedDataHistoricDao = db.processedDataHistoricDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            processedDataHistoricDao.insert(new ProcessedDataHistoric( UUID.randomUUID().toString(),
                    0, 0f, 0f,
                    0f, 0f, 0f, 0f, 0f, 0f, 0f,0f, 0f,
                    0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
                    0));
            return null;
        }
    }

    // Methods
    public static synchronized ProcessedDataHistoricDataBase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ProcessedDataHistoricDataBase.class, "processed_data_historic_database")
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
            new PopulateProcessedDataHistoricDbAsyncTask(instance).execute();
        }
    };
}
