package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class Brand {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}