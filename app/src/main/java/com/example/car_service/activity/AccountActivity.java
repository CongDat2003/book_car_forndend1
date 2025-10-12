package com.example.car_service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    private TextView textViewUserNameHeader, textViewFullName, textViewPhoneNumber,
            textViewDob, textViewGender, textViewAddress, textViewEmail, textEditPersonalInfo;
    private Button buttonLogout;
    private ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;

    private User currentUser; // Lưu thông tin user hiện tại
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        apiService = ApiClient.getApiService();

        // Cấu hình Google Client để đăng xuất
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Ánh xạ views
        textViewUserNameHeader = findViewById(R.id.textViewUserNameHeader);
        textViewFullName = findViewById(R.id.textViewFullName);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        textViewDob = findViewById(R.id.textViewDob);
        textViewGender = findViewById(R.id.textViewGender);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewEmail = findViewById(R.id.textViewEmail);
        textEditPersonalInfo = findViewById(R.id.textEditPersonalInfo); // Giả sử nút "Chỉnh sửa" có ID này
        buttonLogout = findViewById(R.id.buttonLogout);

        // Đăng ký để nhận kết quả trả về từ EditProfileActivity
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Nếu chỉnh sửa thành công, tải lại thông tin
                        fetchUserDetails();
                    }
                });

        // Tải thông tin người dùng từ server
        fetchUserDetails();

        // Xử lý sự kiện click
        buttonLogout.setOnClickListener(v -> logoutUser());
        textEditPersonalInfo.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(AccountActivity.this, EditProfileActivity.class);
                // Gửi toàn bộ thông tin user hiện tại sang màn hình EditProfile
                intent.putExtra("USER_DATA", currentUser);
                editProfileLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Đang tải dữ liệu, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        String token = prefs.getString("jwt_token", null);

        if (userId == -1 || token == null) {
            redirectToLogin();
            return;
        }

        apiService.getUserDetails(token, userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body(); // Lưu lại thông tin mới nhất
                    populateUI(currentUser); // Đổ dữ liệu lên giao diện
                } else {
                    Toast.makeText(AccountActivity.this, "Không thể tải thông tin cá nhân", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(AccountActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI(User user) {
        textViewUserNameHeader.setText(user.getFullName());
        textViewFullName.setText("Họ & tên: " + user.getFullName());
        textViewPhoneNumber.setText("Số di động: " + user.getPhoneNumber());
        textViewEmail.setText("Email: " + user.getEmail());
        textViewDob.setText("Ngày sinh: " + (user.getDateOfBirth() != null ? user.getDateOfBirth() : "Chưa cập nhật"));
        textViewGender.setText("Giới tính: " + (user.getGender() != null ? user.getGender() : "Chưa cập nhật"));
        textViewAddress.setText("Địa chỉ: " + (user.getAddress() != null ? user.getAddress() : "Chưa cập nhật"));
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(AccountActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}