package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.adapter.ProductAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadProducts();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);

        // Grid 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        dbHelper = new DatabaseHelper(this);
    }

    private void loadProducts() {

        // Lấy dữ liệu từ DB
        List<Product> productList = dbHelper.getAllProductsList();

        adapter = new ProductAdapter(this, productList);

        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            }

            if (itemId == R.id.nav_category) {
                // TODO
                return true;
            }

            if (itemId == R.id.nav_favorite) {
                // TODO
                return true;
            }

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }
}