package com.example.car_service.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // LƯU Ý QUAN TRỌNG: DÙNG IP 10.0.2.2 THAY CHO LOCALHOST
    // Đây là địa chỉ IP đặc biệt mà máy ảo Android dùng để truy cập localhost của máy tính.
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}