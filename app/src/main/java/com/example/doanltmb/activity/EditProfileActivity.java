package com.example.doanltmb.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFullName, editPhone;
    private Button btnSave;
    private DatabaseHelper db;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = new DatabaseHelper(this);

        // Lấy username của người đang đăng nhập từ SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPref.getString("currentUsername", "");

        editFullName = findViewById(R.id.editFullName);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSaveProfile);

        btnSave.setOnClickListener(v -> {
            String name = editFullName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Thịnh ơi, điền đủ thông tin nhé!", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.updateUserProfile(currentUsername, name, phone);
                if (success) {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình Profile
                } else {
                    Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}