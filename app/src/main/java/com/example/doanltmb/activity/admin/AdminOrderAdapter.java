package com.example.doanltmb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.utils.NotificationHelper;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private Context context;
    private Cursor cursor;
    private DatabaseHelper db;

    public AdminOrderAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
        String user = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        String product = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

        holder.tvCustomer.setText("Khách hàng: " + user);
        holder.tvProduct.setText("Sản phẩm: " + product);
        holder.tvInfo.setText("SL: " + qty + " | Tổng: " + String.format("%,.0fđ", price * qty).replace(",", "."));
        holder.tvStatus.setText("Trạng thái: " + status);

        // Nút Duyệt
        holder.btnApprove.setOnClickListener(v -> {
            if (db.updateOrderStatus(orderId, "Approved")) {
                Toast.makeText(context, "Đã duyệt đơn hàng", Toast.LENGTH_SHORT).show();
                NotificationHelper.sendNotification(context, "Đơn hàng thành công", "Đơn hàng " + product + " đã được Admin duyệt!");
                refreshData();
            }
        });

        // Nút Hủy
        holder.btnCancel.setOnClickListener(v -> {
            if (db.updateOrderStatus(orderId, "Cancelled")) {
                Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                NotificationHelper.sendNotification(context, "Thông báo đơn hàng", "Rất tiếc, đơn hàng " + product + " đã bị hủy.");
                refreshData();
            }
        });
    }

    private void refreshData() {
        cursor = db.getAllOrders();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer, tvProduct, tvInfo, tvStatus;
        Button btnApprove, btnCancel;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomer = itemView.findViewById(R.id.tvOrderCustomer);
            tvProduct = itemView.findViewById(R.id.tvOrderProduct);
            tvInfo = itemView.findViewById(R.id.tvOrderInfo);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnApprove = itemView.findViewById(R.id.btnApproveOrder);
            btnCancel = itemView.findViewById(R.id.btnCancelOrder);
        }
    }
}