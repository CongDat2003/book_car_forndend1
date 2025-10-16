package com.example.car_service.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Appointment;
import com.example.car_service.model.AppointmentRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView textSummary;
    private Button buttonConfirm;
    private ApiService apiService;

    private long modelId;
    private int[] productIds;
    private int[] quantities;
    private java.util.ArrayList<Long> serviceIds;
    private String date;
    private String slot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        apiService = ApiClient.getApiService();
        textSummary = findViewById(R.id.textSummary);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        modelId = getIntent().getLongExtra("VEHICLE_MODEL_ID", -1);
        productIds = getIntent().getIntArrayExtra("PRODUCT_IDS");
        quantities = getIntent().getIntArrayExtra("QUANTITIES");
        serviceIds = (java.util.ArrayList<Long>) getIntent().getSerializableExtra("SERVICE_IDS");
        date = getIntent().getStringExtra("DATE");
        slot = getIntent().getStringExtra("SLOT");

        textSummary.setText("Xe: " + modelId + "\nDịch vụ: " + serviceIds + "\nSản phẩm: " + (productIds==null?0:productIds.length) + "\nThời gian: " + date + " " + slot);

        buttonConfirm.setOnClickListener(v -> createAppointment());
    }

    private void createAppointment() {
        // Minimal example: map modelId to vehicleId if needed; here we just send modelId as vehicleId placeholder
        AppointmentRequest req = new AppointmentRequest(modelId, serviceIds != null && !serviceIds.isEmpty() ? serviceIds.get(0) : -1, date + "T" + (slot != null ? slot : "00:00"), null);
        apiService.createAppointment(null, req).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CheckoutActivity.this, "Đặt lịch thành công", Toast.LENGTH_SHORT).show();
                    android.content.Intent intent = new android.content.Intent(CheckoutActivity.this, ConfirmationActivity.class);
                    intent.putExtra("APPOINTMENT_ID", response.body().getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi đặt lịch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



