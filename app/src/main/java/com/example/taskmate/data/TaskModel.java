package com.example.taskmate.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private long dueDate;
    private boolean isCompleted;
    private int collectionId;

    public TaskModel(String title,
                     String description,
                     long dueDate,
                     boolean isCompleted,
                     int collectionId) {

        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.collectionId = collectionId;
    }

    // ===== ID =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ===== TITLE =====
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ===== DESCRIPTION =====
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ===== DUE DATE =====
    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    // ===== COMPLETED =====
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // ===== COLLECTION ID =====
    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }
}
