package com.example.car_service.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.car_service.R;
import com.example.car_service.adapter.AppointmentAdapter;
import com.example.car_service.api.ApiClient;
import com.example.car_service.api.ApiService;
import com.example.car_service.model.Appointment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerViewHistory;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        apiService = ApiClient.getApiService();

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AppointmentAdapter(appointmentList);
        recyclerViewHistory.setAdapter(adapter);

        fetchAppointmentHistory();
    }

    private void fetchAppointmentHistory() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
        String authToken = sharedPreferences.getString("jwt_token", null);
        long userId = sharedPreferences.getLong("user_id", -1);

        if (authToken == null || userId == -1) {
            Toast.makeText(requireContext(), "Lỗi xác thực. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAppointmentHistory(authToken, userId).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Appointment>> call, @NonNull Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentList.clear();
                    appointmentList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Không thể tải lịch sử.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}