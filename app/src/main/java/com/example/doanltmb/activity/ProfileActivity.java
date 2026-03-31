package com.example.doanltmb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.doanltmb.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvUserRole;
    private ImageView btnBack;
    private View btnEditProfile, btnLogout; // Dùng View cho đa năng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_profile);
        } catch (Exception e) {
            // Nếu lỗi giao diện (thiếu icon...), app sẽ báo ở đây thay vì văng
            Toast.makeText(this, "Lỗi nạp giao diện XML!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Ánh xạ ID - Thêm kiểm tra kỹ
        tvUsername = findViewById(R.id.tvUsername);
        tvUserRole = findViewById(R.id.tvUserRole);
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.cardEditProfile);
        btnLogout = findViewById(R.id.btnLogOut);

        // Nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Nút sửa hồ sơ
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            });
        }

        // Nút đăng xuất
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = sharedPref.getString("currentUsername", "Khách");
        if (tvUsername != null) tvUsername.setText(name);
    }
}