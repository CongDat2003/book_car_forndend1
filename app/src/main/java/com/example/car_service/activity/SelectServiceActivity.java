package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.adapter.ServiceAdapter;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerViewServices;
    private Button buttonBack, buttonNext;
    private ApiService apiService;
    private ServiceAdapter serviceAdapter;
    private final Set<Long> selectedServiceIds = new HashSet<>();
    private long selectedModelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        selectedModelId = getIntent().getLongExtra("VEHICLE_MODEL_ID", -1);

        apiService = ApiClient.getApiService();
        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        buttonBack = findViewById(R.id.buttonBack);
        buttonNext = findViewById(R.id.buttonNext);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(new ArrayList<>(), (serviceId, selected) -> {
            if (selected) selectedServiceIds.add(serviceId); else selectedServiceIds.remove(serviceId);
        });
        recyclerViewServices.setAdapter(serviceAdapter);

        buttonBack.setOnClickListener(v -> finish());
        buttonNext.setOnClickListener(v -> {
            ArrayList<Long> ids = new ArrayList<>(selectedServiceIds);
            Intent intent = new Intent(this, SelectProductActivity.class);
            intent.putExtra("VEHICLE_MODEL_ID", selectedModelId);
            intent.putExtra("SERVICE_IDS", ids);
            startActivity(intent);
        });

        fetchServices();
    }

    private void fetchServices() {
        apiService.getServices().enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Service> data = response.body();
                    // Re-init adapter to refresh list
                    serviceAdapter = new ServiceAdapter(data, (serviceId, selected) -> {
                        if (selected) selectedServiceIds.add(serviceId); else selectedServiceIds.remove(serviceId);
                    });
                    recyclerViewServices.setAdapter(serviceAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                // TODO show error
            }
        });
    }
}

