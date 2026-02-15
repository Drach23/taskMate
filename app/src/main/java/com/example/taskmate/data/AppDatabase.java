package com.example.taskmate.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CollectionModel.class,TaskModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CollectionDao collectionDao();


}
