package com.example.car_service.model;

public class ResetPasswordDto {
    private String token;
    private String newPassword;

    // Constructor để dễ dàng tạo đối tượng
    public ResetPasswordDto(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    // Getters (không bắt buộc nhưng nên có)
    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}