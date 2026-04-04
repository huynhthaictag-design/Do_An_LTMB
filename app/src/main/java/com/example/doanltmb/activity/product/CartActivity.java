package com.example.doanltmb.activity.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.user.MainActivity;
import com.example.doanltmb.activity.user.ProfileActivity;
import com.example.doanltmb.adapter.CartAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.CartItem;
// --- IMPORT MỚI ---
import com.example.doanltmb.utils.NotificationHelper;
// ------------------
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnCheckout;
    private BottomNavigationView bottomNav;

    private RecyclerView recyclerViewCart;
    private TextView tvTotalPrice;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private DatabaseHelper dbHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCheckout = findViewById(R.id.btnCheckout);
        bottomNav = findViewById(R.id.bottomNavigation);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        dbHelper = new DatabaseHelper(this);
        cartItemList = new ArrayList<>();

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItemList, new CartAdapter.CartUpdateListener() {
            @Override
            public void onCartUpdated() {
                loadCartData();
            }
        });
        recyclerViewCart.setAdapter(cartAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("currentUsername", "tai");

        loadCartData();
    }

    private void loadCartData() {
        cartItemList.clear();
        double totalPrice = 0;

        Cursor cursor = dbHelper.getCartItems(currentUsername);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cartId = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                String imageUrl = cursor.getString(3);
                int quantity = cursor.getInt(4);

                cartItemList.add(new CartItem(cartId, name, price, imageUrl, quantity));
                totalPrice += (price * quantity);

            } while (cursor.moveToNext());
            cursor.close();
        }

        cartAdapter.notifyDataSetChanged();

        String formattedTotal = String.format("%,.0fđ", totalPrice).replace(",", ".");
        if (tvTotalPrice != null) {
            tvTotalPrice.setText(formattedTotal);
        }
    }

    private void setListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (cartItemList == null || cartItemList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isSuccess = dbHelper.checkoutCart(currentUsername);

                if (isSuccess) {
                    // --- THÊM LOGIC THÔNG BÁO CHO ADMIN ---
                    NotificationHelper.sendNotification(
                            CartActivity.this,
                            "Đơn hàng mới!",
                            "Thịnh Admin ơi, người dùng " + currentUsername + " vừa đặt hàng kìa!"
                    );
                    // --------------------------------------

                    Toast.makeText(CartActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    loadCartData();
                } else {
                    Toast.makeText(CartActivity.this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNav == null) return;

        try {
            MenuItem itemCart = bottomNav.getMenu().findItem(R.id.nav_cart);
            if (itemCart != null) {
                itemCart.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }

            if (itemId == R.id.nav_cart) {
                return true;
            }

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
                return false;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            android.view.MenuItem item = bottomNav.getMenu().findItem(R.id.nav_cart);
            if (item != null) {
                item.setChecked(true);
            }
        }
    }
}