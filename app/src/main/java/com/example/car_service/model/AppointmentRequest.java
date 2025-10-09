package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class AppointmentRequest {
    @SerializedName("vehicleId")
    private long vehicleId;

    @SerializedName("serviceId")
    private long serviceId;

    @SerializedName("appointmentDate")
    private String appointmentDate; // Gửi đi dạng String theo format "yyyy-MM-dd'T'HH:mm:ss"

    @SerializedName("notes")
    private String notes;

    public AppointmentRequest(long vehicleId, long serviceId, String appointmentDate, String notes) {
        this.vehicleId = vehicleId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.notes = notes;
    }

    // Getters and Setters
    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }
    public long getServiceId() { return serviceId; }
    public void setServiceId(long serviceId) { this.serviceId = serviceId; }
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}