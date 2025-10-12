package com.example.car_service.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Appointment;
import com.example.car_service.model.AppointmentRequest;
import com.example.car_service.model.Service;
import com.example.car_service.model.Vehicle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private static final String TAG = "BookingActivity";
    private Spinner spinnerVehicle, spinnerService;
    private TextView textViewSelectedDate, textViewSelectedTime;
    private EditText editTextNotes;
    private Button buttonConfirmBooking;

    private ApiService apiService;
    private String authToken;
    private long userId;
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Service> serviceList = new ArrayList<>();
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Ánh xạ views
        spinnerVehicle = findViewById(R.id.spinnerVehicle);
        spinnerService = findViewById(R.id.spinnerService);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewSelectedTime = findViewById(R.id.textViewSelectedTime);
        editTextNotes = findViewById(R.id.editTextNotes);
        buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);

        // Khởi tạo ApiService
        // Dòng code đúng
        apiService = ApiClient.getApiService();

        // Lấy token và user ID đã lưu
        loadAuthInfo();

        // Thiết lập sự kiện click
        textViewSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        textViewSelectedTime.setOnClickListener(v -> showTimePickerDialog());
        buttonConfirmBooking.setOnClickListener(v -> confirmBooking());

        // Tải dữ liệu cho spinners
        if (authToken != null) {
            fetchUserVehicles();
            fetchServices();
        } else {
            Toast.makeText(this, "Lỗi xác thực, vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            // TODO: Chuyển về màn hình Login
        }
    }

    private void loadAuthInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = sharedPreferences.getString("jwt_token", null);
        userId = sharedPreferences.getLong("user_id", -1);
    }

    private void fetchUserVehicles() {
        apiService.getUserVehicles(authToken, userId).enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vehicle>> call, @NonNull Response<List<Vehicle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleList = response.body();
                    // Hiển thị biển số xe lên spinner
                    List<String> vehiclePlates = vehicleList.stream()
                            .map(Vehicle::getLicensePlate)
                            .collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this,
                            android.R.layout.simple_spinner_item, vehiclePlates);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerVehicle.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Vehicle>> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchUserVehicles failed: " + t.getMessage());
            }
        });
    }

    private void fetchServices() {
        apiService.getServices(authToken).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(@NonNull Call<List<Service>> call, @NonNull Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceList = response.body();
                    // Hiển thị tên dịch vụ lên spinner
                    List<String> serviceNames = serviceList.stream()
                            .map(Service::getServiceName)
                            .collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this,
                            android.R.layout.simple_spinner_item, serviceNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerService.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Service>> call, @NonNull Throwable t) {
                Log.e(TAG, "fetchServices failed: " + t.getMessage());
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // Không cho chọn ngày quá khứ
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateTimeLabel();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true); // 24-hour format
        timePickerDialog.show();
    }

    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        textViewSelectedDate.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    private void updateTimeLabel() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        textViewSelectedTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void confirmBooking() {
        // Lấy thông tin đã chọn
        int selectedVehiclePos = spinnerVehicle.getSelectedItemPosition();
        int selectedServicePos = spinnerService.getSelectedItemPosition();
        String notes = editTextNotes.getText().toString().trim();

        if (vehicleList.isEmpty() || serviceList.isEmpty() || textViewSelectedDate.getText().toString().isEmpty() || textViewSelectedTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        long vehicleId = vehicleList.get(selectedVehiclePos).getId();
        long serviceId = serviceList.get(selectedServicePos).getId();

        // Format ngày giờ thành chuỗi ISO 8601 để gửi lên server
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String appointmentDate = isoFormat.format(selectedDateTime.getTime());

        // Tạo request body
        AppointmentRequest request = new AppointmentRequest(vehicleId, serviceId, appointmentDate, notes);

        // Gọi API
        apiService.createAppointment(authToken, request).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(@NonNull Call<Appointment> call, @NonNull Response<Appointment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Đặt lịch thành công!", Toast.LENGTH_LONG).show();
                    finish(); // Đóng màn hình đặt lịch và quay lại màn hình chính
                } else {
                    Toast.makeText(BookingActivity.this, "Đặt lịch thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Appointment> call, @NonNull Throwable t) {
                Toast.makeText(BookingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "confirmBooking failed: " + t.getMessage());
            }
        });
    }
}