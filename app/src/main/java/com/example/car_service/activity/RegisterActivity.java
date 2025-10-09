package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.RegisterDto;
import com.example.car_service.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPhoneNumber, editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Ánh xạ các thành phần từ layout XML vào biến Java
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Thiết lập sự kiện click cho nút Đăng ký
        buttonRegister.setOnClickListener(v -> registerUser());

        // Thiết lập sự kiện click cho textview "Đăng nhập" để quay lại màn hình Login
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        // Lấy dữ liệu người dùng nhập
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào đơn giản
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng DTO để gửi đi
        RegisterDto registerDto = new RegisterDto(fullName, email, password, phoneNumber);

        // Gọi API đăng ký
        Call<User> call = apiService.registerUser(registerDto);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                // Xử lý kết quả trả về từ server
                if (response.isSuccessful() && response.body() != null) {
                    // Đăng ký thành công
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển hướng người dùng đến màn hình Đăng nhập
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Đóng màn hình đăng ký để người dùng không quay lại được
                } else {
                    // Đăng ký thất bại (ví dụ: email đã tồn tại)
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Email có thể đã được sử dụng.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Xử lý lỗi kết nối mạng
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RegisterError", "onFailure: " + t.getMessage());
            }
        });
    }
}