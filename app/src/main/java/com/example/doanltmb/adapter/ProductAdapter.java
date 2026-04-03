package com.example.doanltmb.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.product.ProductDetailActivity;
import com.example.doanltmb.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> list;

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product p = list.get(position);

        // set tên + giá
        holder.txtName.setText(p.getName());
        holder.txtPrice.setText(p.getPrice());

        // set ảnh từ drawable
        String imageName = p.getImageUrl(); // ví dụ: iphone13

        int resId = context.getResources().getIdentifier(
                imageName,
                "drawable",
                context.getPackageName()
        );

        if (resId != 0) {
            holder.img.setImageResource(resId);
        } else {
            // ảnh mặc định nếu sai tên
            holder.img.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", position + 1); // hoặc id thật nếu có
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView txtName, txtPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageProduct);
            txtName = itemView.findViewById(R.id.textProductName);
            txtPrice = itemView.findViewById(R.id.textProductPrice);
        }
    }
}