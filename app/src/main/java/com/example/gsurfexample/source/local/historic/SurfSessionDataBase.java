package com.example.gsurfexample.source.local.historic;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {SurfSession.class}, version =1)
public abstract class SurfSessionDataBase extends RoomDatabase {

    private static SurfSessionDataBase instance;

    public abstract SurfSessionDao surfSessionDao();

    public static synchronized SurfSessionDataBase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SurfSessionDataBase.class, "session_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private SurfSessionDao surfSessionDao;

        private PopulateDbAsyncTask(SurfSessionDataBase db){
            surfSessionDao = db.surfSessionDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            surfSessionDao.insert(new SurfSession("0", "My Best Session", "Eisbachwelle", "2020/04/02 14:35"));
            surfSessionDao.insert(new SurfSession("0", "My Best Session Ever", "Ruhrpott", "2020/04/02 14:35"));
            surfSessionDao.insert(new SurfSession("0", "Pipeline Rocks", "Owahu, Hawaii", "2020/04/02 14:35"));
            return null;
        }
    }
}
