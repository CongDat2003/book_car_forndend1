package com.example.car_service.api;

import com.example.car_service.model.Appointment;
import com.example.car_service.model.Brand;
import com.example.car_service.model.VehicleModel;
import com.example.car_service.model.Product;
import com.example.car_service.model.AppointmentRequest;
import com.example.car_service.model.AuthResponse;
import com.example.car_service.model.District;
import com.example.car_service.model.LoginDto;
import com.example.car_service.model.Province;
import com.example.car_service.model.RegisterDto;
import com.example.car_service.model.ResetPasswordDto;
import com.example.car_service.model.Service;
import com.example.car_service.model.UpdateUserDto;
import com.example.car_service.model.User;
import com.example.car_service.model.Vehicle;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // === API Xác thực ===
    @POST("api/auth/register")
    Call<User> registerUser(@Body RegisterDto registerDto);

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginDto loginDto);

    @POST("api/auth/forgot-password")
    Call<Void> forgotPassword(@Body Map<String, String> emailMap);

    @POST("api/auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordDto resetDto);

    @POST("api/auth/google")
    Call<AuthResponse> loginWithGoogle(@Body Map<String, String> tokenMap);

    // === API Người dùng ===
    @GET("api/users/{id}")
    Call<User> getUserDetails(@Header("Authorization") String token, @Path("id") long userId);

    @PUT("api/users/{id}")
    Call<User> updateUserDetails(@Header("Authorization") String token, @Path("id") long userId, @Body UpdateUserDto user);

    // === API Địa điểm ===
    @GET("api/locations/provinces")
    Call<List<Province>> getProvinces(@Header("Authorization") String token);

    @GET("api/locations/provinces/{provinceId}/districts")
    Call<List<District>> getDistricts(@Header("Authorization") String token, @Path("provinceId") int provinceId);

    // === API Dịch vụ ===
    @GET("api/catalog/services")
    Call<List<Service>> getServices();

    // === API Xe ===
    @GET("api/users/{userId}/vehicles")
    Call<List<Vehicle>> getUserVehicles(@Header("Authorization") String token, @Path("userId") long userId);

    @POST("api/vehicles")
    Call<Vehicle> addVehicle(@Header("Authorization") String token, @Body Vehicle vehicle);

    // === API Thương hiệu & Mẫu xe (không yêu cầu token) ===
    @GET("api/catalog/brands")
    Call<List<Brand>> getBrands();

    // Trả về models theo brandId
    @GET("api/catalog/brands/{brandId}/models")
    Call<List<VehicleModel>> getVehicleModelsByBrand(@Path("brandId") Integer brandId);

    // === API Sản phẩm theo dịch vụ ===
    @GET("api/catalog/services/{serviceId}/products")
    Call<List<Product>> getProductsByService(@Path("serviceId") long serviceId);

    // === API Lịch rảnh (khung giờ) ===
    @GET("api/appointments/available-slots/{date}")
    Call<List<String>> getAvailableSlots(@Path("date") String dateIso);

    // === API Đặt lịch ===
    @POST("api/appointments")
    Call<Appointment> createAppointment(@Header("Authorization") String token, @Body AppointmentRequest appointmentRequest);

    @GET("api/users/{userId}/appointments")
    Call<List<Appointment>> getAppointmentHistory(@Header("Authorization") String token, @Path("userId") long userId);

    // === API Đơn hàng ===
    @GET("api/orders/my")
    Call<List<com.example.car_service.model.Order>> getMyOrders(@Header("Authorization") String token);

    @POST("api/orders")
    Call<com.example.car_service.model.Order> createOrder(@Header("Authorization") String token, @Body com.example.car_service.model.CreateOrderRequest request);
}