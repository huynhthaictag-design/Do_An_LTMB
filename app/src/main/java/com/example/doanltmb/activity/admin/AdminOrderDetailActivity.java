package com.example.doanltmb.activity.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Order;
import com.example.doanltmb.utils.NotificationHelper;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvDetailUser;
    private TextView tvDetailProduct;
    private TextView tvDetailQty;
    private TextView tvDetailPrice;
    private TextView tvDetailStatus;
    private Button btnApprove;
    private Button btnCancel;
    private ImageView btnBack;
    private DatabaseHelper db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        db = new DatabaseHelper(this);
        initViews();

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            loadOrderDetail();
        }

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
        // Cần đảm bảo DatabaseHelper đã có hàm getOrderById trả về Order
        Order order = db.getOrderById(orderId);
        if (order == null) {
            return;
        }

        tvDetailUser.setText("Khách hàng: " + order.getUsername());
        tvDetailProduct.setText("Sản phẩm: " + order.getProductName());
        tvDetailQty.setText("Số lượng: " + order.getQuantity());
        tvDetailPrice.setText("Tổng tiền: " + String.format("%,.0fđ", order.getTotalPrice()).replace(",", "."));
        tvDetailStatus.setText("Trạng thái: " + order.getStatus());
    }

    private void handleAction(String status) {
        if (db.updateOrderStatus(orderId, status)) {
            String message = "Approved".equals(status) ? "Đã duyệt đơn và trừ kho!" : "Đã hủy đơn!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            NotificationHelper.sendNotification(
                    this,
                    "Cập nhật đơn hàng",
                    "Đơn hàng #" + orderId + " hiện tại: " + status
            );

            finish();
        } else {
            if ("Approved".equals(status)) {
                Toast.makeText(this, "LỖI: Không đủ số lượng tồn kho để duyệt đơn này!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}