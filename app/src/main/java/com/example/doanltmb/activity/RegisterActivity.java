package com.example.doanltmb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.utils.HashUtil;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        // Ánh xạ view
        usernameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        DatabaseHelper db = new DatabaseHelper(this);

        // Chuyển sang Login
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        // Xử lý đăng ký
        registerButton.setOnClickListener(v -> {

            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Hash password
                String hashedPassword = HashUtil.hashPassword(password);

                boolean result = db.registerUser(username, hashedPassword);

                if (result) {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();

                } else {
                    Toast.makeText(this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}