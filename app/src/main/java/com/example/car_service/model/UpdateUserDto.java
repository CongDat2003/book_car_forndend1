package com.example.car_service.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này dùng để đóng gói các thông tin mà người dùng muốn cập nhật
 * và gửi lên server.
 */
public class UpdateUserDto {

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("dateOfBirth")
    private String dateOfBirth; // Gửi dạng String "YYYY-MM-DD"

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    public UpdateUserDto(String fullName, String phoneNumber, String dateOfBirth, String gender, String address) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}