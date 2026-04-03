package com.example.doanltmb.activity.product;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;

import java.text.BreakIterator;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView img, btnBack;
    Button btnAddToCart;
    TextView name, price, desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        img = findViewById(R.id.imgProduct);
        name = findViewById(R.id.tvName);
        price = findViewById(R.id.tvPrice);
        desc = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        int productId = getIntent().getIntExtra("product_id", -1);

        DatabaseHelper db = new DatabaseHelper(this);
        Product p = db.getProductById(productId);

        if (p != null) {
            name.setText(p.getName());
            price.setText(p.getPrice());
            desc.setText(p.getDescription());

            int resId = getResources().getIdentifier(
                    p.getImageUrl(),
                    "drawable",
                    getPackageName()
            );

            if (resId != 0) {
                img.setImageResource(resId);
            }
        }
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        btnAddToCart.setOnClickListener(v -> {
            // 1. Lấy username của người dùng đang đăng nhập (giống cách làm ở CartActivity)
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String currentUsername = sharedPreferences.getString("currentUsername", "tai");

            // 2. Lấy tên sản phẩm mà người dùng đang xem
            // (Nếu bạn đang gán tên SP lên TextView thì dùng getText(), hoặc lấy từ Intent)
            String productName = name.getText().toString();

            // 3. Gọi hàm thêm vào Database mà chúng ta đã viết trong DatabaseHelper
            boolean isSuccess = db.addToCart(currentUsername, productName);

            // 4. Kiểm tra kết quả và hiển thị thông báo
            if (isSuccess) {
                // Nếu thành công (isSuccess == true)
                Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu thất bại (isSuccess == false)
                Toast.makeText(ProductDetailActivity.this, "Lỗi! Không thể thêm vào giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}