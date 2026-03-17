package com.example.doanltmb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.utils.HashUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        usernameInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        DatabaseHelper db = new DatabaseHelper(this);

        // Chuyển sang Register
        registerText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Xử lý login
        loginButton.setOnClickListener(v -> {

            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Hash password
                String hashedPassword = HashUtil.hashPassword(password);

                boolean check = db.checkLogin(username, hashedPassword);

                if (check) {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}