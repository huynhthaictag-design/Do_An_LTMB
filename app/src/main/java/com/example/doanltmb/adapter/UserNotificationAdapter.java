package com.example.doanltmb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;

public class UserNotificationAdapter extends RecyclerView.Adapter<UserNotificationAdapter.NotificationViewHolder> {

    public interface NotificationActionListener {
        void onDeleteNotification(int orderId);
    }

    private final Context context;
    private final NotificationActionListener listener;
    private Cursor cursor;

    // Khoi tao adapter thong bao va callback xoa thong bao.
    public UserNotificationAdapter(Context context, Cursor cursor, NotificationActionListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    // Tao layout cho tung item thong bao trong danh sach.
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    // Bind noi dung thong bao va gan su kien bam vao item.
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        if (cursor == null || !cursor.moveToPosition(position)) return;

        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
        String productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));

        if ("Approved".equalsIgnoreCase(status)) {
            holder.tvTitle.setText("Thanh toán thành công");
            holder.tvMessage.setText("Đơn hàng của bạn đã thanh toán thành công: " + productName);
            holder.tvStatus.setText("Đã duyệt");
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            holder.tvTitle.setText("Đơn hàng bị hủy");
            holder.tvMessage.setText("Đơn hàng " + productName + " đã bị hủy.");
            holder.tvStatus.setText("Đã hủy");
        } else {
            holder.tvTitle.setText("Cập nhật đơn hàng");
            holder.tvMessage.setText("Đơn hàng " + productName + " hiện tại: " + status);
            holder.tvStatus.setText(status);
        }

        holder.tvTime.setText(orderDate);
        holder.itemView.setOnClickListener(v -> showDeleteDialog(orderId));
    }

    @Override
    // Tra ve so thong bao dang duoc hien thi.
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    // Thay cursor moi sau khi du lieu thay doi va dong cursor cu an toan.
    public void swapCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    // Giai phong cursor khi adapter khong con duoc su dung.
    public void close() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    // Hien hop thoai xac nhan truoc khi an thong bao khoi danh sach user.
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
        TextView tvTitle, tvMessage, tvTime, tvStatus;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            tvStatus = itemView.findViewById(R.id.tvNotificationStatus);
        }
    }
}
