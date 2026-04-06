package com.example.doanltmb.activity.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class AddCategoryActivity extends AppCompatActivity {

    private TextInputEditText edtCategoryName;
    private Button btnAddCategory;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ View
        Toolbar toolbar = findViewById(R.id.toolbar);
        edtCategoryName = findViewById(R.id.edtCategoryName);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        // Bắt sự kiện nút Back trên Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bắt sự kiện bấm nút Thêm
        btnAddCategory.setOnClickListener(v -> {
            String categoryName = edtCategoryName.getText().toString().trim();

            if (categoryName.isEmpty()) {
                Toast.makeText(AddCategoryActivity.this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm thêm vào Database
            boolean isSuccess = dbHelper.addCategory(categoryName);

            if (isSuccess) {
                Toast.makeText(AddCategoryActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Thêm thành công thì tự động đóng màn hình này lại
            } else {
                Toast.makeText(AddCategoryActivity.this, "Lỗi! Không thể thêm danh mục.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}