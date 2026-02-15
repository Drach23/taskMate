package com.example.taskmate.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CollectionDao {

    @Insert
    void insert(CollectionModel collection);

    @Delete
    void delete(CollectionModel collection);

    @Update
    void update(CollectionModel collection); // <-- método para actualizar

    @Query("SELECT * FROM collections ORDER BY id DESC")
    LiveData<List<CollectionModel>> getAllCollections();

    @Query("SELECT * FROM collections WHERE id = :id")
    LiveData<CollectionModel> getCollection(int id);

}
