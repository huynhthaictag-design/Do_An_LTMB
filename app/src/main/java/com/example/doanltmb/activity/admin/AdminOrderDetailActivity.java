package com.example.doanltmb.activity.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.utils.NotificationHelper;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvDetailUser, tvDetailProduct, tvDetailQty, tvDetailPrice, tvDetailStatus;
    private Button btnApprove, btnCancel;
    private ImageView btnBack;
    private DatabaseHelper db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        db = new DatabaseHelper(this);
        initViews();

        // Lấy ID đơn hàng từ Intent
        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderDetail();
        }

        // Sự kiện nút bấm
        btnApprove.setOnClickListener(v -> handleAction("Approved"));
        btnCancel.setOnClickListener(v -> handleAction("Cancelled"));

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        tvDetailUser = findViewById(R.id.tvDetailUser);
        tvDetailProduct = findViewById(R.id.tvDetailProduct);
        tvDetailQty = findViewById(R.id.tvDetailQty);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        btnApprove = findViewById(R.id.btnApprove);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadOrderDetail() {
        Cursor cursor = db.getOrderById(orderId);
        if (cursor != null && cursor.moveToFirst()) {
            String user = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String product = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            tvDetailUser.setText("Khách hàng: " + user);
            tvDetailProduct.setText("Sản phẩm: " + product);
            tvDetailQty.setText("Số lượng: " + qty);
            tvDetailPrice.setText("Tổng tiền: " + String.format("%,.0fđ", price * qty).replace(",", "."));
            tvDetailStatus.setText("Trạng thái: " + status);
            cursor.close();
        }
    }

    private void handleAction(String status) {
        if (db.updateOrderStatus(orderId, status)) {
            String message = status.equals("Approved") ? "Đơn hàng đã được DUYỆT!" : "Đơn hàng đã bị HỦY!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Gửi thông báo thông qua Helper bạn đã tạo
            NotificationHelper.sendNotification(this, "Cập nhật đơn hàng", "Đơn hàng #" + orderId + " hiện tại: " + status);

            finish(); // Quay lại danh sách đơn hàng
        }
    }
}