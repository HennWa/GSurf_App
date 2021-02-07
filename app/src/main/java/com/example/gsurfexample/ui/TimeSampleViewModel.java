package com.example.gsurfexample.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.gsurfexample.source.local.live.ProcessedData;
import com.example.gsurfexample.source.local.live.TimeSample;
import com.example.gsurfexample.source.local.live.TimeSampleRepository;

import java.util.List;

public class TimeSampleViewModel extends AndroidViewModel {

    // Attributes
    private TimeSampleRepository repository;
    private LiveData<List<TimeSample>> allTimeSamples;
    private LiveData<TimeSample> lastTimeSample;
    private LiveData<List<ProcessedData>> allProcessedData;

    // Constructor
    public TimeSampleViewModel(@NonNull Application application) {
        super(application);
        repository = new TimeSampleRepository(application);
        allTimeSamples = repository.getAllTimeSamples();
        lastTimeSample = repository.getLastTimeSamples();
        allProcessedData = repository.getAllProcessedData();
    }

    // Methods
    // time_sample
    public void sensorDataFetch(){
        repository.sensorDataFetch();
    }

    public void stopSensorDataFetch(){
        repository.stopSensorDataFetch();
    }

    public void insert(TimeSample timeSample){
        repository.insert(timeSample);
    }

    public void update(TimeSample timeSample){
        repository.update(timeSample);
    }

    public void delete(TimeSample timeSample){
        repository.delete(timeSample);
    }

    public void deleteAllTimeSamples(){
        repository.deleteAllTimeSamples();
    }

    public LiveData<List<TimeSample>> getAllTimeSamples(){
        return allTimeSamples;
    }

    public LiveData<TimeSample> getLastTimeSample(){
        return lastTimeSample;
    }

    // processed_data
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

}
