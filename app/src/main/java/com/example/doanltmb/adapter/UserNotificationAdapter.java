package com.example.doanltmb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.model.Order;

import java.util.ArrayList;
import java.util.List;

public class UserNotificationAdapter extends RecyclerView.Adapter<UserNotificationAdapter.NotificationViewHolder> {

    public interface NotificationActionListener {
        void onDeleteNotification(int orderId);
    }

    private final Context context;
    private final NotificationActionListener listener;
    private final List<Order> orders;

    public UserNotificationAdapter(Context context, List<Order> orders, NotificationActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.orders = new ArrayList<>();
        if (orders != null) {
            this.orders.addAll(orders);
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Order order = orders.get(position);

        if ("Approved".equalsIgnoreCase(order.getStatus())) {
            holder.tvTitle.setText("Thanh toán thành công");
            holder.tvMessage.setText("Đơn hàng của bạn đã thanh toán thành công: " + order.getProductName());
            holder.tvStatus.setText("Đã duyệt");
        } else if ("Cancelled".equalsIgnoreCase(order.getStatus())) {
            holder.tvTitle.setText("Đơn hàng bị hủy");
            holder.tvMessage.setText("Đơn hàng " + order.getProductName() + " đã bị hủy.");
            holder.tvStatus.setText("Đã hủy");
        } else {
            holder.tvTitle.setText("Cập nhật đơn hàng");
            holder.tvMessage.setText("Đơn hàng " + order.getProductName() + " hiện tại: " + order.getStatus());
            holder.tvStatus.setText(order.getStatus());
        }

        holder.tvTime.setText(order.getOrderDate());
        holder.itemView.setOnClickListener(v -> showDeleteDialog(order.getOrderId()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void swapData(List<Order> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    private void showDeleteDialog(int orderId) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa thông báo")
                .setMessage("Bạn có muốn xóa thông báo này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteNotification(orderId);
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvMessage;
        TextView tvTime;
        TextView tvStatus;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            tvStatus = itemView.findViewById(R.id.tvNotificationStatus);
        }
    }
}
