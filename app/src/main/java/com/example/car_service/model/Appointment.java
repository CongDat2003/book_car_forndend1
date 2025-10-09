package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    @SerializedName("id")
    private long id;

    @SerializedName("appointmentDate")
    private String appointmentDate; // Nhận về dạng String cho đơn giản

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    @SerializedName("user")
    private User user;

    @SerializedName("vehicle")
    private Vehicle vehicle;

    @SerializedName("service")
    private Service service;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}