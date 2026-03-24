package com.example.doanltmb.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.adapter.*;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        setupBottomNavigation();
    }

    private void setupRecyclerView(){

        // Tìm RecyclerView trong XML
        recyclerView = findViewById(R.id.recyclerViewProducts);

        // Hiển thị dạng lưới 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo database
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Lấy danh sách sản phẩm
        productList = dbHelper.getAllProductsList();

        // Gắn adapter
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation(){

<<<<<<< HEAD:app/src/main/java/com/example/doanltmb/MainActivity.java
        // Xử lý thanh điều hướng dưới cùng
        // Xử lý thanh điều hướng dưới cùng
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Mặc định chọn tab Home khi đang ở MainActivity
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Đang ở trang chủ rồi thì không làm gì cả
                return true;
            } else if (itemId == R.id.nav_category) {
                // TODO: Chuyển sang trang Danh mục (nếu có)
                return true;
            } else if (itemId == R.id.nav_favorite) {
                // TODO: Chuyển sang trang Yêu thích (nếu có)
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Chuyển sang trang Tài khoản
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
=======
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home); //Để mặc định trang chính
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

//            if(id == R.id.nav_home){
//
//            }

//            if(id == R.id.nav_cart){
//
//            }

//            if(id == R.id.nav_profile){
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//                finish();
//            }

            return true;
>>>>>>> fb6769e0cb9aed72adcbd39de416ba07f18514e1:app/src/main/java/com/example/doanltmb/activity/MainActivity.java
        });
    }
}