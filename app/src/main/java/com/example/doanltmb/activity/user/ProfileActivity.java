package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doanltmb.R;
import com.example.doanltmb.activity.LoginActivity;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.User;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvUsername;
    private DatabaseHelper db;
    private LinearLayout btnLogout;
    private LinearLayout btnPurchaseHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái ngay lập tức
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Nếu chưa đăng nhập mà lỡ vào đây -> Đẩy về Login ngay
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc Activity này để không bấm Back quay lại được
            return;
        }

        setContentView(R.layout.activity_profile);
        initViews();
        setupBackButton();
        setupPurchaseHistoryButton();
        setupLogoutButton();
        loadUserInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);
        btnPurchaseHistory = findViewById(R.id.btnPurchaseHistory);
        db = new DatabaseHelper(this);
    }
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }
    // HÀM XỬ LÝ ĐĂNG XUẤT
    // Mo man lich su mua hang de user xem cac don da duoc duyet.
    private void setupPurchaseHistoryButton() {
        if (btnPurchaseHistory != null) {
            btnPurchaseHistory.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, PurchaseHistoryActivity.class)));
        }
    }
    // Ham xu ly dang xuat va xoa trang thai dang nhap hien tai.
    private void setupLogoutButton() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Xóa toàn bộ dữ liệu trong SharedPreferences "UserPrefs"
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear(); // Lệnh này sẽ xóa sạch username, role, isLoggedIn
                editor.apply();

                Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                // 2. Chuyển về màn hình Login
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                // 3. Xóa cờ (Clear Task) để người dùng không bấm nút Back quay lại app được
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish(); // Đóng ProfileActivity
            }
        });
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUsername", "");

        User user = db.getUserModel(username);
        if (user != null) {
            tvUsername.setText(user.getUsername());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close(); // ✅ đóng DB
        }
    }

}
