package com.example.doanltmb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dòng này cực kỳ quan trọng: Nó kết nối code với giao diện XML của bạn
        setContentView(R.layout.activity_regis);

        TextView loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}