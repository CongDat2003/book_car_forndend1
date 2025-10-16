package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTimeActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private RecyclerView recyclerViewSlots;
    private Button buttonNext;
    private ApiService apiService;
    private String selectedDate;
    private String selectedSlot;

    private long modelId;
    private int[] productIds;
    private int[] quantities;
    private ArrayList<Long> serviceIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        modelId = getIntent().getLongExtra("VEHICLE_MODEL_ID", -1);
        productIds = getIntent().getIntArrayExtra("PRODUCT_IDS");
        quantities = getIntent().getIntArrayExtra("QUANTITIES");
        serviceIds = (ArrayList<Long>) getIntent().getSerializableExtra("SERVICE_IDS");

        apiService = ApiClient.getApiService();
        calendarView = findViewById(R.id.calendarView);
        recyclerViewSlots = findViewById(R.id.recyclerViewSlots);
        buttonNext = findViewById(R.id.buttonNextTime);

        recyclerViewSlots.setLayoutManager(new GridLayoutManager(this, 3));
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(calendarView.getDate()));
        fetchSlots(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            fetchSlots(selectedDate);
        });

        buttonNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("VEHICLE_MODEL_ID", modelId);
            intent.putExtra("SERVICE_IDS", serviceIds);
            intent.putExtra("PRODUCT_IDS", productIds);
            intent.putExtra("QUANTITIES", quantities);
            intent.putExtra("DATE", selectedDate);
            intent.putExtra("SLOT", selectedSlot);
            startActivity(intent);
        });
    }

    private void fetchSlots(String date) {
        apiService.getAvailableSlots(date).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // very simple clickable list using basic adapter
                    recyclerViewSlots.setAdapter(new SimpleStringGridAdapter(response.body(), slot -> selectedSlot = slot));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) { }
        });
    }
}

class SimpleStringGridAdapter extends RecyclerView.Adapter<SimpleStringGridAdapter.VH> {
    interface OnClick { void onClick(String value); }
    private final List<String> data;
    private final OnClick onClick;
    SimpleStringGridAdapter(List<String> data, OnClick onClick) { this.data = data; this.onClick = onClick; }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View v = android.view.LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        String s = data.get(position);
        ((android.widget.TextView) holder.itemView.findViewById(android.R.id.text1)).setText(s);
        holder.itemView.setOnClickListener(v -> onClick.onClick(s));
    }
    @Override public int getItemCount() { return data != null ? data.size() : 0; }
    static class VH extends RecyclerView.ViewHolder { VH(@NonNull android.view.View itemView) { super(itemView); } }
}



