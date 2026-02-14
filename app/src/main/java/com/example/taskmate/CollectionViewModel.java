package com.example.taskmate;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.taskmate.data.AppDatabase;
import com.example.taskmate.data.CollectionModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<CollectionModel>> collections;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CollectionViewModel(@NonNull Application application) {
        super(application);

        db = Room.databaseBuilder(
                application,
                AppDatabase.class,
                "taskmate_database"
        ).build();

        collections = db.collectionDao().getAllCollections();
    }

    public LiveData<List<CollectionModel>> getCollections() {
        return collections;
    }

    public void insert(CollectionModel collection) {
        executor.execute(() -> db.collectionDao().insert(collection));
    }

    public void delete(CollectionModel collection) {
        new Thread(() -> {
            db.collectionDao().delete(collection);
        }).start();
    }

}
