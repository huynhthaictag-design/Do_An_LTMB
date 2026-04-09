package com.example.doanltmb.activity.product;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.doanltmb.activity.user.UserNotificationActivity;
import com.example.doanltmb.adapter.CartAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.CartItem;
import com.example.doanltmb.utils.NotificationHelper;
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
        cartAdapter = new CartAdapter(this, cartItemList, this::loadCartData);
        recyclerViewCart.setAdapter(cartAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("currentUsername", "tai");

        loadCartData();
    }

    private void loadCartData() {
        cartItemList.clear();
        List<CartItem> items = dbHelper.getCartItemModels(currentUsername);
        cartItemList.addAll(items);

        double totalPrice = 0;
        for (CartItem item : items) {
            totalPrice += item.getPrice() * item.getQuantity();
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
                if (cartItemList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isSuccess = dbHelper.checkoutCart(currentUsername);

                if (isSuccess) {
                    NotificationHelper.sendNotification(
                            CartActivity.this,
                            "Đơn hàng mới!",
                            "Thịnh Admin ơi, người dùng " + currentUsername + " vừa đặt hàng kìa!"
                    );

                    Toast.makeText(CartActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    loadCartData();
                    showBottomNavIfHidden();
                } else {
                    Toast.makeText(CartActivity.this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showBottomNavIfHidden() {
        if (bottomNav == null) return;

        bottomNav.post(() -> {
            if (bottomNav.getTranslationY() > 0f) {
                bottomNav.animate()
                        .translationY(0f)
                        .setDuration(200)
                        .start();
            }
        });
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

            if (itemId == R.id.nav_notification) {
                startActivity(new Intent(CartActivity.this, UserNotificationActivity.class));
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
            MenuItem item = bottomNav.getMenu().findItem(R.id.nav_cart);
            if (item != null) {
                item.setChecked(true);
            }
        }
        showBottomNavIfHidden();
    }
}
