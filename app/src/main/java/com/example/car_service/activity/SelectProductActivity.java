package com.example.car_service.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.adapter.ProductAdapter;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActivity extends AppCompatActivity implements ProductAdapter.OnCartChangedListener {

    private RecyclerView recyclerViewProducts;
    private TextView textCartSummary;
    private Button buttonNext;
    private ApiService apiService;
    private final Map<Integer, Integer> cart = new HashMap<>();
    private ArrayList<Long> serviceIds;
    private long modelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        textCartSummary = findViewById(R.id.textCartSummary);
        buttonNext = findViewById(R.id.buttonNextProduct);
        apiService = ApiClient.getApiService();

        modelId = getIntent().getLongExtra("VEHICLE_MODEL_ID", -1);
        serviceIds = (ArrayList<Long>) getIntent().getSerializableExtra("SERVICE_IDS");

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProducts.setAdapter(new ProductAdapter(new ArrayList<>(), this));

        buttonNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectTimeActivity.class);
            intent.putExtra("VEHICLE_MODEL_ID", modelId);
            intent.putExtra("SERVICE_IDS", serviceIds);
            // Flatten cart to two arrays
            int[] productIds = cart.keySet().stream().mapToInt(Integer::intValue).toArray();
            int[] quantities = cart.values().stream().mapToInt(Integer::intValue).toArray();
            intent.putExtra("PRODUCT_IDS", productIds);
            intent.putExtra("QUANTITIES", quantities);
            startActivity(intent);
        });

        fetchProductsForServices();
    }

    private void fetchProductsForServices() {
        // naive sequential fetch; in practice consider parallel merge
        List<Product> aggregated = new ArrayList<>();
        if (serviceIds == null) return;
        for (Long id : serviceIds) {
            apiService.getProductsByService(id).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        aggregated.addAll(response.body());
                        ProductAdapter adapter = (ProductAdapter) recyclerViewProducts.getAdapter();
                        if (adapter != null) adapter.submitList(new ArrayList<>(aggregated));
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) { }
            });
        }
    }

    @Override
    public void onCartChanged(Integer productId, int quantity, double unitPrice) {
        if (quantity <= 0) cart.remove(productId); else cart.put(productId, quantity);
        updateCartSummary((ProductAdapter) recyclerViewProducts.getAdapter());
    }

    private void updateCartSummary(ProductAdapter adapter) {
        if (adapter == null) return;
        int totalQty = 0;
        double totalMoney = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            totalQty += e.getValue();
            Product p = adapter.findProductById(e.getKey());
            if (p != null) totalMoney += p.getPrice() * e.getValue();
        }
        textCartSummary.setText("Giỏ hàng: " + totalQty + " sp • " + String.format("%,.0f₫", totalMoney));
    }
}



