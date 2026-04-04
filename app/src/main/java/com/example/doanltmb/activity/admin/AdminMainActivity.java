package com.example.doanltmb.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.doanltmb.model.Product;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanltmb.R;
import com.example.doanltmb.activity.user.ProfileActivity;
import com.example.doanltmb.adapter.AdminProductAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {
    private RecyclerView rvAdminProducts;
    private DatabaseHelper db;
    private EditText edtSearch;
    private List<Product> fullList;
    private BottomNavigationView bottomNav;
    private ImageView btnOrderList; // Nút xem đơn hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        db = new DatabaseHelper(this);
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        edtSearch = findViewById(R.id.edtAdminSearch);
        bottomNav = findViewById(R.id.bottomNavigation);
        FloatingActionButton btnAdd = findViewById(R.id.btnAddNewProduct);

        rvAdminProducts.setLayoutManager(new GridLayoutManager(this, 2));

        loadData();
        setupBottomNavigation();

        // Sự kiện nút thêm sản phẩm
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));

        // Tìm kiếm sản phẩm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBottomNavigation() {
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) return true;

            if (itemId == R.id.nav_notification) {
                startActivity(new Intent(AdminMainActivity.this, AdminOrderActivity.class));
                return true;
            }

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(AdminMainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }


    private void loadData() {
        fullList = db.getAllProductsList();
        rvAdminProducts.setAdapter(new AdminProductAdapter(this, fullList));
    }

    private void filterProducts(String keyword) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : fullList) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }
        rvAdminProducts.setAdapter(new AdminProductAdapter(this, filtered));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_home);
    }
}