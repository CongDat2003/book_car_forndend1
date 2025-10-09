package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class Vehicle {
    @SerializedName("id")
    private long id;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("licensePlate")
    private String licensePlate;

    @SerializedName("year")
    private int year;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}