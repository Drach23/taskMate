package com.example.taskmate.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "collections")
public class CollectionModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private int quantity;

    private String color;

    public CollectionModel(String title, int quantity, String color){
        this.title = title;
        this.quantity = quantity;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

