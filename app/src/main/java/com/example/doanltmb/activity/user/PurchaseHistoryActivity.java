package com.example.doanltmb.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.doanltmb.model.Order;

import java.util.List;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PurchaseHistoryAdapter adapter;
    private TextView tvEmpty;
    private String currentUsername;

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

    private void loadPurchaseHistory() {
        List<Order> history = dbHelper.getUserPurchaseHistory(currentUsername);
        adapter.swapData(history);
        boolean hasItems = history != null && !history.isEmpty();
        tvEmpty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPurchaseHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
