package com.example.doanltmb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Gọi cái giao diện XML ra để hiển thị
        setContentView(R.layout.activity_trangchu);

        // 2. Tìm cái nút "Đăng sản phẩm" thông qua cái ID lúc nãy mình vừa đặt
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        // 3. Bắt sự kiện: Nếu người dùng click vào cái nút đó thì làm gì?
        btnAddProduct.setOnClickListener(view -> {

            // Tạo 1 "chuyến xe" (Intent) chở người dùng từ trang này (ProfileActivity) sang trang kia (AddProductActivity)
            Intent intent = new Intent(ProfileActivity.this, AddProductActivity.class);
            startActivity(intent);

        });
    }
}