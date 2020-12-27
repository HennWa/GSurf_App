package com.example.gsurfexample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimeSampleDao {

    @Insert
    void insert(TimeSample ts);

    @Update
    void update(TimeSample ts);

    @Delete
    void delete(TimeSample ts);

    @Query("DELETE FROM timesample_table")
    void deleteAllTimeSamples();

    @Query("SELECT * FROM timesample_table")
    LiveData<List<TimeSample>> getAllTimeSamples();

    @Query("SELECT * FROM timesample_table ORDER BY id DESC LIMIT 1")
    LiveData<TimeSample> getLastTimeSamples();

    @Query("SELECT * FROM timesample_table WHERE id = :id")
    TimeSample getTimeSamplesByID(int id);
}
