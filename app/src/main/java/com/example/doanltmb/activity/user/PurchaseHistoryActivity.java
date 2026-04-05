package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.LoginActivity;
import com.example.doanltmb.adapter.PurchaseHistoryAdapter;
import com.example.doanltmb.database.DatabaseHelper;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PurchaseHistoryAdapter adapter;
    private TextView tvEmpty;
    private String currentUsername;

    // Khoi tao man lich su mua hang va chi cho phep user da dang nhap truy cap.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUsername = prefs.getString("currentUsername", "");
        dbHelper = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPurchaseHistory);
        tvEmpty = findViewById(R.id.tvEmptyPurchaseHistory);
        ImageView btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PurchaseHistoryAdapter(this, null);
        recyclerView.setAdapter(adapter);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadPurchaseHistory();
    }

    // Nap lich su mua hang da duoc duyet va cap nhat trang thai rong.
    private void loadPurchaseHistory() {
        Cursor cursor = dbHelper.getUserPurchaseHistory(currentUsername);
        adapter.swapCursor(cursor);
        boolean hasItems = cursor != null && cursor.getCount() > 0;
        tvEmpty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    // Tai lai du lieu khi user quay lai man lich su.
    @Override
    protected void onResume() {
        super.onResume();
        loadPurchaseHistory();
    }

    // Dong adapter va database khi activity bi huy.
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
}
