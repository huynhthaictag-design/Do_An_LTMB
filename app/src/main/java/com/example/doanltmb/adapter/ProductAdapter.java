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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.AddProductActivity;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private String userRole;
    private DatabaseHelper db;

    public ProductAdapter(Context context, List<Product> productList, String userRole) {
        this.context = context;
        this.productList = productList;
        this.userRole = userRole;
        this.db = new DatabaseHelper(context); // Khởi tạo Database để dùng cho việc xóa
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Gán dữ liệu lên giao diện
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice());

        // 2. Kiểm tra quyền Admin để hiện/ẩn nút Sửa và Xóa
        if ("admin".equals(userRole)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // 3. XỬ LÝ NÚT XÓA (Thùng rác đỏ)
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa " + product.getName() + " không?")
                    .setPositiveButton("Xóa luôn", (dialog, which) -> {
                        // Xóa trong Database (Truyền vào String tên sản phẩm)
                        db.deleteProduct(product.getName());

                        // Cập nhật lại danh sách trên màn hình
                        int currentPosition = holder.getAdapterPosition();
                        productList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, productList.size());

                        Toast.makeText(context, "Đã xóa: " + product.getName(), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // 4. XỬ LÝ NÚT SỬA (Cây bút xanh)
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddProductActivity.class);
            intent.putExtra("IS_EDIT", true);
            intent.putExtra("OLD_NAME", product.getName());
            intent.putExtra("OLD_PRICE", product.getPrice());
            intent.putExtra("OLD_URL", product.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image, btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textProductName);
            price = itemView.findViewById(R.id.textProductPrice);
            image = itemView.findViewById(R.id.imageProduct);
            btnEdit = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
        }
    }
}