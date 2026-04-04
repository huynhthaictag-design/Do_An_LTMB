package com.example.doanltmb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.R;
import com.example.doanltmb.activity.admin.AdminMainActivity;
import com.example.doanltmb.activity.user.MainActivity;
import com.example.doanltmb.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerText;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        loginButton.setOnClickListener(v -> {
            String user = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Thịnh ơi, đừng để trống tài khoản mật khẩu nhé!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Kiểm tra đăng nhập
            if (db.checkUser(user, pass)) {

                // 2. Lấy vai trò (Role) của User này từ Database
                String role = db.getUserRole(user);

                // 3. Lưu trạng thái đăng nhập vào SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("currentUsername", user);
                editor.putString("userRole", role); // Lưu 'admin' hoặc 'customer'
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công: " + role, Toast.LENGTH_SHORT).show();

                // 4. PHÂN LUỒNG MÀN HÌNH CHÍNH
                if (role != null && role.equalsIgnoreCase("admin")) {
                    // Nếu là Admin -> Vào màn hình quản trị của Thịnh
                    Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                    startActivity(intent);
                } else {
                    // Nếu là User -> Vào màn hình chính của bạn Thịnh
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                // Đóng màn hình Login sau khi đăng nhập thành công
                finish();

            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu rồi!", Toast.LENGTH_SHORT).show();
            }
        });
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}