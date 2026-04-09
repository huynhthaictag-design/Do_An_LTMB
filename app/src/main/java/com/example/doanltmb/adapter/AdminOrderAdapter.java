package com.example.doanltmb.adapter;

import android.content.Context;
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
import com.example.doanltmb.model.Order;
import com.example.doanltmb.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private final Context context;
    private final DatabaseHelper db;
    private final List<Order> orders;

    public AdminOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.db = new DatabaseHelper(context);
        this.orders = new ArrayList<>(orders);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvCustomer.setText("Khách hàng: " + order.getUsername());
        holder.tvProduct.setText("Sản phẩm: " + order.getProductName());
        holder.tvInfo.setText("SL: " + order.getQuantity() + " | Tổng: " +
                String.format("%,.0fđ", order.getTotalPrice()).replace(",", "."));
        holder.tvStatus.setText("Trạng thái: " + order.getStatus());

        holder.btnApprove.setOnClickListener(v -> {
            if (db.updateOrderStatus(order.getOrderId(), "Approved")) {
                Toast.makeText(context, "Đã duyệt đơn hàng", Toast.LENGTH_SHORT).show();
                NotificationHelper.sendNotification(
                        context,
                        "Đơn hàng thành công",
                        "Đơn hàng " + order.getProductName() + " đã được Admin duyệt!"
                );
                refreshData();
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (db.updateOrderStatus(order.getOrderId(), "Cancelled")) {
                Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                NotificationHelper.sendNotification(
                        context,
                        "Thông báo đơn hàng",
                        "Rất tiếc, đơn hàng " + order.getProductName() + " đã bị hủy."
                );
                refreshData();
            }
        });
    }

    private void refreshData() {
        orders.clear();
        orders.addAll(db.getAllOrders());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer;
        TextView tvProduct;
        TextView tvInfo;
        TextView tvStatus;
        Button btnApprove;
        Button btnCancel;

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
