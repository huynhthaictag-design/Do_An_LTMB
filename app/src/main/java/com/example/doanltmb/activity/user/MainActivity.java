package com.example.doanltmb.activity.user;

import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.text.*;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.doanltmb.R;
import com.example.doanltmb.adapter.ProductAdapter;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvUsername;
    private EditText edtSearch;

    // Khai báo các biến phục vụ Lọc sản phẩm
    private View btnFilter;
    private int selectedCategoryIndex = 0; // Vị trí danh mục đang chọn
    private int selectedPriceIndex = 0;    // Vị trí mức giá đang chọn
    private List<String> categoryList;
    // ĐÂY LÀ BIẾN BẠN BỊ THIẾU
    private String[] priceOptions = {"Tất cả mức giá", "Dưới 10 triệu", "Từ 10 - 20 triệu", "Trên 20 triệu"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadProducts();
        loadUserInfo();
        setupBottomNavigation();
        setupSearch();
        setupFilter();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        tvUsername = findViewById(R.id.tvUsername);
        edtSearch = findViewById(R.id.edtSearch);
        btnFilter = findViewById(R.id.btnFilter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dbHelper = new DatabaseHelper(this);
    }

    private void loadProducts() {
        List<Product> productList = dbHelper.getAllProductsList();
        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUsername", "");
        if (!username.isEmpty()) {
            Cursor cursor = dbHelper.getUser(username);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                if (tvUsername != null) {
                    tvUsername.setText(name);
                }
                cursor.close();
            }
        }
    }

    // ==========================================
    // HÀM TÌM KIẾM ĐÃ ĐƯỢC TỐI ƯU
    // ==========================================
    private void setupSearch() {
        if (edtSearch == null) return;

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Đơn giản hóa: Khi gõ chữ, chỉ cần gọi hàm Lọc Tổng Hợp
                applyCombinedFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            if (itemId == R.id.nav_notification) return true;
            if (itemId == R.id.nav_shopping_cart) return true;
            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // ==========================================
    // HÀM HIỂN THỊ HỘP THOẠI LỌC KẾT HỢP
    // ==========================================
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

    // ==========================================
    // HÀM LỌC TỔNG HỢP: DANH MỤC -> TÌM KIẾM -> GIÁ
    // ==========================================
    private void applyCombinedFilters() {
        // 1. Lọc theo Danh mục
        List<Product> currentList;
        if (selectedCategoryIndex == 0 || categoryList == null) {
            currentList = dbHelper.getAllProductsList();
        } else {
            String selectedCategoryName = categoryList.get(selectedCategoryIndex);
            currentList = dbHelper.getProductsByCategory(selectedCategoryName);
        }

        // 2. Lọc tiếp bằng Từ khóa
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

        // 3. Lọc tiếp bằng Mức giá
        List<Product> finalFilteredList = new ArrayList<>();
        if (selectedPriceIndex == 0) {
            finalFilteredList.addAll(currentList);
        } else {
            for (Product p : currentList) {
                try {
                    String priceStr = p.getPrice().replaceAll("[^0-9]", "");
                    if (priceStr.isEmpty()) continue;
                    double price = Double.parseDouble(priceStr);

                    if (selectedPriceIndex == 1 && price < 10000000) {
                        finalFilteredList.add(p);
                    } else if (selectedPriceIndex == 2 && price >= 10000000 && price <= 20000000) {
                        finalFilteredList.add(p);
                    } else if (selectedPriceIndex == 3 && price > 20000000) {
                        finalFilteredList.add(p);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 4. Cập nhật kết quả lên màn hình
        adapter = new ProductAdapter(MainActivity.this, finalFilteredList);
        recyclerView.setAdapter(adapter);
    }
}