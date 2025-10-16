package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.adapter.BrandAdapter;
import com.example.car_service.adapter.VehicleModelAdapter;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Brand;
import com.example.car_service.model.VehicleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectVehicleActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBrands, recyclerViewModels;
    private SearchView searchViewVehicle;
    private ApiService apiService;
    private BrandAdapter brandAdapter;
    private VehicleModelAdapter modelAdapter;
    private final List<Brand> brandList = new ArrayList<>();
    private final List<VehicleModel> allModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle);

        apiService = ApiClient.getApiService();
        recyclerViewBrands = findViewById(R.id.recyclerViewBrands);
        recyclerViewModels = findViewById(R.id.recyclerViewModels);
        searchViewVehicle = findViewById(R.id.searchViewVehicle);

        recyclerViewBrands.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewModels.setLayoutManager(new GridLayoutManager(this, 2));

        modelAdapter = new VehicleModelAdapter(new ArrayList<>(), model -> {
            Intent intent = new Intent(this, SelectServiceActivity.class);
            intent.putExtra("VEHICLE_MODEL_ID", model.getId());
            startActivity(intent);
        });
        recyclerViewModels.setAdapter(modelAdapter);

        setupSearch();
        fetchBrands();
    }

    private void setupSearch() {
        searchViewVehicle.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterModels(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterModels(newText);
                return true;
            }
        });
    }

    private void filterModels(String keyword) {
        List<VehicleModel> filtered;
        if (TextUtils.isEmpty(keyword)) {
            filtered = new ArrayList<>(allModels);
        } else {
            String lower = keyword.toLowerCase();
            filtered = allModels.stream()
                    .filter(m -> m.getName() != null && m.getName().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }
        modelAdapter.submitList(filtered);
    }

    private void fetchBrands() {
        apiService.getBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList.clear();
                    brandList.addAll(response.body());
                    brandAdapter = new BrandAdapter(brandList, brand -> fetchModelsByBrand(brand.getId()));
                    recyclerViewBrands.setAdapter(brandAdapter);
                    fetchAllModels(); // Load all models after brands are loaded
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                // TODO: Show retry/snackbar
            }
        });
    }

    private void fetchAllModels() {
        // Fetch all models by calling each brand
        if (brandList.isEmpty()) {
            // If brands not loaded yet, wait for them
            return;
        }
        
        List<VehicleModel> allModelsList = new ArrayList<>();
        final int[] completedRequests = {0};
        
        for (Brand brand : brandList) {
            apiService.getVehicleModelsByBrand(brand.getId()).enqueue(new Callback<List<VehicleModel>>() {
                @Override
                public void onResponse(Call<List<VehicleModel>> call, Response<List<VehicleModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allModelsList.addAll(response.body());
                    }
                    completedRequests[0]++;
                    if (completedRequests[0] == brandList.size()) {
                        allModels.clear();
                        allModels.addAll(allModelsList);
                        modelAdapter.submitList(new ArrayList<>(allModels));
                    }
                }

                @Override
                public void onFailure(Call<List<VehicleModel>> call, Throwable t) {
                    completedRequests[0]++;
                    if (completedRequests[0] == brandList.size()) {
                        allModels.clear();
                        allModels.addAll(allModelsList);
                        modelAdapter.submitList(new ArrayList<>(allModels));
                    }
                }
            });
        }
    }

    private void fetchModelsByBrand(Integer brandId) {
        apiService.getVehicleModelsByBrand(brandId).enqueue(new Callback<List<VehicleModel>>() {
            @Override
            public void onResponse(Call<List<VehicleModel>> call, Response<List<VehicleModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allModels.clear();
                    allModels.addAll(response.body());
                    modelAdapter.submitList(new ArrayList<>(allModels));
                }
            }

            @Override
            public void onFailure(Call<List<VehicleModel>> call, Throwable t) {
                // TODO: Show retry/snackbar
            }
        });
    }
}