package com.example.car_service.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.adapter.ProductAdapter;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewStoreProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), (productId, quantity, unitPrice) -> {
            com.example.car_service.service.CartManager.setQuantity(requireContext(), productId, quantity);
        });
        recyclerView.setAdapter(productAdapter);

        apiService = ApiClient.getApiService();
        loadProducts();
        return root;
    }

    private void loadProducts() {
        // Hiển thị toàn bộ sản phẩm bằng cách gom sản phẩm từ tất cả dịch vụ hiện có
        apiService.getServices().enqueue(new Callback<List<com.example.car_service.model.Service>>() {
            @Override
            public void onResponse(@NonNull Call<List<com.example.car_service.model.Service>> call, @NonNull Response<List<com.example.car_service.model.Service>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                List<Product> all = new ArrayList<>();
                for (com.example.car_service.model.Service s : response.body()) {
                    apiService.getProductsByService(s.getId()).enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> resp) {
                            if (resp.isSuccessful() && resp.body() != null) {
                                all.addAll(resp.body());
                                productAdapter.submitList(new ArrayList<>(all));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) { }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<com.example.car_service.model.Service>> call, @NonNull Throwable t) { }
        });
    }
}


