package com.example.gsurfexample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gsurfexample.source.local.historic.ProcessedDataHistoric;
import com.example.gsurfexample.source.local.historic.ProcessedDataHistoricRepository;
import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.ProcessedDataRepository;

import java.util.List;

public class ProcessedDataHistoricViewModel extends AndroidViewModel {

    // Attributes
    private ProcessedDataHistoricRepository repository;

    // Constructor
    public ProcessedDataHistoricViewModel(@NonNull Application application) {
        super(application);
        repository = new ProcessedDataHistoricRepository(application);
    }

    public void insert(ProcessedDataHistoric processedDataHistoric){
        repository.insert(processedDataHistoric);
    }

    public void update(ProcessedDataHistoric processedDataHistoric){
        repository.update(processedDataHistoric);
    }

    public void delete(ProcessedDataHistoric processedDataHistoric){
        repository.delete(processedDataHistoric);
    }

    public void deleteAllProcessedData(){
        repository.deleteAllProcessedDataHistoric();
    }

    public List<ProcessedDataHistoric> getAllProcessedDataHistoricSync() throws Exception{
        return repository.getAllProcessedDataHistoricSync();
    }

    public List<ProcessedDataHistoric> getProcessedDataHistoricSyncBySessionsId(String sessionID)
            throws Exception{
        return repository.getProcessedDataHistoricSyncBySessionsId(sessionID);
    }

}
