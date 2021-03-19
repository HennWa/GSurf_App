package com.example.gsurfexample.source.local.historic;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.gsurfexample.source.local.historic.SurfSession;
import com.example.gsurfexample.source.local.historic.SurfSessionDao;
import com.example.gsurfexample.source.local.historic.SurfSessionDataBase;

import java.util.List;

public class SurfSessionRepository {

    private SurfSessionDao surfSessionDao;
    private LiveData<List<SurfSession>> allSessions;

    public SurfSessionRepository(Application application){
        SurfSessionDataBase database = SurfSessionDataBase.getInstance(application);
        surfSessionDao = database.surfSessionDao();
        allSessions = surfSessionDao.getAllNotes();
    }

    public void insert(SurfSession surfSession){
        new InsertSurfSessionAsyncTask(surfSessionDao).execute(surfSession);
    }

    public void update(SurfSession surfSession){
        new UpdateSurfSessionAsyncTask(surfSessionDao).execute(surfSession);
    }

    public void delete(SurfSession surfSession){
        new DeleteSurfSessionAsyncTask(surfSessionDao).execute(surfSession);
    }

    public void deleteAllSurfSessions(){
        new DeleteAllSurfSessionsAsyncTask(surfSessionDao).execute();
    }

    public LiveData<List<SurfSession>> getAllSurfSessions() {
        return allSessions;
    }


    private static class InsertSurfSessionAsyncTask extends AsyncTask<SurfSession, Void, Void> {
        private SurfSessionDao surfSessionDao;

        private InsertSurfSessionAsyncTask(SurfSessionDao noteDao){
            this.surfSessionDao = noteDao;
        }

        @Override
        protected Void doInBackground(SurfSession... surfSessions){
            surfSessionDao.insert(surfSessions[0]);
            return null;
        }
    }

    private static class UpdateSurfSessionAsyncTask extends AsyncTask<SurfSession, Void, Void> {
        private SurfSessionDao surfSessionDao;

        private UpdateSurfSessionAsyncTask(SurfSessionDao surfSessionDao){
            this.surfSessionDao = surfSessionDao;
        }

        @Override
        protected Void doInBackground(SurfSession... surfSessions){
            surfSessionDao.update(surfSessions[0]);
            return null;
        }
    }

    private static class DeleteSurfSessionAsyncTask extends AsyncTask<SurfSession, Void, Void> {
        private SurfSessionDao surfSessionDao;

        private DeleteSurfSessionAsyncTask(SurfSessionDao surfSessionDao){
            this.surfSessionDao = surfSessionDao;
        }

        @Override
        protected Void doInBackground(SurfSession... surfSessions){
            surfSessionDao.delete(surfSessions[0]);
            return null;
        }
    }

    private static class DeleteAllSurfSessionsAsyncTask extends AsyncTask<Void, Void, Void> {
        private SurfSessionDao surfSessionDao;

        private DeleteAllSurfSessionsAsyncTask(SurfSessionDao surfSessionDao){
            this.surfSessionDao = surfSessionDao;
        }

        @Override
        protected Void doInBackground(Void... voids){
            surfSessionDao.deleteAllNotes();
            return null;
        }
    }
}






















