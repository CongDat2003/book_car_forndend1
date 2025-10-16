package com.example.car_service.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Dùng IP LAN của PC khi test trên điện thoại thật
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    private static Retrofit getClient() { // Giữ phương thức này là private
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // *** TẠO PHƯƠNG THỨC PUBLIC NÀY ĐỂ SỬ DỤNG Ở MỌI NƠI ***
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}