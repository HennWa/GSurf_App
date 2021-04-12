package com.example.gsurfexample.source.local.historic;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProcessedDataHistoricDao {

    @Insert
    void insert(ProcessedDataHistoric processedDataHistoric);

    @Update
    void update(ProcessedDataHistoric processedDataHistoric);

    @Delete
    void delete(ProcessedDataHistoric processedDataHistoric);

    @Query("DELETE FROM processed_data_historic_table")
    void deleteAllProcessedDataHistoric();

    @Query("SELECT * FROM processed_data_historic_table")
    List<ProcessedDataHistoric> getAllProcessedDataHistoricSync();

    @Query("SELECT * FROM processed_data_historic_table ORDER BY timeStamp DESC LIMIT 1")
    ProcessedDataHistoric getLastProcessedDataHistoricSync();

    @Query("SELECT * FROM processed_data_historic_table WHERE session_id = :session_id")
    List<ProcessedDataHistoric> getProcessedDataHistoricSamplesSyncBySessionId(String session_id);
}
