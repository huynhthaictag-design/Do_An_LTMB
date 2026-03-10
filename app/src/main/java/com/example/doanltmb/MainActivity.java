package com.example.doanltmb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Import thêm DatabaseHelper
import com.example.doanltmb.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tìm cái khung chứa sản phẩm trong file XML
        recyclerView = findViewById(R.id.recyclerViewProducts);

        // Cài đặt hiển thị thành dạng lưới (Grid) có 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Lấy danh sách sản phẩm thật từ SQLite
        productList = dbHelper.getAllProductsList();

        // Đưa danh sách vào Adapter để "lắp ráp" lên màn hình
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Xử lý thanh điều hướng dưới cùng
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {

            return true;
        });
    }
}