package com.example.taskmate.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "collections")
public class CollectionModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private int quantity;

    public CollectionModel(String title, int quantity){
        this.title = title;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public int getQuantity() { return quantity; }
}

