package com.example.gsurfexample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.gsurfexample.source.local.historic.SurfSession;
import com.example.gsurfexample.source.local.historic.SurfSessionRepository;

import java.util.List;

public class SurfSessionViewModel extends AndroidViewModel {

    private SurfSessionRepository repository;
    private LiveData<List<SurfSession>> allSurfSessions;

    public SurfSessionViewModel(@NonNull Application application) {
        super(application);
        repository = new SurfSessionRepository(application);
        allSurfSessions = repository.getAllSurfSessions();
    }

    public void insert(SurfSession surfSession){
        repository.insert(surfSession);
    }

    public void update(SurfSession surfSession){
        repository.update(surfSession);
    }

    public void delete(SurfSession surfSession){
        repository.delete(surfSession);
    }

    public void deleteAllSurfSessions(){
        repository.deleteAllSurfSessions();
    }

    public LiveData<List<SurfSession>> getAllNotes(){
        return allSurfSessions;
    }
}
