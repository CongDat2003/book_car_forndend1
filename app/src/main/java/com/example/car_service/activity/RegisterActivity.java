package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.RegisterDto;
import com.example.car_service.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout layoutFullName, layoutEmail, layoutPhoneNumber, layoutPassword, layoutConfirmPassword;
    private TextInputEditText editTextFullName, editTextEmail, editTextPhoneNumber, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getApiService();

        // Ánh xạ views
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhoneNumber = findViewById(R.id.layoutPhoneNumber);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(v -> registerUser());
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // --- CÁC HÀM VALIDATE ---
    private boolean validateFullName() {
        String fullNameInput = layoutFullName.getEditText().getText().toString().trim();
        if (fullNameInput.isEmpty()) {
            layoutFullName.setError("Họ tên không được để trống");
            return false;
        } else {
            layoutFullName.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = layoutEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            layoutEmail.setError("Email không được để trống");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            layoutEmail.setError("Vui lòng nhập email hợp lệ");
            return false;
        } else {
            layoutEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = layoutPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            layoutPassword.setError("Mật khẩu không được để trống");
            return false;
        } else if (passwordInput.length() < 6) {
            layoutPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        } else {
            layoutPassword.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordInput = layoutPassword.getEditText().getText().toString().trim();
        String confirmPasswordInput = layoutConfirmPassword.getEditText().getText().toString().trim();
        if (confirmPasswordInput.isEmpty()) {
            layoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            return false;
        } else if (!passwordInput.equals(confirmPasswordInput)) {
            layoutConfirmPassword.setError("Mật khẩu không trùng khớp");
            return false;
        } else {
            layoutConfirmPassword.setError(null);
            return true;
        }
    }

    private void registerUser() {
        // Chạy tất cả các hàm validate. Dấu | đảm bảo tất cả các hàm đều được chạy để hiển thị lỗi.
        if (!validateFullName() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
            return;
        }

        String fullName = layoutFullName.getEditText().getText().toString().trim();
        String email = layoutEmail.getEditText().getText().toString().trim();
        String phoneNumber = layoutPhoneNumber.getEditText().getText().toString().trim();
        String password = layoutPassword.getEditText().getText().toString().trim();

        RegisterDto registerDto = new RegisterDto(fullName, email, password, phoneNumber);

        // Gọi API
        apiService.registerUser(registerDto).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Email hoặc SĐT có thể đã được sử dụng.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}