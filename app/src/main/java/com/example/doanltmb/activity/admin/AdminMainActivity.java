package com.example.doanltmb.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    // BIẾN PHÂN TRANG
    private int currentPage = 1;
    private final int totalPages = 3;
    private TextView tvPageInfo;
    private Button btnPrev, btnNext;
    private View layoutAdminPagination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        db = new DatabaseHelper(this);
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        edtSearch = findViewById(R.id.edtAdminSearch);
        bottomNav = findViewById(R.id.bottomNavigation);
        FloatingActionButton btnAdd = findViewById(R.id.btnAddNewProduct);

        // ÁNH XẠ CÁC NÚT PHÂN TRANG
        tvPageInfo = findViewById(R.id.tvPageInfo);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        layoutAdminPagination = findViewById(R.id.layoutAdminPagination);

        rvAdminProducts.setLayoutManager(new GridLayoutManager(this, 2));

        loadData();
        setupBottomNavigation();
        setupPagination(); // Thiết lập sự kiện bấm nút phân trang

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPagination() {
        if (btnNext == null || btnPrev == null) return;

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadData();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadData();
            }
        });
    }

    private void setupBottomNavigation() {
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) return true;

            if(itemId == R.id.nav_category){
                startActivity(new Intent(AdminMainActivity.this, AddCategoryActivity.class));
                return true;
            }

            if (itemId == R.id.nav_notification) {
                startActivity(new Intent(AdminMainActivity.this, AdminOrderActivity.class));
                return true;
            }

            if (itemId == R.id.nav_profile) {
                // GIỮ NGUYÊN ĐỂ VÀO ĐƯỢC TÀI KHOẢN
                startActivity(new Intent(AdminMainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }

    private void loadData() {
        // LẤY DỮ LIỆU THEO TRANG (8 MÓN)
        List<Product> pageList = db.getProductsByPage(currentPage);
        fullList = db.getAllProductsList(); // Giữ fullList để phục vụ tìm kiếm
        rvAdminProducts.setAdapter(new AdminProductAdapter(this, pageList));

        // Cập nhật giao diện thanh phân trang
        if(tvPageInfo != null) tvPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        if(btnPrev != null) btnPrev.setEnabled(currentPage > 1);
        if(btnNext != null) btnNext.setEnabled(currentPage < totalPages);
    }

    private void filterProducts(String keyword) {
        if (keyword.isEmpty()) {
            loadData(); // Quay lại chế độ phân trang
            if (layoutAdminPagination != null) layoutAdminPagination.setVisibility(View.VISIBLE);
            return;
        }

        List<Product> filtered = new ArrayList<>();
        for (Product p : fullList) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }
        rvAdminProducts.setAdapter(new AdminProductAdapter(this, filtered));

        // Ẩn thanh phân trang khi đang tìm kiếm để tránh rối dữ liệu
        if (layoutAdminPagination != null) layoutAdminPagination.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_home);
    }
}