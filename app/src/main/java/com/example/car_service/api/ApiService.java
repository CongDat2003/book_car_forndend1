package com.example.car_service.api;

import com.example.car_service.model.Appointment;
import com.example.car_service.model.AppointmentRequest;
import com.example.car_service.model.AuthResponse;
import com.example.car_service.model.LoginDto;
import com.example.car_service.model.RegisterDto;
import com.example.car_service.model.Service;
import com.example.car_service.model.User;
import com.example.car_service.model.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // === API Xác thực (Không cần token) ===
    @POST("api/auth/register")
    Call<User> registerUser(@Body RegisterDto registerDto);

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginDto loginDto);

    // === API Dịch vụ (Cần token) ===
    @GET("api/services")
    Call<List<Service>> getServices(@Header("Authorization") String token);

    // === API Xe (Cần token) ===
    @GET("api/users/{userId}/vehicles")
    Call<List<Vehicle>> getUserVehicles(@Header("Authorization") String token, @Path("userId") long userId);

    @POST("api/vehicles")
    Call<Vehicle> addVehicle(@Header("Authorization") String token, @Body Vehicle vehicle);

    // === API Đặt lịch (Cần token) ===
    @POST("api/appointments")
    Call<Appointment> createAppointment(@Header("Authorization") String token, @Body AppointmentRequest appointmentRequest);

    @GET("api/users/{userId}/appointments")
    Call<List<Appointment>> getAppointmentHistory(@Header("Authorization") String token, @Path("userId") long userId);
}