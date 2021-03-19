package com.example.gsurfexample.source.local.historic;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gsurfexample.source.local.historic.SurfSession;

import java.util.List;

@Dao
public interface SurfSessionDao {

    @Insert
    void insert(SurfSession surfSession);

    @Update
    void update(SurfSession surfSession);

    @Delete
    void delete(SurfSession surfSession);

    @Query("DELETE FROM session_table")
    void deleteAllNotes();

    @Query("SELECT * FROM session_table ORDER BY priority DESC")
    LiveData<List<SurfSession>> getAllNotes();

}
