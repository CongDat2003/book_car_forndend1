package com.example.car_service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.fragment.SelectionBottomSheetFragment;
import com.example.car_service.model.District;
import com.example.car_service.model.Province;
import com.example.car_service.model.Selectable;
import com.example.car_service.model.UpdateUserDto;
import com.example.car_service.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements SelectionBottomSheetFragment.OnItemSelectedListener {

    private TextInputEditText editTextFullName, editTextPhoneNumber, editTextDob, editTextGender, editTextEmail;
    private TextView textViewProvince, textViewDistrict;
    private Button buttonSave;
    private ApiService apiService;

    // Lưu lại lựa chọn của người dùng
    private Province selectedProvince;
    private District selectedDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = ApiClient.getApiService();

        // Ánh xạ views
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDob = findViewById(R.id.editTextDob);
        editTextGender = findViewById(R.id.editTextGender);
        textViewProvince = findViewById(R.id.textViewProvince);
        textViewDistrict = findViewById(R.id.textViewDistrict);
        buttonSave = findViewById(R.id.buttonSave);

        // Nhận dữ liệu và đổ vào form
        User currentUser = (User) getIntent().getSerializableExtra("USER_DATA");
        if (currentUser != null) {
            populateForm(currentUser);
        }

        // Thiết lập sự kiện click
        buttonSave.setOnClickListener(v -> saveProfileChanges());
        textViewProvince.setOnClickListener(v -> openProvinceSelection());
        textViewDistrict.setOnClickListener(v -> {
            if (selectedProvince == null) {
                Toast.makeText(this, "Vui lòng chọn Tỉnh/Thành phố trước", Toast.LENGTH_SHORT).show();
            } else {
                openDistrictSelection(selectedProvince.getId());
            }
        });
    }

    private void populateForm(User user) {
        editTextFullName.setText(user.getFullName());
        editTextPhoneNumber.setText(user.getPhoneNumber());
        editTextEmail.setText(user.getEmail());
        editTextDob.setText(user.getDateOfBirth());
        editTextGender.setText(user.getGender());

        // Giả sử địa chỉ lưu dạng "Quận, Tỉnh"
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            String[] addressParts = user.getAddress().split(",");
            if (addressParts.length > 1) {
                textViewDistrict.setText(addressParts[0].trim());
                textViewProvince.setText(addressParts[1].trim());
            } else {
                // Nếu chỉ có tỉnh, hoặc chuỗi không đúng định dạng
                textViewProvince.setText(user.getAddress());
            }
        }
    }

    private void openProvinceSelection() {
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null) return;

        apiService.getProvinces(token).enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(@NonNull Call<List<Province>> call, @NonNull Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Selectable> selectableItems = new ArrayList<>(response.body());
                    SelectionBottomSheetFragment bottomSheet = SelectionBottomSheetFragment.newInstance(selectableItems, "Chọn Tỉnh/Thành phố");
                    bottomSheet.show(getSupportFragmentManager(), "ProvinceSheet");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Province>> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi tải danh sách Tỉnh/Thành", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDistrictSelection(int provinceId) {
        String token = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null) return;

        apiService.getDistricts(token, provinceId).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(@NonNull Call<List<District>> call, @NonNull Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Selectable> selectableItems = new ArrayList<>(response.body());
                    SelectionBottomSheetFragment bottomSheet = SelectionBottomSheetFragment.newInstance(selectableItems, "Chọn Quận/Huyện");
                    bottomSheet.show(getSupportFragmentManager(), "DistrictSheet");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<District>> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi tải danh sách Quận/Huyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(Selectable item) {
        if (item instanceof Province) {
            selectedProvince = (Province) item;
            textViewProvince.setText(selectedProvince.getDisplayName());
            // Xóa lựa chọn huyện cũ và tự động mở danh sách huyện mới
            selectedDistrict = null;
            textViewDistrict.setText("");
            textViewDistrict.setHint("Quận/ Huyện");
            openDistrictSelection(selectedProvince.getId());
        } else if (item instanceof District) {
            selectedDistrict = (District) item;
            textViewDistrict.setText(selectedDistrict.getDisplayName());
        }
    }

    private void saveProfileChanges() {
        // 1. Lấy tất cả dữ liệu mới từ các ô nhập liệu
        String newFullName = editTextFullName.getText().toString().trim();
        String newPhoneNumber = editTextPhoneNumber.getText().toString().trim();
        String newDob = editTextDob.getText().toString().trim();
        String newGender = editTextGender.getText().toString().trim();

        // 2. Tạo chuỗi địa chỉ từ các lựa chọn
        String newAddress = "";
        if (selectedDistrict != null && selectedProvince != null) {
            newAddress = selectedDistrict.getDisplayName() + ", " + selectedProvince.getDisplayName();
        }

        // 3. Tạo đối tượng DTO để gửi đi
        UpdateUserDto updateUserDto = new UpdateUserDto(newFullName, newPhoneNumber, newDob, newGender, newAddress);

        // 4. Lấy thông tin xác thực
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        String token = prefs.getString("jwt_token", null);

        if (userId == -1 || token == null) {
            Toast.makeText(this, "Lỗi xác thực, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Gọi API để cập nhật
        apiService.updateUserDetails(token, userId, updateUserDto).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // Báo cho màn hình trước (AccountActivity) biết là đã thành công
                    setResult(Activity.RESULT_OK);
                    // Đóng màn hình này
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}