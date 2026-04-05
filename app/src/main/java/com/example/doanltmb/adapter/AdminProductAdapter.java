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
import com.example.doanltmb.utils.ImageLoader;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminViewHolder> {
    private final Context context;
    private final List<Product> list;
    private final DatabaseHelper db;

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
        Product product = list.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());

        // Hien thi anh san pham tu drawable hoac Uri da luu trong database.
        ImageLoader.loadProductImage(context, holder.imgProduct, product.getImageUrl());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddProductActivity.class);
            intent.putExtra("IS_EDIT", true);
            intent.putExtra("OLD_NAME", product.getName());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            db.deleteProduct(product.getName());
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            Toast.makeText(context, "Da xoa: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct, btnEdit, btnDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgAdminProduct);
            tvName = itemView.findViewById(R.id.tvAdminProductName);
            tvPrice = itemView.findViewById(R.id.tvAdminProductPrice);
            btnEdit = itemView.findViewById(R.id.imgAdminEdit);
            btnDelete = itemView.findViewById(R.id.imgAdminDelete);
        }
    }
}
