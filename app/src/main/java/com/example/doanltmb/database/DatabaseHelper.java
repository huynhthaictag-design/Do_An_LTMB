package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

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
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_details");

        onCreate(db);
    }

    // LOGIN
    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

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

    // GET PRODUCTS
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

}