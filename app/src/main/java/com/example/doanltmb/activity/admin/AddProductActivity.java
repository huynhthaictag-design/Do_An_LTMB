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
    private Button saveButton;
    private DatabaseHelper db;

    private boolean isEditMode = false;
    private String oldProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);

        nameInput = findViewById(R.id.nameInput);
        priceInput = findViewById(R.id.priceInput);
        urlInput = findViewById(R.id.urlInput);
        saveButton = findViewById(R.id.saveButton);

        // KIỂM TRA CHẾ ĐỘ: THÊM HAY SỬA
        isEditMode = getIntent().getBooleanExtra("IS_EDIT", false);

        if (isEditMode) {
            // Nếu là Sửa: Điền dữ liệu cũ vào các ô
            saveButton.setText("CẬP NHẬT SẢN PHẨM");
            oldProductName = getIntent().getStringExtra("OLD_NAME");
            nameInput.setText(oldProductName);

            // Xử lý giá: Bỏ chữ 'đ' để admin nhập số cho dễ
            String price = getIntent().getStringExtra("OLD_PRICE");
            if (price != null) {
                priceInput.setText(price.replace("đ", ""));
            }

            urlInput.setText(getIntent().getStringExtra("OLD_URL"));
        }

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();
            String url = urlInput.getText().toString().trim();

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ tên và giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                // Lệnh CẬP NHẬT
                int result = db.updateProduct(oldProductName, name, price + "đ", url);
                if (result > 0) {
                    Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Lệnh THÊM MỚI
                long result = db.addProduct(name, price + "đ", url);
                if (result != -1) {
                    Toast.makeText(this, "Đã thêm sản phẩm mới!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}