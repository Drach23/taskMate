package com.example.taskmate.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(TaskModel task);

    @Update
    void update(TaskModel task);

    @Delete
    void delete(TaskModel task);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskModel getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE collectionId = :collectionId ORDER BY dueDate ASC")
    LiveData<List<TaskModel>> getTasksForCollection(int collectionId);

}
