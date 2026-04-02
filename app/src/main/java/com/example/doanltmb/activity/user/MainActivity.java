package com.example.doanltmb.activity.user;

import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

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
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadProducts();
        loadUserInfo(); // 👈 tách riêng
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        tvUsername = findViewById(R.id.tvUsername);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new DatabaseHelper(this);
    }

    private void loadProducts() {
        List<Product> productList = dbHelper.getAllProductsList();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

    private void loadUserInfo() {

        SharedPreferences prefs = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        Cursor cursor = dbHelper.getUser(username);

        if (cursor != null && cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            tvUsername.setText(name);

            cursor.close();
        }
    }

    private void setupBottomNavigation() {

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) return true;

            if (itemId == R.id.nav_category) return true;

            if (itemId == R.id.nav_favorite) return true;

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }
}