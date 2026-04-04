package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.doanltmb.model.Product; // Đảm bảo đã import đúng model
import com.example.doanltmb.utils.HashUtil;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "store.db";
    // Tăng lên 17 để nạp lại toàn bộ dữ liệu ảnh mới từ drawable
    private static final int DATABASE_VERSION = 17;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT, phone TEXT)");
        db.execSQL("CREATE TABLE categories (category_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT)");
        db.execSQL("CREATE TABLE products (product_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, description TEXT, price REAL, image_url TEXT, category_id INTEGER)");
        db.execSQL("CREATE TABLE cart (cart_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER)");
        db.execSQL("CREATE TABLE orders (order_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER, price REAL, status TEXT DEFAULT 'Pending', order_date DATETIME DEFAULT CURRENT_TIMESTAMP)");
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // 1. Tài khoản mặc định
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('admin', '" + HashUtil.hashPassword("123123") + "', 'admin')");
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('tai', '" + HashUtil.hashPassword("123456") + "', 'customer')");

        // 2. Danh mục
        db.execSQL("INSERT INTO categories(category_name) VALUES ('Điện thoại'), ('Laptop'), ('Phụ kiện')");

        // 3. Nạp sản phẩm khớp với ảnh trong drawable của Thịnh
        db.execSQL("INSERT INTO products(product_name, price, image_url, description, category_id) VALUES " +
                "('iPhone 13 Pro', 18500000, 'iphone13', 'Màn hình Super Retina XDR, chip A15 cực mạnh', 1)," +
                "('Samsung S23 Ultra', 21900000, 'samsungs23', 'Camera 200MP, S-Pen thần thánh', 1)," +
                "('MacBook Air M1', 19000000, 'macbookairm1', 'Chip M1 siêu nhanh, pin cả ngày', 2)," +
                "('Dell XPS 13', 28000000, 'dellxps13', 'Màn hình vô cực, đẳng cấp doanh nhân', 2)," +
                "('AirPods Pro', 4500000, 'airpodspro', 'Chống ồn chủ động, âm thanh đỉnh cao', 3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int next) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
    }

    // ===========================================================
    // 1. QUẢN LÝ TÀI KHOẢN & HỒ SƠ
    // ===========================================================
    public boolean registerUser(String u, String p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", u); v.put("password", HashUtil.hashPassword(p)); v.put("role", "customer");
        return db.insert("users", null, v) != -1;
    }

    public boolean updateUserProfile(String username, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", name); v.put("phone", phone);
        return db.update("users", v, "username = ?", new String[]{username}) > 0;
    }

    public Cursor getUser(String u) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM users WHERE username=?", new String[]{u});
    }

    public boolean checkUser(String u, String p) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{u, HashUtil.hashPassword(p)});
        boolean res = c.getCount() > 0; c.close(); return res;
    }

    public String getUserRole(String u) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT role FROM users WHERE username=?", new String[]{u});
        String r = c.moveToFirst() ? c.getString(0) : "customer"; c.close(); return r;
    }

    // ===========================================================
    // 2. QUẢN LÝ SẢN PHẨM & LỌC (ADMIN + USER)
    // ===========================================================
    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Tất cả danh mục");
        Cursor c = this.getReadableDatabase().rawQuery("SELECT category_name FROM categories", null);
        while (c.moveToNext()) list.add(c.getString(0));
        c.close(); return list;
    }

    public ArrayList<Product> getProductsByCategory(String categoryName) {
        ArrayList<Product> list = new ArrayList<>();
        String query = "SELECT p.product_name, p.price, p.image_url, p.description, p.product_id FROM products p INNER JOIN categories c ON p.category_id = c.category_id WHERE c.category_name = ?";
        Cursor c = this.getReadableDatabase().rawQuery(query, new String[]{categoryName});
        while (c.moveToNext()) {
            Product p = new Product(c.getString(0), String.format("%,.0fđ", c.getDouble(1)).replace(",", "."), c.getString(2), c.getString(3));
            p.setId(c.getInt(4)); list.add(p);
        }
        c.close(); return list;
    }

    public ArrayList<Product> getAllProductsList() {
        ArrayList<Product> list = new ArrayList<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT product_name, price, image_url, description, product_id FROM products", null);
        while (c.moveToNext()) {
            Product p = new Product(c.getString(0), String.format("%,.0fđ", c.getDouble(1)).replace(",", "."), c.getString(2), c.getString(3));
            p.setId(c.getInt(4)); list.add(p);
        }
        c.close(); return list;
    }

    public Product getProductById(int id) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT product_name, price, image_url, description FROM products WHERE product_id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            Product p = new Product(c.getString(0), String.format("%,.0fđ", c.getDouble(1)).replace(",", "."), c.getString(2), c.getString(3));
            c.close(); return p;
        }
        return null;
    }

    public boolean addProduct(String name, double price, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("product_name", name); v.put("price", price); v.put("image_url", url); v.put("category_id", 1);
        return db.insert("products", null, v) != -1;
    }

    public int updateProduct(String old, String name, String price, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        String p = price.replaceAll("[^0-9]", "");
        v.put("product_name", name); v.put("price", Double.parseDouble(p)); v.put("image_url", url);
        return db.update("products", v, "product_name = ?", new String[]{old});
    }

    public void deleteProduct(String name) {
        this.getWritableDatabase().delete("products", "product_name = ?", new String[]{name});
    }

    // ===========================================================
    // 3. GIỎ HÀNG & ĐƠN HÀNG
    // ===========================================================
    public boolean addToCart(String user, String prod) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", user); v.put("product_name", prod); v.put("quantity", 1);
        return db.insert("cart", null, v) != -1;
    }

    public void updateCartQuantity(int id, int qty) {
        ContentValues v = new ContentValues(); v.put("quantity", qty);
        this.getWritableDatabase().update("cart", v, "cart_id=?", new String[]{String.valueOf(id)});
    }

    public void deleteCartItem(int id) {
        this.getWritableDatabase().delete("cart", "cart_id=?", new String[]{String.valueOf(id)});
    }

    public Cursor getCartItems(String user) {
        return this.getReadableDatabase().rawQuery("SELECT c.cart_id, c.product_name, p.price, p.image_url, c.quantity FROM cart c INNER JOIN products p ON c.product_name = p.product_name WHERE c.username=?", new String[]{user});
    }

    public boolean checkoutCart(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = getCartItems(user);
        db.beginTransaction();
        try {
            if (c.moveToFirst()) {
                do {
                    ContentValues v = new ContentValues();
                    v.put("username", user); v.put("product_name", c.getString(1)); v.put("quantity", c.getInt(4)); v.put("price", c.getDouble(2));
                    v.put("status", "Pending");
                    db.insert("orders", null, v);
                } while (c.moveToNext());
                db.delete("cart", "username=?", new String[]{user});
                db.setTransactionSuccessful(); return true;
            }
        } finally { db.endTransaction(); c.close(); }
        return false;
    }

    public Cursor getAllOrders() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM orders ORDER BY order_id DESC", null);
    }

    public Cursor getOrderById(int id) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE order_id = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateOrderStatus(int id, String s) {
        ContentValues v = new ContentValues(); v.put("status", s);
        return this.getWritableDatabase().update("orders", v, "order_id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}