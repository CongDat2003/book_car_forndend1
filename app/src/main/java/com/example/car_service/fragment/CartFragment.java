package com.example.car_service.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.CreateOrderRequest;
import com.example.car_service.model.Order;
import com.example.car_service.model.Product;
import com.example.car_service.service.CartManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textTotal;
    private Button buttonCheckout;
    private ApiService apiService;
    private final Map<Integer, Integer> cart = new HashMap<>();
    private final List<Product> products = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewCart);
        textTotal = root.findViewById(R.id.textTotal);
        buttonCheckout = root.findViewById(R.id.buttonCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new SimpleCartAdapter());

        apiService = ApiClient.getApiService();
        cart.clear();
        cart.putAll(CartManager.getAll(requireContext()));
        loadProductsAndUpdateUI();

        buttonCheckout.setOnClickListener(v -> doCheckout());
        return root;
    }

    private void loadProductsAndUpdateUI() {
        // Lấy danh sách dịch vụ rồi lấy sản phẩm (giống StoreFragment) để có giá theo productId
        apiService.getServices().enqueue(new Callback<List<com.example.car_service.model.Service>>() {
            @Override
            public void onResponse(@NonNull Call<List<com.example.car_service.model.Service>> call, @NonNull Response<List<com.example.car_service.model.Service>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                products.clear();
                for (com.example.car_service.model.Service s : response.body()) {
                    apiService.getProductsByService(s.getId()).enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> resp) {
                            if (resp.isSuccessful() && resp.body() != null) {
                                products.addAll(resp.body());
                                recyclerView.getAdapter().notifyDataSetChanged();
                                updateTotal();
                            }
                        }
                        @Override public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) { }
                    });
                }
            }
            @Override public void onFailure(@NonNull Call<List<com.example.car_service.model.Service>> call, @NonNull Throwable t) { }
        });
    }

    private void updateTotal() {
        double total = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            Product p = findProduct(e.getKey());
            if (p != null) total += p.getPrice() * e.getValue();
        }
        textTotal.setText("Tổng: " + String.format("%,.0f₫", total));
    }

    private Product findProduct(int id) {
        for (Product p : products) if (p.getId() == id) return p;
        return null;
    }

    private void doCheckout() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        List<CreateOrderRequest.Item> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            if (e.getValue() > 0) items.add(new CreateOrderRequest.Item(e.getKey(), e.getValue()));
        }
        if (items.isEmpty()) {
            Toast.makeText(requireContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }
        CreateOrderRequest req = new CreateOrderRequest(0, items);
        apiService.createOrder(token, req).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful()) {
                    CartManager.clear(requireContext());
                    Toast.makeText(requireContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    cart.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    updateTotal();
                } else {
                    Toast.makeText(requireContext(), "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class SimpleCartAdapter extends RecyclerView.Adapter<SimpleCartAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int position) {
            Integer productId = new ArrayList<>(cart.keySet()).get(position);
            int qty = cart.get(productId);
            Product p = findProduct(productId);
            String title = p != null ? p.getName() : ("SP #" + productId);
            String subtitle = "Số lượng: " + qty + (p != null ? " • " + String.format("%,.0f₫", p.getPrice()*qty) : "");
            ((TextView) h.itemView.findViewById(android.R.id.text1)).setText(title);
            ((TextView) h.itemView.findViewById(android.R.id.text2)).setText(subtitle);
        }
        @Override public int getItemCount() { return cart.size(); }
        class VH extends RecyclerView.ViewHolder { VH(@NonNull View itemView) { super(itemView); } }
    }
}



