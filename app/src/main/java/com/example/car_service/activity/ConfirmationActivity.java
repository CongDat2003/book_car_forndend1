package com.example.car_service.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.car_service.R;

public class ConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        long id = getIntent().getLongExtra("APPOINTMENT_ID", -1);
        TextView textMessage = findViewById(R.id.textMessage);
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonView = findViewById(R.id.buttonView);

        textMessage.setText("Đặt lịch thành công! Mã: " + id);
        buttonHome.setOnClickListener(v -> finish());
        buttonView.setOnClickListener(v -> finish());
    }
}












