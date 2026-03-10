package com.example.doanltmb;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dòng này cực kỳ quan trọng: Nó kết nối code với giao diện XML của bạn
        setContentView(R.layout.activity_login);
    }
}