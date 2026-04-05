package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.LoginActivity;
import com.example.doanltmb.activity.product.CartActivity;
import com.example.doanltmb.adapter.UserNotificationAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserNotificationActivity extends AppCompatActivity implements UserNotificationAdapter.NotificationActionListener {

    private DatabaseHelper dbHelper;
    private UserNotificationAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private BottomNavigationView bottomNav;
    private String currentUsername;

    // Khoi tao man thong bao, kiem tra dang nhap va nap danh sach thong bao.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUsername = prefs.getString("currentUsername", "");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        tvEmpty = findViewById(R.id.tvEmptyNotifications);
        bottomNav = findViewById(R.id.bottomNavigation);
        ImageView btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserNotificationAdapter(this, null, this);
        recyclerView.setAdapter(adapter);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        setupBottomNavigation();
        loadNotifications();
    }

    // Nap lai thong bao tu database va cap nhat trang thai rong.
    private void loadNotifications() {
        Cursor cursor = dbHelper.getUserNotifications(currentUsername);
        adapter.swapCursor(cursor);
        boolean hasNotifications = cursor != null && cursor.getCount() > 0;
        tvEmpty.setText(hasNotifications ? "" : "Chưa có thông báo đơn hàng nào.");
        tvEmpty.setVisibility(hasNotifications ? View.GONE : View.VISIBLE);
    }

    // Dieu huong bottom navigation tu man thong bao cua user.
    private void setupBottomNavigation() {
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_notification);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(UserNotificationActivity.this, MainActivity.class));
                finish();
                return true;
            }

            if (itemId == R.id.nav_notification) {
                return true;
            }

            if (itemId == R.id.nav_cart) {
                startActivity(new Intent(UserNotificationActivity.this, CartActivity.class));
                return true;
            }

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(UserNotificationActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }

    // Tu dong tai lai thong bao moi nhat khi user quay lai man hinh.
    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    // Dong adapter va database de tranh ro ri tai nguyen.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // Xu ly an mot thong bao sau khi user dong y xoa.
    @Override
    public void onDeleteNotification(int orderId) {
        boolean deleted = dbHelper.hideUserNotification(orderId, currentUsername);
        if (deleted) {
            Toast.makeText(this, "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
            loadNotifications();
        } else {
            Toast.makeText(this, "Không thể xóa thông báo", Toast.LENGTH_SHORT).show();
        }
    }
}
