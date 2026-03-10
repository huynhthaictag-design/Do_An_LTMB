package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doanltmb.Product;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // USERS
        db.execSQL("CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "address TEXT," +
                "role TEXT)");

        // CATEGORIES
        db.execSQL("CREATE TABLE categories (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT," +
                "description TEXT)");

        // PRODUCTS
        db.execSQL("CREATE TABLE products (" +
                "product_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name TEXT," +
                "description TEXT," +
                "price REAL," +
                "stock INTEGER," +
                "image_url TEXT," +
                "category_id INTEGER)");

        // CART
        db.execSQL("CREATE TABLE cart (" +
                "cart_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "created_at TEXT)");

        // CART ITEMS
        db.execSQL("CREATE TABLE cart_items (" +
                "cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cart_id INTEGER," +
                "product_id INTEGER," +
                "quantity INTEGER)");

        // ORDERS
        db.execSQL("CREATE TABLE orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "total_price REAL," +
                "status TEXT," +
                "order_date TEXT," +
                "shipping_address TEXT)");

        // ORDER DETAILS
        db.execSQL("CREATE TABLE order_details (" +
                "order_detail_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "product_id INTEGER," +
                "quantity INTEGER," +
                "price REAL)");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {

        db.execSQL("INSERT INTO users(username,password,role) VALUES('admin','123456','admin')");
        db.execSQL("INSERT INTO users(username,password,role) VALUES('tai','123456','customer')");

        db.execSQL("INSERT INTO categories(category_name) VALUES('Điện thoại')");
        db.execSQL("INSERT INTO categories(category_name) VALUES('Laptop')");
        db.execSQL("INSERT INTO categories(category_name) VALUES('Tai nghe')");

        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('iPhone 13','Apple smartphone',15000000,10,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('Samsung Galaxy S23','Samsung flagship',17000000,8,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('MacBook Air M1','Apple laptop',22000000,5,2)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('Dell XPS 13','Dell laptop',25000000,4,2)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('AirPods Pro','Tai nghe Apple',4500000,15,3)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('SamSung Galaxy S21','Dien thoai Samsung',7000000,10,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('MacBook Pro M2','Apple laptop cao cấp',35000000,3,2)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('Google Pixel 7','Google smartphone',14000000,6,1)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('Sony WH-1000XM5','Tai nghe chống ồn Sony',8500000,12,3)");
        db.execSQL("INSERT INTO products(product_name,description,price,stock,category_id) " +
                "VALUES('ASUS ROG Zephyrus','Laptop Gaming',42000000,2,2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_details");

        onCreate(db);
    }

    // LOGIN
    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();
// đây là dòng kết nói
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        return cursor.getCount() > 0;
    }

    // REGISTER
    public boolean registerUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", "customer");

        long result = db.insert("users", null, values);

        return result != -1;
    }

    // GET PRODUCTS (Hàm cũ trả về chuỗi)
    public ArrayList<String> getProducts() {

        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT product_name,price FROM products", null);

        while (cursor.moveToNext()) {

            String name = cursor.getString(0);
            double price = cursor.getDouble(1);

            list.add(name + " - " + price + " VND");
        }

        return list;
    }

    // --- HÀM MỚI THÊM VÀO: Lấy danh sách đối tượng Product từ Database ---
    public ArrayList<Product> getAllProductsList() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy tên, giá và link ảnh từ bảng products
        Cursor cursor = db.rawQuery("SELECT product_name, price, image_url FROM products", null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            double price = cursor.getDouble(1);
            String imageUrl = cursor.getString(2);

            // Xử lý link ảnh nếu bị null
            if (imageUrl == null) {
                imageUrl = "";
            }

            // Định dạng lại giá tiền cho đẹp (ví dụ: 15000000 -> 15.000.000đ)
            String formattedPrice = String.format("%,.0fđ", price).replace(",", ".");

            // Thêm vào danh sách
            list.add(new Product(name, formattedPrice, imageUrl));
        }

        cursor.close();
        return list;
    }
}