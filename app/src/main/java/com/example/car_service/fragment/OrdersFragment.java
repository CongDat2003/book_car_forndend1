package com.example.car_service.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private final List<Order> orders = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new SimpleOrdersAdapter());
        apiService = ApiClient.getApiService();
        loadOrders();
        return root;
    }

    private void loadOrders() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null) return;
        apiService.getMyOrders(token).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body());
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
            @Override public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) { }
        });
    }

    class SimpleOrdersAdapter extends RecyclerView.Adapter<SimpleOrdersAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int position) {
            Order o = orders.get(position);
            ((TextView) h.itemView.findViewById(android.R.id.text1)).setText("Đơn #" + o.getId() + " • " + o.getStatus());
            ((TextView) h.itemView.findViewById(android.R.id.text2)).setText(o.getCreatedAt() + " • " + String.format("%,.0f₫", o.getTotalAmount()));
        }
        @Override public int getItemCount() { return orders.size(); }
        class VH extends RecyclerView.ViewHolder { VH(@NonNull View itemView) { super(itemView); } }
    }
}


