package com.example.car_service.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private Button buttonNewBooking, buttonViewHistory, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Kiểm tra trạng thái đăng nhập
        if (!isUserLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển đến màn hình Login
            redirectToLogin();
            return; // Dừng thực thi code ở đây
        }

        // Nếu đã đăng nhập, tiếp tục hiển thị giao diện của MainActivity
        setContentView(R.layout.activity_main);

        // Ánh xạ views
        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonNewBooking = findViewById(R.id.buttonNewBooking);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        buttonLogout = findViewById(R.id.buttonLogout);

        // TODO: Lấy tên người dùng và hiển thị lên textViewWelcome

        // Xử lý sự kiện click cho các nút
        buttonNewBooking.setOnClickListener(v -> {
            // Chuyển sang màn hình đặt lịch
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        buttonViewHistory.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình Lịch sử đặt lịch
            Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        buttonLogout.setOnClickListener(v -> {
            // Xử lý đăng xuất
            logoutUser();
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        // Kiểm tra xem token có tồn tại và không rỗng hay không
        return sharedPreferences.getString("jwt_token", null) != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng MainActivity để người dùng không thể quay lại bằng nút back
    }

    private void logoutUser() {
        // Xóa token đã lưu
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");
        editor.apply();

        // Chuyển về màn hình đăng nhập
        redirectToLogin();
    }
}