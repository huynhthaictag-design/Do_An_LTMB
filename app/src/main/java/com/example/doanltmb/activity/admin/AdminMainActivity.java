package com.example.doanltmb.activity.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.LoginActivity;
import com.example.doanltmb.activity.user.ProfileActivity;
import com.example.doanltmb.adapter.AdminProductAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 8;

    private RecyclerView rvAdminProducts;
    private DatabaseHelper db;
    private EditText edtSearch;
    private View btnFilter;
    private List<Product> fullList;
    private BottomNavigationView bottomNav;
    private TextView tvPageInfo;
    private TextView tvAdminGreeting;
    private TextView tvAdminSummary;
    private Button btnPrev;
    private Button btnNext;
    private View layoutAdminPagination;
    private CardView cardAddProduct;
    private CardView cardAddCategory;
    private CardView cardOrders;
    private CardView cardLogout;
    private int currentPage = 1;
    private int totalPages = 1;
    private String adminUsername = "admin";

    private int selectedCategoryIndex = 0;
    private int selectedPriceIndex = 0;
    private List<String> categoryList;
    private final String[] priceOptions = {
            "Tat ca muc gia",
            "Duoi 10 trieu",
            "Tu 10 - 20 trieu",
            "Tren 20 trieu"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ensureAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_admin_main);

        db = new DatabaseHelper(this);
        fullList = new ArrayList<>();

        initViews();
        setupGreeting();
        setupRecyclerView();
        setupQuickActions();
        setupBottomNavigation();
        setupPagination();
        setupSearch();
        setupFilter();
        loadData();
    }

    // Kiem tra trang thai dang nhap va chi cho phep admin truy cap man nay.
    private boolean ensureAdminSession() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("userRole", "");
        adminUsername = prefs.getString("currentUsername", "admin");

        if (!isLoggedIn || role == null || !role.equalsIgnoreCase("admin")) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return false;
        }

        return true;
    }

    // Anh xa toan bo view tren dashboard admin.
    private void initViews() {
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        edtSearch = findViewById(R.id.edtAdminSearch);
        btnFilter = findViewById(R.id.btnFilter);
        bottomNav = findViewById(R.id.bottomNavigation);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        tvAdminGreeting = findViewById(R.id.tvAdminGreeting);
        tvAdminSummary = findViewById(R.id.tvAdminSummary);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        layoutAdminPagination = findViewById(R.id.layoutAdminPagination);
        cardAddProduct = findViewById(R.id.cardAddProduct);
        cardAddCategory = findViewById(R.id.cardAddCategory);
        cardOrders = findViewById(R.id.cardOrders);
        cardLogout = findViewById(R.id.cardLogout);
    }

    // Hien thi loi chao va vai tro nguoi dang nhap trong phan dau trang admin.
    private void setupGreeting() {
        if (tvAdminGreeting != null) {
            tvAdminGreeting.setText("Xin chào, " + adminUsername);
        }
    }

    // Cau hinh danh sach san pham cho man hinh quan tri.
    private void setupRecyclerView() {
        rvAdminProducts.setLayoutManager(new GridLayoutManager(this, 2));
    }

    // Gan su kien cho cac thao tac nhanh: them san pham, them danh muc, xem don va dang xuat.
    private void setupQuickActions() {
        if (cardAddProduct != null) {
            cardAddProduct.setOnClickListener(v -> openAddProduct());
        }

        if (cardAddCategory != null) {
            cardAddCategory.setOnClickListener(v ->
                    startActivity(new Intent(AdminMainActivity.this, AddCategoryActivity.class)));
        }

        if (cardOrders != null) {
            cardOrders.setOnClickListener(v ->
                    startActivity(new Intent(AdminMainActivity.this, AdminOrderActivity.class)));
        }

        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> logoutAdmin());
        }
    }

    // Mo form them san pham cho admin.
    private void openAddProduct() {
        startActivity(new Intent(this, AddProductActivity.class));
    }

    // Gan thanh dieu huong duoi cho cac man hinh quan tri chinh.
    private void setupBottomNavigation() {
        if (bottomNav == null) {
            return;
        }

        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            }

            if (itemId == R.id.nav_category) {
                startActivity(new Intent(AdminMainActivity.this, AddCategoryActivity.class));
                return true;
            }

            if (itemId == R.id.nav_add_product) {
                openAddProduct();
                return true;
            }

            if (itemId == R.id.nav_notification) {
                startActivity(new Intent(AdminMainActivity.this, AdminOrderActivity.class));
                return true;
            }

            if (itemId == R.id.nav_logout) {
                logoutAdmin();
                return true;
            }

            return false;
        });
    }

    // Thiet lap phan trang dong de khong bi co dinh 3 trang nhu truoc.
    private void setupPagination() {
        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> {
                if (currentPage > 1) {
                    currentPage--;
                    loadData();
                }
            });
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    loadData();
                }
            });
        }
    }

    // Lang nghe o tim kiem de loc danh sach san pham ngay tren dashboard.
    private void setupSearch() {
        if (edtSearch == null) {
            return;
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyCombinedFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Gan lai nut loc san pham theo danh muc va muc gia cho man admin.
    private void setupFilter() {
        if (btnFilter == null) {
            return;
        }

        btnFilter.setOnClickListener(v -> {
            categoryList = db.getAllCategoryNames();

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(60, 40, 60, 20);

            TextView tvCategory = new TextView(this);
            tvCategory.setText("Lọc theo danh mục:");
            tvCategory.setTextSize(16);
            tvCategory.setPadding(0, 0, 0, 10);
            layout.addView(tvCategory);

            Spinner spinnerCategory = new Spinner(this);
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryList
            );
            spinnerCategory.setAdapter(categoryAdapter);
            spinnerCategory.setSelection(selectedCategoryIndex);
            layout.addView(spinnerCategory);

            TextView tvPrice = new TextView(this);
            tvPrice.setText("Lọc theo mức giá:");
            tvPrice.setTextSize(16);
            tvPrice.setPadding(0, 40, 0, 10);
            layout.addView(tvPrice);

            Spinner spinnerPrice = new Spinner(this);
            ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    priceOptions
            );
            spinnerPrice.setAdapter(priceAdapter);
            spinnerPrice.setSelection(selectedPriceIndex);
            layout.addView(spinnerPrice);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    // Nap du lieu san pham, cap nhat tong trang va refresh dashboard admin.
    private void loadData() {
        fullList = db.getAllProductsList();
        totalPages = Math.max(1, (int) Math.ceil(fullList.size() / (double) PAGE_SIZE));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<Product> pageList = db.getProductsByPage(currentPage);
        rvAdminProducts.setAdapter(new AdminProductAdapter(this, pageList));

        updateSummary();
        updatePaginationState();
    }

    // Hien thi tom tat nhanh so luong san pham admin dang quan ly.
    private void updateSummary() {
        if (tvAdminSummary == null) {
            return;
        }

        if (fullList.isEmpty()) {
            tvAdminSummary.setText("Chưa có sản phẩm nào trong cửa hàng.");
            return;
        }

        tvAdminSummary.setText("Đang quản lý " + fullList.size() + " sản phẩm trong cửa hàng.");
    }

    // Cap nhat trang thai nut va thong tin phan trang theo tong so san pham hien tai.
    private void updatePaginationState() {
        if (tvPageInfo != null) {
            tvPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        }

        if (btnPrev != null) {
            btnPrev.setEnabled(currentPage > 1);
        }

        if (btnNext != null) {
            btnNext.setEnabled(currentPage < totalPages);
        }

        if (layoutAdminPagination != null) {
            layoutAdminPagination.setVisibility(fullList.size() > PAGE_SIZE ? View.VISIBLE : View.GONE);
        }
    }

    // Gop tim kiem va loc de hien thi dung danh sach san pham admin can thao tac.
    private void applyCombinedFilters() {
        String keyword = edtSearch != null ? edtSearch.getText().toString().trim().toLowerCase() : "";
        boolean hasFilter = !keyword.isEmpty() || selectedCategoryIndex != 0 || selectedPriceIndex != 0;

        if (!hasFilter) {
            loadData();
            return;
        }

        List<Product> currentList;
        if (selectedCategoryIndex == 0 || categoryList == null) {
            currentList = db.getAllProductsList();
        } else {
            currentList = db.getProductsByCategory(categoryList.get(selectedCategoryIndex));
        }

        List<Product> searchFilteredList = new ArrayList<>();
        if (!keyword.isEmpty()) {
            for (Product product : currentList) {
                if (product.getName() != null
                        && product.getName().toLowerCase().contains(keyword)) {
                    searchFilteredList.add(product);
                }
            }
            currentList = searchFilteredList;
        }

        List<Product> finalFilteredList = new ArrayList<>();
        if (selectedPriceIndex == 0) {
            finalFilteredList.addAll(currentList);
        } else {
            for (Product product : currentList) {
                try {
                    String priceStr = product.getPrice().replaceAll("[^0-9]", "");
                    if (priceStr.isEmpty()) {
                        continue;
                    }

                    double price = Double.parseDouble(priceStr);
                    if (selectedPriceIndex == 1 && price < 10000000) {
                        finalFilteredList.add(product);
                    } else if (selectedPriceIndex == 2 && price >= 10000000 && price <= 20000000) {
                        finalFilteredList.add(product);
                    } else if (selectedPriceIndex == 3 && price > 20000000) {
                        finalFilteredList.add(product);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        rvAdminProducts.setAdapter(new AdminProductAdapter(this, finalFilteredList));
        if (layoutAdminPagination != null) {
            layoutAdminPagination.setVisibility(View.GONE);
        }
    }

    // Dang xuat admin, xoa session hien tai va quay ve man hinh dang nhap.
    private void logoutAdmin() {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        sharedPref.edit().clear().apply();

        Toast.makeText(this, "Da dang xuat tai khoan admin.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
