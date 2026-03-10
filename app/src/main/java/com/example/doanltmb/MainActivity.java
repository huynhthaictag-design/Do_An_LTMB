package com.example.doanltmb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        // 1. Tìm cái khung chứa sản phẩm trong file XML
        // Lưu ý: ID ở đây tôi đang dùng là "recyclerViewProducts" để khớp với code AI Studio vừa cho bạn.
        recyclerView = findViewById(R.id.recyclerViewProducts);

        // Cài đặt hiển thị thành dạng lưới (Grid) có 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // 2. Tạo một vài sản phẩm giả để xem giao diện lên hình như thế nào
        productList = new ArrayList<>();
        productList.add(new Product("iPhone 15 Pro", "28.990.000đ", ""));
        productList.add(new Product("MacBook Air M2", "24.500.000đ", ""));
        productList.add(new Product("Sony WH-1000XM5", "6.500.000đ", ""));
        productList.add(new Product("Samsung Galaxy S23", "15.990.000đ", ""));
        productList.add(new Product("iPad Pro M2", "20.990.000đ", ""));
        productList.add(new Product("Apple Watch S8", "9.500.000đ", ""));

        // 3. Đưa danh sách sản phẩm này cho Adapter để nó "lắp ráp" lên màn hình
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // 4. Xử lý thanh điều hướng dưới cùng
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            // Tạm thời để trống, sau này bạn code chuyển trang thì nhét vào đây
            return true;
        });
    }
}