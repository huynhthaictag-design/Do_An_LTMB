package com.example.doanltmb.activity.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;

public class AddProductActivity extends AppCompatActivity {

    private EditText nameInput, priceInput, urlInput;
    private DatabaseHelper db;
    private boolean isEditMode = false;
    private String oldProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);
        nameInput = findViewById(R.id.inputProductName);
        priceInput = findViewById(R.id.inputProductPrice);
        urlInput = findViewById(R.id.inputProductImage);
        Button saveButton = findViewById(R.id.btnSaveProduct);

        isEditMode = getIntent().getBooleanExtra("IS_EDIT", false);

        if (isEditMode) {
            saveButton.setText("CẬP NHẬT SẢN PHẨM");
            oldProductName = getIntent().getStringExtra("OLD_NAME");
            nameInput.setText(oldProductName);
            String price = getIntent().getStringExtra("OLD_PRICE");
            if (price != null) {
                // Chỉ lấy số để hiển thị cho Admin sửa
                priceInput.setText(price.replaceAll("[^0-9]", ""));
            }
            urlInput.setText(getIntent().getStringExtra("OLD_URL"));
        }

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String url = urlInput.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Thịnh ơi, đừng để trống tên và giá nhé!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                // CHẾ ĐỘ SỬA: database của bạn đang lưu giá kèm chữ 'đ'
                int result = db.updateProduct(oldProductName, name, priceStr + "đ", url);
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // CHẾ ĐỘ THÊM MỚI:
                try {
                    // Ép kiểu về Double trước khi thêm (không cộng 'đ' ở đây)
                    double priceVal = Double.parseDouble(priceStr);
                    boolean success = db.addProduct(name, priceVal, url);

                    if (success) {
                        Toast.makeText(this, "Đã thêm sản phẩm mới!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá tiền phải là số nha!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}