package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class Service {
    @SerializedName("id")
    private Long id;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("imageUrl")
    private String imageUrl;
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    }