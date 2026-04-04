package com.example.doanltmb.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanltmb.R;
import com.example.doanltmb.activity.admin.AddProductActivity;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminViewHolder> {
    private Context context;
    private List<Product> list;
    private DatabaseHelper db;

    public AdminProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Product p = list.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(p.getPrice());

        // --- ĐOẠN CODE SỬA LỖI HÌNH ẢNH ---
        String imageName = p.getImageUrl(); // Lấy tên file từ Database (ví dụ: iphone13)
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (resId != 0) {
            holder.imgProduct.setImageResource(resId);
        } else {
            // Nếu không tìm thấy ảnh, hiện ảnh mặc định
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }
        // ----------------------------------

        // Nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddProductActivity.class);
            intent.putExtra("IS_EDIT", true);
            intent.putExtra("OLD_NAME", p.getName());
            intent.putExtra("OLD_PRICE", p.getPrice());
            intent.putExtra("OLD_URL", p.getImageUrl());
            context.startActivity(intent);
        });

        // Nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            db.deleteProduct(p.getName());
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            Toast.makeText(context, "Đã xóa: " + p.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override public int getItemCount() { return list.size(); }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct, btnEdit, btnDelete;
        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgAdminProduct); // ID phải khớp với XML
            tvName = itemView.findViewById(R.id.tvAdminProductName);
            tvPrice = itemView.findViewById(R.id.tvAdminProductPrice);
            btnEdit = itemView.findViewById(R.id.imgAdminEdit);
            btnDelete = itemView.findViewById(R.id.imgAdminDelete);
        }
    }
}