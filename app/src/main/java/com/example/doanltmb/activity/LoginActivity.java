package com.example.doanltmb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.user.MainActivity;
import com.example.doanltmb.database.DatabaseHelper;
// Import thêm thư viện HashUtil của bạn vào đây
import com.example.doanltmb.utils.HashUtil;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Chuyển từ Login sang Regis
        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Đăng nhập
        DatabaseHelper db = new DatabaseHelper(this);

        Button loginButton = findViewById(R.id.loginButton);
        EditText username = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);

        try {
            loginButton.setOnClickListener(v -> {

                String u = username.getText().toString();
                String p = password.getText().toString(); // Mật khẩu chữ thường người dùng nhập

                // --- SỬA Ở ĐÂY: Mã hóa mật khẩu người dùng nhập vào ---
                String hashedPass = HashUtil.hashPassword(p);

                // --- Truyền mật khẩu ĐÃ MÃ HÓA vào database để kiểm tra ---
                boolean check = db.checkLogin(u, hashedPass);

                if (check) {
                    // Lưu tên đăng nhập
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERNAME", u);
                    editor.apply();

                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
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