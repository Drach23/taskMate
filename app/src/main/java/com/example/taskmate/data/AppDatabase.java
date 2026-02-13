package com.example.taskmate.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CollectionModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CollectionDao collectionDao();
}
