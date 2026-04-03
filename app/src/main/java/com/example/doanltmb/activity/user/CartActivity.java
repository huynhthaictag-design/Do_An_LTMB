package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnCheckout;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setListeners();
        setupBottomNavigation();
        onResume();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCheckout = findViewById(R.id.btnCheckout);
        bottomNav = findViewById(R.id.bottomNavigation);
    }

    private void setListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                Toast.makeText(CartActivity.this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNav == null) return;

        // Cách an toàn tuyệt đối để sáng icon mà không văng app
        try {
            MenuItem itemCart = bottomNav.getMenu().findItem(R.id.nav_cart);
            if (itemCart != null) {
                itemCart.setChecked(true); // Chỉ đổi giao diện, không kích hoạt vòng lặp
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Nếu bấm vào icon Trang Chủ
            // LƯU Ý: Nếu ID của bạn là R.id.nav_main thì đổi nav_home thành nav_main nhé
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                // Dòng này giúp xóa các màn hình trung gian đi, chỉ giữ 1 bản MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }

            // Nếu bấm vào icon Giỏ Hàng (đang ở trang này rồi thì bỏ qua)
            if (itemId == R.id.nav_cart) {
                return true;
            }

            // Nếu bạn có tab Profile
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                return false;
            }

            return false;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            // LƯU Ý: Đảm bảo "nav_main" đúng với ID ở file bottom_nav_menu.xml
            android.view.MenuItem item = bottomNav.getMenu().findItem(R.id.nav_cart);
            if (item != null) {
                item.setChecked(true);
            }
        }
    }
}