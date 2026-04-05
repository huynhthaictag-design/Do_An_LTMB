package com.example.doanltmb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseHistoryViewHolder> {

    private final Context context;
    private Cursor cursor;

    // Khoi tao adapter lich su mua hang tu cursor doc trong bang orders.
    public PurchaseHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // Tao layout cho tung dong lich su mua hang.
    @NonNull
    @Override
    public PurchaseHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history, parent, false);
        return new PurchaseHistoryViewHolder(view);
    }

    // Do du lieu ten san pham, thoi gian, so luong va gia tien len item.
    @Override
    public void onBindViewHolder(@NonNull PurchaseHistoryViewHolder holder, int position) {
        if (cursor == null || !cursor.moveToPosition(position)) return;

        String productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));

        holder.tvProductName.setText(productName);
        holder.tvOrderTime.setText(orderDate);
        holder.tvQuantity.setText("So luong: " + quantity);
        holder.tvPrice.setText("Gia tien: " + String.format("%,.0fđ", price * quantity).replace(",", "."));
    }

    // Tra ve so dong lich su hien co.
    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    // Thay cursor moi va dong cursor cu de tranh leak.
    public void swapCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    // Dong cursor khi adapter khong con su dung.
    public void close() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    static class PurchaseHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvOrderTime, tvQuantity, tvPrice;

        PurchaseHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvHistoryProductName);
            tvOrderTime = itemView.findViewById(R.id.tvHistoryOrderTime);
            tvQuantity = itemView.findViewById(R.id.tvHistoryQuantity);
            tvPrice = itemView.findViewById(R.id.tvHistoryPrice);
        }
    }
}
