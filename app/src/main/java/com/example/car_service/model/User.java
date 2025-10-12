package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Thêm import này

public class User implements Serializable { // Thêm "implements Serializable"

    @SerializedName("id")
    private Long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("role")
    private String role;

    // Thêm các trường mới
    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    // Getters and setters cho tất cả các trường...
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
}