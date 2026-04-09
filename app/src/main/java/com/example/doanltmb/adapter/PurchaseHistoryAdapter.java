package com.example.doanltmb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.model.Order;

import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseHistoryViewHolder> {

    private final Context context;
    private final List<Order> orders;

    public PurchaseHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = new ArrayList<>();
        if (orders != null) {
            this.orders.addAll(orders);
        }
    }

    @NonNull
    @Override
    public PurchaseHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false);
        return new PurchaseHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHistoryViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvProductName.setText(order.getProductName());
        holder.tvOrderTime.setText(order.getOrderDate());
        holder.tvQuantity.setText("Số lượng: " + order.getQuantity());
        holder.tvPrice.setText("Giá tiền: " + String.format("%,.0fđ", order.getTotalPrice()).replace(",", "."));
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

    static class PurchaseHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvOrderTime;
        TextView tvQuantity;
        TextView tvPrice;

        PurchaseHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvHistoryProductName);
            tvOrderTime = itemView.findViewById(R.id.tvHistoryOrderTime);
            tvQuantity = itemView.findViewById(R.id.tvHistoryQuantity);
            tvPrice = itemView.findViewById(R.id.tvHistoryPrice);
        }
    }
}
