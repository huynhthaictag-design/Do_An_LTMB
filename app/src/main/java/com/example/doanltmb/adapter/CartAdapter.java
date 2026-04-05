package com.example.doanltmb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.CartItem;
import com.example.doanltmb.utils.ImageLoader;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;
    private DatabaseHelper dbHelper;
    private CartUpdateListener listener;

    // Interface để giao tiếp với CartActivity
    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItemList, CartUpdateListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context); // Khởi tạo DB để cập nhật
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        String formattedPrice = String.format("%,.0fđ", item.getPrice()).replace(",", ".");
        holder.tvPrice.setText(formattedPrice);

        ImageLoader.loadProductImage(context, holder.imgProduct, item.getImageUrl());

        // BẮT SỰ KIỆN NÚT CỘNG
        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            int newQty = currentQty + 1;

            dbHelper.updateCartQuantity(item.getCartId(), newQty); // Cập nhật DB
            item.setQuantity(newQty); // Cập nhật List
            notifyItemChanged(position); // Cập nhật giao diện 1 item này
            listener.onCartUpdated(); // Báo cho Activity tính lại tổng tiền
        });

        // BẮT SỰ KIỆN NÚT TRỪ
        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 1) {
                int newQty = currentQty - 1;
                dbHelper.updateCartQuantity(item.getCartId(), newQty);
                item.setQuantity(newQty);
                notifyItemChanged(position);
                listener.onCartUpdated();
            } else {
                // Nếu số lượng đang là 1 mà bấm trừ -> Xóa luôn
                dbHelper.deleteCartItem(item.getCartId());
                cartItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItemList.size());
                listener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, btnPlus, btnMinus;
        ImageView imgProduct;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameCart);
            tvPrice = itemView.findViewById(R.id.tvPriceCart);
            tvQuantity = itemView.findViewById(R.id.tvQuantityCart);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            imgProduct = itemView.findViewById(R.id.imgProductCart);
        }
    }
}
