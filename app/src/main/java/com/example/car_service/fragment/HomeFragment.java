package com.example.car_service.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.car_service.R;
import com.example.car_service.activity.BookingActivity;
import com.example.car_service.activity.LoginActivity;
import com.example.car_service.activity.RegisterActivity;
import com.example.car_service.adapter.BannerAdapter;
import com.example.car_service.adapter.StoreAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutIndicator;
    private RecyclerView recyclerViewStores;
    private TextView textViewUserName;
    private LinearLayout layoutAuthActions; // For "Đăng nhập | Đăng ký"

    private int[] bannerImages = {R.drawable.banner_placeholder_1, R.drawable.ic_launcher_background};
    private int[] storeImages = {R.drawable.store_placeholder_1, R.drawable.ic_launcher_background, R.drawable.store_placeholder_1};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);
        recyclerViewStores = view.findViewById(R.id.recyclerViewStores);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        layoutAuthActions = view.findViewById(R.id.authActions); // Ánh xạ LinearLayout

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBanner();
        setupStoresRecyclerView();

        // Setup click listeners for login/register
        view.findViewById(R.id.textLogin).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LoginActivity.class))
        );
        view.findViewById(R.id.textRegister).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RegisterActivity.class))
        );

        // Đã dọn bỏ các thẻ đặt lịch cũ khỏi layout
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserNameAndAuthActions();
    }

    private void displayUserNameAndAuthActions() {
        if (getActivity() == null) {
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String fullName = sharedPreferences.getString("user_full_name", null);

        if (fullName != null && !fullName.isEmpty()) {
            // Hiển thị tên user đã đăng nhập
            textViewUserName.setText(fullName.split(" ")[0]); // Lấy tên đầu tiên
            textViewUserName.setVisibility(View.VISIBLE);
            layoutAuthActions.setVisibility(View.GONE);
        } else {
            // Hiển thị nút đăng nhập/đăng ký
            textViewUserName.setVisibility(View.GONE);
            layoutAuthActions.setVisibility(View.VISIBLE);
        }
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);
        new TabLayoutMediator(tabLayoutIndicator, viewPagerBanner, (tab, position) -> {}).attach();
    }

    private void setupStoresRecyclerView() {
        recyclerViewStores.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        StoreAdapter storeAdapter = new StoreAdapter(storeImages);
        recyclerViewStores.setAdapter(storeAdapter);
    }
}
