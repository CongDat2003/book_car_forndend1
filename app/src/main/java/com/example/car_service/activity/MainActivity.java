package com.example.car_service.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.car_service.R;
import com.example.car_service.adapter.BannerAdapter;
import com.example.car_service.adapter.StoreAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutIndicator;
    private RecyclerView recyclerViewStores;
    private BottomNavigationView bottomNavigation;
    private TextView textViewUserName;

    // Dữ liệu mẫu (trong thực tế sẽ lấy từ API)
    private int[] bannerImages = {R.drawable.banner_placeholder_1, R.drawable.ic_launcher_background};
    private int[] storeImages = {R.drawable.store_placeholder_1, R.drawable.ic_launcher_background, R.drawable.store_placeholder_1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabLayoutIndicator = findViewById(R.id.tabLayoutIndicator);
        recyclerViewStores = findViewById(R.id.recyclerViewStores);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        textViewUserName = findViewById(R.id.textViewUserName);

        // Cài đặt
        displayUserName();
        setupBanner();
        setupStoresRecyclerView();
        setupBottomNavigation();

        // Setup click cho các thẻ trong Grid
        findViewById(R.id.cardBookRepair).setOnClickListener(v ->
                startActivity(new Intent(this, BookingActivity.class))
        );
    }

    private void displayUserName() {
        String fullName = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("user_full_name", "Khách");
        if (fullName != null && !fullName.isEmpty()) {
            textViewUserName.setText(fullName.split(" ")[0]);
        }
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);
        new TabLayoutMediator(tabLayoutIndicator, viewPagerBanner, (tab, position) -> {}).attach();
    }

    private void setupStoresRecyclerView() {
        recyclerViewStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        StoreAdapter storeAdapter = new StoreAdapter(storeImages);
        recyclerViewStores.setAdapter(storeAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Người dùng đang ở trang chủ, không cần làm gì
                return true;
            } else if (itemId == R.id.nav_appointment) {
                // Chuyển đến màn hình Lịch sử cuộc hẹn
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (itemId == R.id.nav_account) {
                // Chuyển đến màn hình Tài khoản
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            } else if (itemId == R.id.nav_rescue || itemId == R.id.nav_store) {
                // Hiển thị thông báo cho các chức năng chưa phát triển
                Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
                return true;
            }

            // Trả về false nếu không xử lý
            return false;
        });
    }
}