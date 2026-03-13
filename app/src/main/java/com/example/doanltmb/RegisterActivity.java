package com.example.doanltmb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanltmb.database.DatabaseHelper;

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

        DatabaseHelper db = new DatabaseHelper(this);

        Button registerButton = findViewById(R.id.registerButton);
        EditText username = findViewById(R.id.nameInput);
        EditText password = findViewById(R.id.passwordInput);

        try {
            registerButton.setOnClickListener(v -> {

                String u = username.getText().toString();
                String p = password.getText().toString();

                boolean result = db.registerUser(u,p);

                if(result){

                    Toast.makeText(this,"Register success",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }else{

                    Toast.makeText(this,"Username already exists",Toast.LENGTH_SHORT).show();

                }

            });
        }
        catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}