package com.example.doanltmb.activity.user;

import android.content.*;
import android.os.Bundle;
import android.text.*;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.doanltmb.model.Product;
import com.example.doanltmb.model.User;
import com.example.doanltmb.adapter.ProductAdapter;
import com.example.doanltmb.R;
import com.example.doanltmb.activity.product.CartActivity;
import com.example.doanltmb.activity.LoginActivity;
import com.example.doanltmb.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvUsername;
    private EditText edtSearch;

    // Khai báo các biến phục vụ Lọc sản phẩm (Giữ nguyên của Thịnh)
    private View btnFilter;
    private int selectedCategoryIndex = 0;
    private int selectedPriceIndex = 0;
    private List<String> categoryList;
    private String[] priceOptions = {"Tất cả mức giá", "Dưới 10 triệu", "Từ 10 - 20 triệu", "Trên 20 triệu"};

    // --- CODE PHÂN TRANG MỚI ---
    private int currentPage = 1;
    private final int totalPages = 3; // 24 sản phẩm / 8 mỗi trang
    private TextView tvPageInfo;
    private Button btnPrev, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadProducts(); // Cập nhật để load theo trang
        loadUserInfo();
        setupBottomNavigation();
        setupSearch();
        setupFilter();
        setupPagination(); // Thiết lập nút bấm
        onResume();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        tvUsername = findViewById(R.id.tvUsername);
        edtSearch = findViewById(R.id.edtSearch);
        btnFilter = findViewById(R.id.btnFilter);

        // Ánh xạ nút phân trang
        tvPageInfo = findViewById(R.id.tvPageInfo);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new DatabaseHelper(this);
    }

    // --- LOGIC PHÂN TRANG ---
    private void setupPagination() {
        if (btnNext == null || btnPrev == null) return;
        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadProducts();
            }
        });
        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadProducts();
            }
        });
    }

    private void loadProducts() {
        // Sử dụng hàm getProductsByPage mới trong DatabaseHelper
        List<Product> productList = dbHelper.getProductsByPage(currentPage);
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        // Cập nhật giao diện nút
        if(tvPageInfo != null) tvPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        if(btnPrev != null) btnPrev.setEnabled(currentPage > 1);
        if(btnNext != null) btnNext.setEnabled(currentPage < totalPages);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUsername", "");
        if (!username.isEmpty()) {
            User user = dbHelper.getUserModel(username);
            if (user != null && tvUsername != null) {
                tvUsername.setText(user.getUsername());
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_notification) {
                if (!isLoggedIn) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem thông báo!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, UserNotificationActivity.class));
                }
                return true;
            }

            if (itemId == R.id.nav_cart){
                if (!isLoggedIn) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                }
                return true;
            }

            if (itemId == R.id.nav_profile) {
                if (!isLoggedIn) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem tài khoản!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                return true;
            }
            return false;
        });
    }

    private void setupSearch() {
        if (edtSearch == null) return;
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyCombinedFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilter() {
        if (btnFilter == null) return;
        btnFilter.setOnClickListener(v -> {
            categoryList = dbHelper.getAllCategoryNames();
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(60, 40, 60, 20);

            TextView tvCategory = new TextView(MainActivity.this);
            tvCategory.setText("Lọc theo danh mục:");
            tvCategory.setTextSize(16);
            tvCategory.setPadding(0, 0, 0, 10);
            layout.addView(tvCategory);

            Spinner spinnerCategory = new Spinner(MainActivity.this);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
            spinnerCategory.setAdapter(categoryAdapter);
            spinnerCategory.setSelection(selectedCategoryIndex);
            layout.addView(spinnerCategory);

            TextView tvPrice = new TextView(MainActivity.this);
            tvPrice.setText("Lọc theo mức giá:");
            tvPrice.setTextSize(16);
            tvPrice.setPadding(0, 40, 0, 10);
            layout.addView(tvPrice);

            Spinner spinnerPrice = new Spinner(MainActivity.this);
            ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, priceOptions);
            spinnerPrice.setAdapter(priceAdapter);
            spinnerPrice.setSelection(selectedPriceIndex);
            layout.addView(spinnerPrice);

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Bộ lọc sản phẩm");
            builder.setView(layout);
            builder.setPositiveButton("Áp dụng", (dialog, which) -> {
                selectedCategoryIndex = spinnerCategory.getSelectedItemPosition();
                selectedPriceIndex = spinnerPrice.getSelectedItemPosition();
                applyCombinedFilters();
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    // --- GIỮ NGUYÊN LOGIC LỌC CỦA THỊNH ---
    private void applyCombinedFilters() {
        List<Product> currentList;
        if (selectedCategoryIndex == 0 || categoryList == null) {
            currentList = dbHelper.getAllProductsList();
        } else {
            String selectedCategoryName = categoryList.get(selectedCategoryIndex);
            currentList = dbHelper.getProductsByCategory(selectedCategoryName);
        }

        String keyword = edtSearch != null ? edtSearch.getText().toString().trim().toLowerCase() : "";
        List<Product> searchFilteredList = new ArrayList<>();
        if (!keyword.isEmpty()) {
            for (Product p : currentList) {
                if (p.getName() != null && p.getName().toLowerCase().contains(keyword)) {
                    searchFilteredList.add(p);
                }
            }
            currentList = searchFilteredList;
        }

        List<Product> finalFilteredList = new ArrayList<>();
        if (selectedPriceIndex == 0) {
            finalFilteredList.addAll(currentList);
        } else {
            for (Product p : currentList) {
                try {
                    String priceStr = p.getPrice().replaceAll("[^0-9]", "");
                    if (priceStr.isEmpty()) continue;
                    double price = Double.parseDouble(priceStr);

                    if (selectedPriceIndex == 1 && price < 10000000) finalFilteredList.add(p);
                    else if (selectedPriceIndex == 2 && price >= 10000000 && price <= 20000000) finalFilteredList.add(p);
                    else if (selectedPriceIndex == 3 && price > 20000000) finalFilteredList.add(p);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }

        adapter = new ProductAdapter(MainActivity.this, finalFilteredList);
        recyclerView.setAdapter(adapter);

        // Ẩn thanh phân trang khi đang lọc để tránh rối dữ liệu
        boolean isSearching = !keyword.isEmpty() || selectedCategoryIndex != 0 || selectedPriceIndex != 0;
        findViewById(R.id.layoutPagination).setVisibility(isSearching ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            android.view.MenuItem item = bottomNav.getMenu().findItem(R.id.nav_home);
            if (item != null) item.setChecked(true);
        }
    }
}
