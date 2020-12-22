package com.example.gsurfexample;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TimeSampleViewModel extends AndroidViewModel {

    private TimeSampleRepository repository;
    private LiveData<List<TimeSample>> allTimeSamples;
    private LiveData<TimeSample> lastTimeSample;

    public TimeSampleViewModel(@NonNull Application application) {
        super(application);
        repository = new TimeSampleRepository(application);
        allTimeSamples = repository.getAllTimeSamples();
        lastTimeSample = repository.getLastTimeSamples();
    }

    public void sensorDataFetch(){ repository.sensorDataFetch(); }

    public void stopSensorDataFetch(){ repository.stopSensorDataFetch(); }

    public void insert(TimeSample timeSample){ repository.insert(timeSample); }

    public void update(TimeSample timeSample){ repository.update(timeSample); }

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
}
