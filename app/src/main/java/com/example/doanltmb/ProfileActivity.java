package com.example.doanltmb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu); // Đảm bảo đúng tên file layout của bạn

        // 1. Ánh xạ các thành phần từ XML
        TextView tvUsername = findViewById(R.id.tvUsername);
        ImageView btnBack = findViewById(R.id.btnBack);

        // 2. Lấy Username đã lưu khi nãy từ SharedPreferences ra
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // Lấy chữ, nếu không tìm thấy thì để mặc định là "Người dùng"
        String username = sharedPreferences.getString("USERNAME", "Người dùng");

        // 3. Thay thế chữ Nguyễn Văn A bằng Username thật
        tvUsername.setText(username);

        // 4. Bắt sự kiện khi bấm vào nút mũi tên Back
        btnBack.setOnClickListener(v -> {
            finish(); // Lệnh này sẽ đóng trang Tài Khoản lại, tự động lùi về màn hình Trang chủ (MainActivity)
        });
    }
}