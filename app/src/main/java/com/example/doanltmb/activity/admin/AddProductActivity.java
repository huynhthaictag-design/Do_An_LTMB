package com.example.doanltmb.activity.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;

public class AddProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        EditText inputName = findViewById(R.id.inputProductName);
        EditText inputPrice = findViewById(R.id.inputProductPrice);
        EditText inputImage = findViewById(R.id.inputProductImage);
        Button btnSave = findViewById(R.id.btnSaveProduct);

        DatabaseHelper db = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String name = inputName.getText().toString();
            String priceStr = inputPrice.getText().toString();
            String image = inputImage.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                boolean success = db.addProduct(name, price, image);

                if (success) {
                    Toast.makeText(this, "Đăng sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Tự động đóng trang này và quay về trang chủ
                } else {
                    Toast.makeText(this, "Lỗi khi lưu vào database", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Giá tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}