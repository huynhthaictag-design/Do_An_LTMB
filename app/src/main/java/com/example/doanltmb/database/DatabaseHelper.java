package com.example.doanltmb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doanltmb.model.CartItem;
import com.example.doanltmb.model.Category;
import com.example.doanltmb.model.Order;
import com.example.doanltmb.model.Product;
import com.example.doanltmb.model.User;
import com.example.doanltmb.utils.HashUtil;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 24;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =========================================================================
    // 1. CORE DATABASE METHODS (KHỞI TẠO & DỮ LIỆU MẪU)
    // =========================================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT, phone TEXT)");
        db.execSQL("CREATE TABLE categories (category_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT)");
        db.execSQL("CREATE TABLE products (product_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, description TEXT, price REAL, image_url TEXT, category_id INTEGER, quantity INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE cart (cart_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER)");
        db.execSQL("CREATE TABLE orders (order_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER, price REAL, status TEXT DEFAULT 'Pending', order_date DATETIME DEFAULT CURRENT_TIMESTAMP, is_hidden INTEGER DEFAULT 0)");
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int next) {
        if (old < 19) {
            if (old < 18) {
                db.execSQL("ALTER TABLE orders ADD COLUMN is_hidden INTEGER DEFAULT 0");
            }
            db.execSQL("ALTER TABLE products ADD COLUMN quantity INTEGER DEFAULT 0");
            return;
        }

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Tài khoản mặc định
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('admin', '" + HashUtil.hashPassword("123123") + "', 'admin')");
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('tai', '" + HashUtil.hashPassword("123456") + "', 'customer')");

        // Danh mục
        db.execSQL("INSERT INTO categories(category_name) VALUES ('Điện thoại'), ('Laptop'), ('Phụ kiện')");

        // Sản phẩm
        db.execSQL("INSERT INTO products(product_name, price, image_url, description, category_id, quantity) VALUES " +
                "('iPhone 13 Pro', 18500000, 'iphone13', 'Màn hình Super Retina XDR, chip A15 cực mạnh', 1, 12)," +
                "('Samsung S23 Ultra', 21900000, 'samsungs23', 'Camera 200MP, S-Pen thần thánh', 1, 21)," +
                "('MacBook Air M1', 19000000, 'macbookairm1', 'Chip M1 siêu nhanh, pin cả ngày', 2, 12)," +
                "('Dell XPS 13', 28000000, 'dellxps13', 'Màn hình vô cực, đẳng cấp doanh nhân', 2, 32)," +
                "('AirPods Pro', 4500000, 'airpodspro', 'Chống ồn chủ động, âm thanh đỉnh cao', 3, 23)," +
                "('iPhone 15 Pro Max', 34990000, 'iphone15pm', 'Khung viền Titan, chip A17 Pro mạnh mẽ nhất', 1, 12)," +
                "('Samsung Galaxy S24 Ultra', 29990000, 's24ultra', 'Quyền năng Galaxy AI, camera zoom 100x', 1, 21)," +
                "('Google Pixel 8 Pro', 18500000, 'pixel8pro', 'Trải nghiệm Android thuần khiết, camera AI đỉnh', 1, 32)," +
                "('Xiaomi 14 Ultra', 25500000, 'xiaomi14u', 'Ống kính Leica thế hệ mới, sạc siêu tốc', 1, 14)," +
                "('OPPO Find X7 Ultra', 19000000, 'oppofindx7', 'Thiết kế sang trọng, camera tiềm vọng kép', 1, 15)," +
                "('MacBook Pro M3', 39900000, 'macbookm3', 'Hiệu năng đồ họa vượt trội, màn hình ProMotion', 2, 14)," +
                "('ASUS ROG Strix G16', 31500000, 'rogstrix', 'Laptop Gaming đỉnh cao, tản nhiệt cực mát', 2, 12)," +
                "('MSI Katana 15', 24000000, 'msikatana', 'Vũ khí chiến game mạnh mẽ cho game thủ', 2, 14)," +
                "('Lenovo ThinkPad X1 Carbon', 36000000, 'thinkpadx1', 'Bền bỉ chuẩn quân đội, bàn phím gõ cực sướng', 2, 17)," +
                "('HP Spectre x360', 32000000, 'hpspectre', 'Màn hình OLED xoay gập 360 độ linh hoạt', 2, 12)," +
                "('Tai nghe Sony WH-1000XM5', 7500000, 'sonyxm5', 'Chống ồn tốt nhất thế giới, pin 30 giờ', 3, 21)," +
                "('Chuột Logitech MX Master 3S', 2400000, 'mxmaster3s', 'Cuộn siêu nhanh, hỗ trợ làm việc đa nhiệm', 3, 12)," +
                "('Bàn phím cơ Keychron K2', 1900000, 'keychronk2', 'Kết nối không dây, switch cơ gõ cực đã', 3, 13)," +
                "('Loa Marshall Emberton II', 3900000, 'marshall', 'Âm thanh 360 độ, thiết kế cổ điển sang trọng', 3, 14)," +
                "('Apple Watch Ultra 2', 20500000, 'watchultra', 'Vỏ Titan bền bỉ, màn hình sáng 3000 nits', 3, 15)");
    }

    // =========================================================================
    // 2. USER MANAGEMENT (QUẢN LÝ NGƯỜI DÙNG)
    // =========================================================================

    public boolean registerUser(String u, String p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("username", u);
        v.put("password", HashUtil.hashPassword(p));
        v.put("role", "customer");
        return db.insert("users", null, v) != -1;
    }

    public boolean checkUser(String u, String p) {
        Cursor c = this.getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{u, HashUtil.hashPassword(p)}
        );
        boolean res = c.getCount() > 0;
        c.close();
        return res;
    }

    public Cursor getUser(String u) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM users WHERE username=?", new String[]{u});
    }

    public User getUserModel(String username) {
        Cursor cursor = getUser(username);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getString(cursor.getColumnIndexOrThrow("role")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public String getUserRole(String u) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT role FROM users WHERE username=?", new String[]{u});
        String r = c.moveToFirst() ? c.getString(0) : "customer";
        c.close();
        return r;
    }

    // =========================================================================
    // 3. CATEGORY MANAGEMENT (QUẢN LÝ DANH MỤC)
    // =========================================================================

    public boolean addCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);
        long result = db.insert("categories", null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT category_id, category_name FROM categories ORDER BY category_id", null);
        try {
            while (c.moveToNext()) {
                list.add(new Category(
                        c.getInt(c.getColumnIndexOrThrow("category_id")),
                        c.getString(c.getColumnIndexOrThrow("category_name"))
                ));
            }
        } finally {
            c.close();
        }
        return list;
    }

    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Tất cả danh mục");
        for (Category category : getAllCategories()) {
            list.add(category.getCategoryName());
        }
        return list;
    }

    private int getCategoryIdByName(String categoryName) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT category_id FROM categories WHERE category_name = ?", new String[]{categoryName});
        try {
            if (c.moveToFirst()) return c.getInt(0);
        } finally {
            c.close();
        }
        return 1;
    }

    // =========================================================================
    // 4. PRODUCT MANAGEMENT (QUẢN LÝ SẢN PHẨM)
    // =========================================================================

    public boolean addProduct(String name, double price, String url, String description, String categoryName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("product_name", name);
        v.put("price", price);
        v.put("image_url", url);
        v.put("description", description);
        v.put("category_id", getCategoryIdByName(categoryName));
        v.put("quantity", quantity);
        return db.insert("products", null, v) != -1;
    }

    public int updateProduct(String old, String name, double price, String url, String description, String categoryName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("product_name", name);
        v.put("price", price);
        v.put("image_url", url);
        v.put("description", description);
        v.put("category_id", getCategoryIdByName(categoryName));
        v.put("quantity", quantity);
        return db.update("products", v, "product_name = ?", new String[]{old});
    }

    public void deleteProduct(String name) {
        this.getWritableDatabase().delete("products", "product_name = ?", new String[]{name});
    }

    public ArrayList<Product> getAllProductsList() {
        ArrayList<Product> list = new ArrayList<>();
        Cursor c = this.getReadableDatabase().rawQuery("SELECT product_name, price, image_url, description, product_id, quantity FROM products", null);
        try {
            while (c.moveToNext()) {
                Product p = new Product(
                        c.getString(0),
                        formatPrice(c.getDouble(1)),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(5)
                );
                p.setId(c.getInt(4));
                list.add(p);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public ArrayList<Product> getProductsByCategory(String categoryName) {
        ArrayList<Product> list = new ArrayList<>();
        String query = "SELECT p.product_name, p.price, p.image_url, p.description, p.product_id, p.quantity " +
                "FROM products p INNER JOIN categories c ON p.category_id = c.category_id WHERE c.category_name = ?";
        Cursor c = this.getReadableDatabase().rawQuery(query, new String[]{categoryName});
        try {
            while (c.moveToNext()) {
                Product p = new Product(
                        c.getString(0),
                        formatPrice(c.getDouble(1)),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(5)
                );
                p.setId(c.getInt(4));
                list.add(p);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public ArrayList<Product> getProductsByPage(int pageNumber) {
        ArrayList<Product> list = new ArrayList<>();
        int pageSize = 8;
        int offset = (pageNumber - 1) * pageSize;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT product_name, price, image_url, description, product_id, quantity " +
                        "FROM products ORDER BY product_id DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(pageSize), String.valueOf(offset)}
        );
        try {
            while (c.moveToNext()) {
                Product p = new Product(
                        c.getString(0),
                        formatPrice(c.getDouble(1)),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(5)
                );
                p.setId(c.getInt(4));
                list.add(p);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT product_name, price, image_url, description, quantity FROM products WHERE product_id = ?",
                new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            Product p = new Product(
                    c.getString(0),
                    String.format("%,.0fđ", c.getDouble(1)).replace(",", "."),
                    c.getString(2),
                    c.getString(3),
                    c.getInt(4)
            );
            p.setId(id);
            c.close();
            return p;
        }
        c.close();
        return null;
    }

    public Cursor getProductForEdit(String productName) {
        return this.getReadableDatabase().rawQuery(
                "SELECT p.product_name, p.description, p.price, p.image_url, p.quantity, c.category_name " +
                        "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id WHERE p.product_name = ?",
                new String[]{productName}
        );
    }

    // =========================================================================
    // 5. CART MANAGEMENT (QUẢN LÝ GIỎ HÀNG)
    // =========================================================================

    public boolean addToCart(String username, String productName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor pc = db.rawQuery("SELECT quantity FROM products WHERE product_name = ?", new String[]{productName});
        int stock = 0;
        if (pc.moveToFirst()) stock = pc.getInt(0);
        pc.close();
        if (stock <= 0) return false;

        Cursor cc = db.rawQuery("SELECT quantity FROM cart WHERE username = ? AND product_name = ?", new String[]{username, productName});
        if (cc.moveToFirst()) {
            int currentInCart = cc.getInt(0);
            cc.close();
            if (currentInCart + 1 > stock) return false;

            ContentValues v = new ContentValues();
            v.put("quantity", currentInCart + 1);
            return db.update("cart", v, "username = ? AND product_name = ?", new String[]{username, productName}) > 0;
        } else {
            cc.close();
            ContentValues v = new ContentValues();
            v.put("username", username);
            v.put("product_name", productName);
            v.put("quantity", 1);
            return db.insert("cart", null, v) > -1;
        }
    }

    public boolean updateCartQuantity(int cartId, int newQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT product_name FROM cart WHERE cart_id = ?", new String[]{String.valueOf(cartId)});
        if (c.moveToFirst()) {
            String pName = c.getString(0);
            c.close();

            Cursor pc = db.rawQuery("SELECT quantity FROM products WHERE product_name = ?", new String[]{pName});
            if (pc.moveToFirst()) {
                int stock = pc.getInt(0);
                pc.close();
                if (newQty > stock) return false;
            }
        }
        ContentValues v = new ContentValues();
        v.put("quantity", newQty);
        return db.update("cart", v, "cart_id = ?", new String[]{String.valueOf(cartId)}) > 0;
    }

    public void deleteCartItem(int id) {
        this.getWritableDatabase().delete("cart", "cart_id=?", new String[]{String.valueOf(id)});
    }

    public Cursor getCartItems(String user) {
        return this.getReadableDatabase().rawQuery(
                "SELECT c.cart_id, c.product_name, p.price, p.image_url, c.quantity " +
                        "FROM cart c INNER JOIN products p ON c.product_name = p.product_name WHERE c.username=?",
                new String[]{user}
        );
    }

    public ArrayList<CartItem> getCartItemModels(String user) {
        ArrayList<CartItem> list = new ArrayList<>();
        Cursor c = getCartItems(user);
        try {
            while (c.moveToNext()) {
                list.add(new CartItem(
                        c.getInt(c.getColumnIndexOrThrow("cart_id")),
                        c.getString(c.getColumnIndexOrThrow("product_name")),
                        c.getDouble(c.getColumnIndexOrThrow("price")),
                        c.getString(c.getColumnIndexOrThrow("image_url")),
                        c.getInt(c.getColumnIndexOrThrow("quantity"))
                ));
            }
        } finally {
            c.close();
        }
        return list;
    }

    public boolean checkoutCart(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CartItem> cartItems = getCartItemModels(user);
        db.beginTransaction();
        try {
            if (!cartItems.isEmpty()) {
                for (CartItem item : cartItems) {
                    ContentValues v = new ContentValues();
                    v.put("username", user);
                    v.put("product_name", item.getProductName());
                    v.put("quantity", item.getQuantity());
                    v.put("price", item.getPrice());
                    v.put("status", "Pending");
                    db.insert("orders", null, v);
                }
                db.delete("cart", "username=?", new String[]{user});
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // =========================================================================
    // 6. ORDER MANAGEMENT (QUẢN LÝ ĐƠN HÀNG)
    // =========================================================================

    public ArrayList<Order> getAllOrders() {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE status = 'Pending' ORDER BY order_id DESC", null);
        return mapOrders(c);
    }

    public ArrayList<Order> getUserNotifications(String username) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE username = ? AND status != 'Pending' AND is_hidden = 0 ORDER BY order_id DESC", new String[]{username});
        return mapOrders(c);
    }

    public ArrayList<Order> getUserPurchaseHistory(String username) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE username = ? AND status = 'Approved' ORDER BY order_date DESC", new String[]{username});
        return mapOrders(c);
    }

    public Order getOrderById(int id) {
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE order_id = ?", new String[]{String.valueOf(id)});
        try {
            if (c.moveToFirst()) return mapOrder(c);
        } finally {
            c.close();
        }
        return null;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            if ("Approved".equals(status)) {
                Cursor c = db.rawQuery("SELECT product_name, quantity FROM orders WHERE order_id = ?", new String[]{String.valueOf(orderId)});
                if (c.moveToFirst()) {
                    String pName = c.getString(0);
                    int orderQty = c.getInt(1);
                    c.close();

                    Cursor pc = db.rawQuery("SELECT quantity FROM products WHERE product_name = ?", new String[]{pName});
                    if (pc.moveToFirst()) {
                        int stock = pc.getInt(0);
                        pc.close();
                        if (stock < orderQty) return false;

                        db.execSQL("UPDATE products SET quantity = quantity - ? WHERE product_name = ?", new Object[]{orderQty, pName});
                    }
                }
            }
            ContentValues v = new ContentValues();
            v.put("status", status);
            db.update("orders", v, "order_id = ?", new String[]{String.valueOf(orderId)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean hideUserNotification(int orderId, String username) {
        ContentValues v = new ContentValues();
        v.put("is_hidden", 1);
        return this.getWritableDatabase().update("orders", v, "order_id = ? AND username = ?", new String[]{String.valueOf(orderId), username}) > 0;
    }

    // =========================================================================
    // 7. UTILITY / HELPER METHODS (CÁC HÀM PHỤ TRỢ)
    // =========================================================================

    private ArrayList<Order> mapOrders(Cursor cursor) {
        ArrayList<Order> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                list.add(mapOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private Order mapOrder(Cursor cursor) {
        int orderId = getIntSafely(cursor, "order_id");
        String username = getStringSafely(cursor, "username");
        String productName = getStringSafely(cursor, "product_name");
        int quantity = getIntSafely(cursor, "quantity");
        double price = getDoubleSafely(cursor, "price");
        String status = getStringSafely(cursor, "status");
        String orderDate = getStringSafely(cursor, "order_date");
        boolean hidden = getIntSafely(cursor, "is_hidden") == 1;

        return new Order(orderId, username, productName, quantity, price, status, orderDate, hidden);
    }

    private int getIntSafely(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getInt(index) : 0;
    }

    private double getDoubleSafely(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getDouble(index) : 0;
    }

    private String getStringSafely(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getString(index) : "";
    }

    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(",", ".");
    }
}