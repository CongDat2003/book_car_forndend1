package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class District implements Serializable, Selectable { // Thêm ", Selectable"
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // Triển khai các phương thức của Selectable
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return name;
    }
}