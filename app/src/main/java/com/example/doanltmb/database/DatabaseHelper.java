package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doanltmb.model.Product;
import com.example.doanltmb.utils.HashUtil;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 5; // tăng version để reset DB

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "address TEXT," +
                "role TEXT)");

        db.execSQL("CREATE TABLE categories (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT," +
                "description TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name TEXT," +
                "description TEXT," +
                "price REAL," +
                "stock INTEGER," +
                "image_url TEXT," +
                "category_id INTEGER)");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {

        // HASH TẤT CẢ PASSWORD
        String adminPass = HashUtil.hashPassword("123123");
        String userPass = HashUtil.hashPassword("123456");

        db.execSQL("INSERT INTO users(username,password,role) VALUES('admin','" + adminPass + "','admin')");
        db.execSQL("INSERT INTO users(username,password,role) VALUES('tai','" + userPass + "','customer')");
        db.execSQL("INSERT INTO users(username,password,role) VALUES('thai','" + userPass + "','customer')");

        db.execSQL("INSERT INTO categories(category_name) VALUES('Điện thoại')");
        db.execSQL("INSERT INTO categories(category_name) VALUES('Laptop')");
        db.execSQL("INSERT INTO categories(category_name) VALUES('Tai nghe')");

        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) VALUES('iPhone 13','Apple smartphone',15000000,10,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) VALUES('Samsung Galaxy S23','Samsung flagship',17000000,8,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) VALUES('MacBook Air M1','Apple laptop',22000000,5,2)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) VALUES('Dell XPS 13','Dell laptop',25000000,4,2)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) VALUES('AirPods Pro','Tai nghe Apple',4500000,15,3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS products");

        onCreate(db);
    }

    // LOGIN (KHÔNG HASH Ở ĐÂY)
    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean result = cursor.moveToFirst();

        cursor.close();
        db.close();

        return result;
    }

    // REGISTER (KHÔNG HASH Ở ĐÂY)
    public boolean registerUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password); // đã hash từ Activity
        values.put("role", "customer");

        long result = db.insert("users", null, values);

        cursor.close();
        db.close();

        return result != -1;
    }

    // GET PRODUCTS
    public boolean addProduct(String name, double price, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();

        values.put("product_name", name);
        values.put("price", price);
        values.put("image_url", imageUrl);
        // Tạm thời fix cứng các thông tin chưa cần thiết để form đơn giản
        values.put("description", "Mô tả sản phẩm mới");
        values.put("stock", 10);
        values.put("category_id", 1);

        long result = db.insert("products", null, values);
        db.close();

        return result != -1; // Nếu insert thành công result sẽ khác -1
    }
    public ArrayList<Product> getAllProductsList() {

        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT product_name, price, image_url FROM products", null
        );

        while (cursor.moveToNext()) {

            String name = cursor.getString(0);
            double price = cursor.getDouble(1);
            String imageUrl = cursor.getString(2);

            if (imageUrl == null) imageUrl = "";

            String formattedPrice = String.format("%,.0fđ", price).replace(",", ".");

            list.add(new Product(name, formattedPrice, imageUrl));
        }

        cursor.close();
        db.close();

        return list;
    }
}