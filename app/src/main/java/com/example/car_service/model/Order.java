package com.example.car_service.model;

import java.util.List;

public class Order {
    private long id;
    private double totalAmount;
    private String status;
    private String createdAt;
    private List<OrderItem> items;

    public long getId() { return id; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}



