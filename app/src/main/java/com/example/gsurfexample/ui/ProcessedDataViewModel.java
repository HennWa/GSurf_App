package com.example.gsurfexample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.ProcessedDataRepository;

import java.util.List;

public class ProcessedDataViewModel extends AndroidViewModel {

    // Attributes
    private final ProcessedDataRepository repository;
    private final LiveData<ProcessedData> lastProcessedDataSample;
    private final LiveData<List<ProcessedData>> allProcessedData;

    // Constructor
    public ProcessedDataViewModel(@NonNull Application application) {
        super(application);
        repository = new ProcessedDataRepository(application);
        lastProcessedDataSample = repository.getLastProcessedDataSample();
        allProcessedData = repository.getAllProcessedData();
    }

    // Methods
    public void sensorDataFetch(){
        repository.sensorDataFetch();
    }

    public void stopSensorDataFetch(){
        repository.stopSensorDataFetch();
    }

    public void insert(ProcessedData processedData){
        repository.insert(processedData);
    }

    public void update(ProcessedData processedData){
        repository.update(processedData);
    }

    public void delete(ProcessedData processedData){
        repository.delete(processedData);
    }

    public void deleteAllProcessedData(){
        repository.deleteAllProcessedData();
    }

    public LiveData<List<ProcessedData>> getAllProcessedData(){
        return allProcessedData;
    }

    public List<ProcessedData> getAllProcessedDataSync() throws Exception{
        return repository.getAllProcessedDataSync();
    }

    //public LiveData<ProcessedData> getLastProcessedDataSample(){
    //    return lastProcessedDataSample;
    //}

}
