package com.example.doanltmb.activity.admin;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.adapter.AdminOrderAdapter;
import com.example.doanltmb.database.DatabaseHelper;

public class AdminOrderActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private DatabaseHelper db;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        db = new DatabaseHelper(this);
        rvOrders = findViewById(R.id.rvAdminOrders);
        btnBack = findViewById(R.id.btnBack);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        loadOrders();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadOrders() {
        rvOrders.setAdapter(new AdminOrderAdapter(this, db.getAllOrders()));
    }
}
