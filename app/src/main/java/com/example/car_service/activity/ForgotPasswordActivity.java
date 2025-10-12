package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail;
    private Button buttonSendRequest;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSendRequest = findViewById(R.id.buttonSendRequest);
        apiService = ApiClient.getApiService();

        buttonSendRequest.setOnClickListener(v -> sendForgotPasswordRequest());
    }

    private void sendForgotPasswordRequest() {
        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("email", email);

        apiService.forgotPassword(emailMap).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Yêu cầu đã được gửi. Vui lòng kiểm tra email của bạn!", Toast.LENGTH_LONG).show();
                    // Chuyển sang màn hình ResetPassword
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                    finish(); // Đóng màn hình này
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại trong hệ thống.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}