package com.example.car_service.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.car_service.R;
import com.example.car_service.activity.EditProfileActivity;
import com.example.car_service.activity.LoginActivity;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private TextView textViewUserNameHeader, textViewFullName, textViewPhoneNumber,
            textViewDob, textViewGender, textViewAddress, textViewEmail, textEditPersonalInfo, textEditContactInfo;
    private Button buttonLogout;
    private ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;

    private User currentUser;
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        textViewUserNameHeader = view.findViewById(R.id.textViewUserNameHeader);
        textViewFullName = view.findViewById(R.id.textViewFullName);
        textViewPhoneNumber = view.findViewById(R.id.textViewPhoneNumber);
        textViewDob = view.findViewById(R.id.textViewDob);
        textViewGender = view.findViewById(R.id.textViewGender);
        textViewAddress = view.findViewById(R.id.textViewAddress);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textEditPersonalInfo = view.findViewById(R.id.textEditPersonalInfo);
        textEditContactInfo = view.findViewById(R.id.textEditContactInfo);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchUserDetails();
                    }
                });

        // Hiển thị thông tin từ SharedPreferences trước, sau đó gọi API để cập nhật
        displayUserInfoFromPrefs();
        fetchUserDetails();

        buttonLogout.setOnClickListener(v -> logoutUser());
        
        // Xử lý click cho nút "Chỉnh sửa" thông tin cá nhân
        textEditPersonalInfo.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                intent.putExtra("USER_DATA", currentUser);
                editProfileLauncher.launch(intent);
            } else {
                Toast.makeText(requireContext(), "Đang tải dữ liệu, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Xử lý click cho nút "Chỉnh sửa" thông tin liên hệ (cùng chức năng)
        textEditContactInfo.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                intent.putExtra("USER_DATA", currentUser);
                editProfileLauncher.launch(intent);
            } else {
                Toast.makeText(requireContext(), "Đang tải dữ liệu, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserInfoFromPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        String fullName = prefs.getString("user_full_name", null);
        String email = prefs.getString("user_email", null);
        String phone = prefs.getString("user_phone", null);
        String dob = prefs.getString("user_dob", null);
        String gender = prefs.getString("user_gender", null);
        String address = prefs.getString("user_address", null);

        if (fullName != null && !fullName.isEmpty()) {
            textViewUserNameHeader.setText(fullName);
            textViewFullName.setText("Họ & tên: " + fullName);
        }
        
        if (phone != null && !phone.isEmpty()) {
            textViewPhoneNumber.setText("Số di động: " + phone);
        }
        
        if (email != null && !email.isEmpty()) {
            textViewEmail.setText("Email: " + email);
        }
        
        if (dob != null && !dob.isEmpty()) {
            textViewDob.setText("Ngày sinh: " + dob);
        }
        
        if (gender != null && !gender.isEmpty()) {
            textViewGender.setText("Giới tính: " + gender);
        }
        
        if (address != null && !address.isEmpty()) {
            textViewAddress.setText("Địa chỉ: " + address);
        }
    }

    private void fetchUserDetails() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
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
                    currentUser = response.body();
                    populateUI(currentUser);
                    // Lưu thông tin mới vào SharedPreferences
                    saveUserInfoToPrefs(currentUser);
                } else {
                    // Nếu API thất bại, vẫn giữ thông tin từ SharedPreferences
                    Toast.makeText(requireContext(), "Không thể tải thông tin mới từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Nếu API thất bại, vẫn giữ thông tin từ SharedPreferences
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfoToPrefs(User user) {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        if (user.getFullName() != null) {
            editor.putString("user_full_name", user.getFullName());
        }
        if (user.getEmail() != null) {
            editor.putString("user_email", user.getEmail());
        }
        if (user.getPhoneNumber() != null) {
            editor.putString("user_phone", user.getPhoneNumber());
        }
        if (user.getDateOfBirth() != null) {
            editor.putString("user_dob", user.getDateOfBirth());
        }
        if (user.getGender() != null) {
            editor.putString("user_gender", user.getGender());
        }
        if (user.getAddress() != null) {
            editor.putString("user_address", user.getAddress());
        }
        
        editor.apply();
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
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}