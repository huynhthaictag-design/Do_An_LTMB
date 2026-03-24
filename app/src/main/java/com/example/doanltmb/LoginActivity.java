package com.example.doanltmb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dòng này cực kỳ quan trọng: Nó kết nối code với giao diện XML của bạn
        setContentView(R.layout.activity_login);

        //Chuyển từ Login sang Regis
        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //Đăng nhập
        DatabaseHelper db = new DatabaseHelper(this);

        Button loginButton = findViewById(R.id.loginButton);
        EditText username = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);

        try {
            loginButton.setOnClickListener(v -> {

                String u = username.getText().toString();
                String p = password.getText().toString();

                boolean check = db.checkLogin(u, p);

                if (check) {
                    // --- ĐOẠN LƯU TÊN ĐĂNG NHẬP PHẢI NẰM Ở ĐÂY ---
                    android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERNAME", u); // Lưu tên người dùng
                    editor.apply();
                    // ------------------------------------------

                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Invalid account", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
}