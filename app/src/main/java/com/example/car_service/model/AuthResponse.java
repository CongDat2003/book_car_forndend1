package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("userId")
    private long userId;

    @SerializedName("fullName") // Thêm trường này
    private String fullName;

    // Getters
    public String getToken() {
        return token;
    }

    public long getUserId() {
        return userId;
    }

    public String getFullName() { // Thêm phương thức này
        return fullName;
    }
}