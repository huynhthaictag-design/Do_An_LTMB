package com.techpro.store;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo RecyclerView cho sản phẩm
        RecyclerView rvProducts = findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Thiết lập Adapter (Bạn cần tạo ProductAdapter riêng)
        // rvProducts.setAdapter(new ProductAdapter(productList));

        // Xử lý Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            // Xử lý chuyển tab
            return true;
        });
    }
}