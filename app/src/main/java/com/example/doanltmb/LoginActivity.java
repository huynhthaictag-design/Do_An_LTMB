package com.example.doanltmb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dòng này cực kỳ quan trọng: Nó kết nối code với giao diện XML của bạn
        setContentView(R.layout.activity_login);

        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}