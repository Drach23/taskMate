package com.example.taskmate.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CollectionDao {

    @Insert
    void insert(CollectionModel collection);

    @Delete
    void delete(CollectionModel collection);

    @Query("SELECT * FROM collections ORDER BY id DESC")
    LiveData<List<CollectionModel>> getAllCollections();

}
