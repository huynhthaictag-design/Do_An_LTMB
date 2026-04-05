package com.example.doanltmb.activity.admin;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.doanltmb.R;
import com.example.doanltmb.database.DatabaseHelper;
import com.example.doanltmb.utils.ImageLoader;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    private static final String DEFAULT_IMAGE_NAME = "ic_launcher_background";

    private TextInputEditText nameInput;
    private TextInputEditText priceInput;
    private TextInputEditText quantityInput;
    private TextInputEditText descriptionInput;
    private AutoCompleteTextView categoryInput;
    private Button saveButton;
    private Button cancelButton;
    private CardView uploadImageCard;
    private ImageView imagePreview;
    private TextView imageTitle;
    private TextView imageHint;
    private DatabaseHelper db;
    private boolean isEditMode;
    private String oldProductName;
    private String currentImageValue = DEFAULT_IMAGE_NAME;

    private final ActivityResultLauncher<String[]> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleImagePicked);

    // Khoi tao man them sua san pham va ket noi day du cac thanh phan cua form.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);

        initViews();
        setupToolbar();
        setupCategoryDropdown();
        bindEditDataIfNeeded();
        setupActions();
        updateImagePreview();
    }

    // Anh xa toan bo view can su dung trong form them sua san pham.
    private void initViews() {
        nameInput = findViewById(R.id.edtProductName);
        priceInput = findViewById(R.id.edtPrice);
        quantityInput = findViewById(R.id.edtQuantity);
        descriptionInput = findViewById(R.id.edtDescription);
        categoryInput = findViewById(R.id.autoCompleteCategory);
        saveButton = findViewById(R.id.btnSaveProduct);
        cancelButton = findViewById(R.id.btnCancel);
        uploadImageCard = findViewById(R.id.cvUploadImage);
        imagePreview = findViewById(R.id.imgProductPreview);
        imageTitle = findViewById(R.id.tvImageUploadTitle);
        imageHint = findViewById(R.id.tvImageUploadHint);
    }

    // Gan nut quay lai cho toolbar.
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    // Nap danh sach danh muc tu database vao dropdown trong form admin.
    private void setupCategoryDropdown() {
        ArrayList<String> categories = db.getAllCategoryNames();
        if (!categories.isEmpty()) {
            categories.remove(0);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        categoryInput.setAdapter(adapter);
    }

    // Neu dang sua san pham thi do du lieu cu len form va giu lai anh da luu.
    private void bindEditDataIfNeeded() {
        isEditMode = getIntent().getBooleanExtra("IS_EDIT", false);
        oldProductName = getIntent().getStringExtra("OLD_NAME");

        if (!isEditMode || oldProductName == null || oldProductName.trim().isEmpty()) {
            return;
        }

        saveButton.setText("CAP NHAT SAN PHAM");

        Cursor cursor = db.getProductForEdit(oldProductName);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                nameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
                descriptionInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                priceInput.setText(String.valueOf((long) cursor.getDouble(cursor.getColumnIndexOrThrow("price"))));
                quantityInput.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))));

                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                if (categoryName != null) {
                    categoryInput.setText(categoryName, false);
                }

                String imageValue = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                if (imageValue != null && !imageValue.trim().isEmpty()) {
                    currentImageValue = imageValue;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Gan su kien cho nut chon anh, luu va huy.
    private void setupActions() {
        if (uploadImageCard != null) {
            uploadImageCard.setOnClickListener(v -> openImagePicker());
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> handleSaveProduct());
        }

        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> finish());
        }
    }

    // Mo thu vien anh cua dien thoai de admin chon anh cho san pham.
    private void openImagePicker() {
        imagePickerLauncher.launch(new String[]{"image/*"});
    }

    // Nhan Uri anh da chon, giu quyen doc va cap nhat preview tren form.
    private void handleImagePicked(Uri uri) {
        if (uri == null) {
            return;
        }

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
        }

        currentImageValue = uri.toString();
        updateImagePreview();
    }

    // Cap nhat preview anh theo drawable mac dinh hoac Uri da chon tu thu vien.
    private void updateImagePreview() {
        if (imagePreview != null) {
            ImageLoader.loadProductImage(this, imagePreview, currentImageValue);
        }

        if (imageTitle != null) {
            imageTitle.setText(currentImageValue.startsWith("content://")
                    ? "Da chon anh tu thu vien"
                    : "Tai len hinh anh dai dien");
        }

        if (imageHint != null) {
            imageHint.setText(currentImageValue.startsWith("content://")
                    ? "Nhan vao day neu ban muon chon lai anh khac"
                    : "Dinh dang JPG, PNG");
        }
    }

    // Doc, kiem tra va luu du lieu form vao database theo che do them hoac sua.
    private void handleSaveProduct() {
        String name = readText(nameInput);
        String priceText = readText(priceInput);
        String quantityText = readText(quantityInput);
        String description = readText(descriptionInput);
        String category = categoryInput.getText() == null ? "" : categoryInput.getText().toString().trim();

        if (!validateInputs(name, priceText, quantityText, category)) {
            return;
        }

        try {
            double priceValue = Double.parseDouble(priceText.replace(",", ""));
            int quantityValue = Integer.parseInt(quantityText);

            boolean success;
            if (isEditMode) {
                success = db.updateProduct(
                        oldProductName,
                        name,
                        priceValue,
                        currentImageValue,
                        description,
                        category,
                        quantityValue
                ) > 0;
            } else {
                success = db.addProduct(
                        name,
                        priceValue,
                        currentImageValue,
                        description,
                        category,
                        quantityValue
                );
            }

            if (success) {
                Toast.makeText(
                        this,
                        isEditMode ? "Cap nhat san pham thanh cong!" : "Them san pham thanh cong!",
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            } else {
                Toast.makeText(this, "Khong the luu san pham. Vui long thu lai!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Gia tien va so luong phai la so hop le!", Toast.LENGTH_SHORT).show();
        }
    }

    // Kiem tra cac truong bat buoc truoc khi luu san pham.
    private boolean validateInputs(String name, String priceText, String quantityText, String category) {
        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui long nhap du ten, gia, so luong va danh muc!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double priceValue = Double.parseDouble(priceText.replace(",", ""));
            int quantityValue = Integer.parseInt(quantityText);

            if (priceValue <= 0) {
                Toast.makeText(this, "Gia san pham phai lon hon 0!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (quantityValue < 0) {
                Toast.makeText(this, "So luong khong duoc am!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Gia tien va so luong phai la so hop le!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Doc text tu TextInputEditText va tra ve chuoi an toan de xu ly.
    private String readText(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
