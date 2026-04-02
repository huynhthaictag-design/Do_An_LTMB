package com.example.doanltmb.activity.user;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView img, btnBack;
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
    }
}