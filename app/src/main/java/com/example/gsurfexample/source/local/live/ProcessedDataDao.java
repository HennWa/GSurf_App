package com.example.gsurfexample.source.local.live;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gsurfexample.source.local.live.ProcessedData;

import java.util.List;

@Dao
public interface ProcessedDataDao {

    @Insert
    void insert(ProcessedData processedData);

    @Update
    void update(ProcessedData processedData);

    @Delete
    void delete(ProcessedData processedData);

    @Query("DELETE FROM processed_data_table")
    void deleteAllProcessedData();

    @Query("SELECT * FROM processed_data_table")
    LiveData<List<ProcessedData>> getAllProcessedData();

    @Query("SELECT * FROM processed_data_table ORDER BY id DESC LIMIT 1")
    LiveData<ProcessedData> getLastProcessedData();

    @Query("SELECT * FROM processed_data_table WHERE id = :id")
    ProcessedData getProcessedDataSamplesById(int id);
}