package com.example.doanltmb.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.doanltmb.model.Product;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanltmb.R;
import com.example.doanltmb.activity.product.ProductDetailActivity;
import com.example.doanltmb.model.Product; // Import lớp Product từ model
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> list;

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = list.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(p.getPrice());

        // Tìm ảnh trong drawable theo tên lưu trong Database
        int resId = context.getResources().getIdentifier(p.getImageUrl(), "drawable", context.getPackageName());
        holder.imgProduct.setImageResource(resId != 0 ? resId : R.drawable.ic_launcher_background);

        // Bấm vào để xem chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", p.getId());
            context.startActivity(intent);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textProductName);
            tvPrice = itemView.findViewById(R.id.textProductPrice);
            imgProduct = itemView.findViewById(R.id.imageProduct);
        }
    }
}