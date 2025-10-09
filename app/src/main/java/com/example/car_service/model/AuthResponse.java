// trong file AuthResponse.java
package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    // Thêm dòng này
    @SerializedName("userId")
    private long userId;

    public String getToken() {
        return token;
    }

    // Thêm hàm này
    public long getUserId() {
        return userId;
    }
}