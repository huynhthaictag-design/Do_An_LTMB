package com.example.doanltmb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.adapter.ProductAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FloatingActionButton fabAdd;
    private DatabaseHelper db;
    private String userRole;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Khởi tạo Database và lấy thông tin người dùng
        db = new DatabaseHelper(this);
        checkUserStatus();

        // 2. Thiết lập nút Thêm (+) dành cho Admin
        fabAdd = findViewById(R.id.fabAddProduct);
        updateFabVisibility();

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddProductActivity.class));
        });

        // 3. Thiết lập danh sách sản phẩm (RecyclerView)
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // 4. Thiết lập thanh điều hướng Bottom Navigation
        setupBottomNavigation();
    }

    // Hàm kiểm tra trạng thái đăng nhập từ máy
    private void checkUserStatus() {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
        userRole = sharedPref.getString("userRole", "user");
    }

    // Hàm ẩn/hiện nút Thêm dựa trên quyền admin
    private void updateFabVisibility() {
        if ("admin".equals(userRole) && isLoggedIn) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }
    }

    // Cực kỳ quan trọng: Tự động nạp lại dữ liệu mỗi khi quay về trang chủ
    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus(); // Cập nhật lại trạng thái (phòng trường hợp vừa đăng xuất)
        updateFabVisibility();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        List<Product> productList = db.getAllProducts();
        // Truyền context, danh sách và role vào Adapter
        adapter = new ProductAdapter(this, productList, userRole);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                // CHỐT KIỂM SOÁT ĐĂNG NHẬP Ở ĐÂY
                if (isLoggedIn) {
                    // Đã đăng nhập -> Mở trang Profile
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    // Chưa đăng nhập -> Yêu cầu đăng nhập
                    Toast.makeText(this, "đăng nhập đã nhé!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return false; // Không chuyển trạng thái chọn trên menu để giữ ở Home
            }
            return true;
        });
    }
}