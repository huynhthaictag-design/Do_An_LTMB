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
    private static final int DATABASE_VERSION = 21;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, role TEXT, phone TEXT)");
        db.execSQL("CREATE TABLE categories (category_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT)");
        db.execSQL("CREATE TABLE products (product_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, description TEXT, price REAL, image_url TEXT, category_id INTEGER, quantity INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE cart (cart_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER)");
        db.execSQL("CREATE TABLE orders (order_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_name TEXT, quantity INTEGER, price REAL, status TEXT DEFAULT 'Pending', order_date DATETIME DEFAULT CURRENT_TIMESTAMP, is_hidden INTEGER DEFAULT 0)");
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // 1. Tài khoản mặc định
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('admin', '" + HashUtil.hashPassword("123123") + "', 'admin')");
        db.execSQL("INSERT INTO users(username, password, role) VALUES ('tai', '" + HashUtil.hashPassword("123456") + "', 'customer')");

        // 2. Danh mục
        db.execSQL("INSERT INTO categories(category_name) VALUES ('Điện thoại'), ('Laptop'), ('Phụ kiện')");


        db.execSQL("INSERT INTO products(product_name, price, image_url, description, category_id) VALUES " +
                "('iPhone 13 Pro', 18500000, 'iphone13', 'Màn hình Super Retina XDR, chip A15 cực mạnh', 1)," +
                "('Samsung S23 Ultra', 21900000, 'samsungs23', 'Camera 200MP, S-Pen thần thánh', 1)," +
                "('MacBook Air M1', 19000000, 'macbookairm1', 'Chip M1 siêu nhanh, pin cả ngày', 2)," +
                "('Dell XPS 13', 28000000, 'dellxps13', 'Màn hình vô cực, đẳng cấp doanh nhân', 2)," +
                "('AirPods Pro', 4500000, 'airpodspro', 'Chống ồn chủ động, âm thanh đỉnh cao', 3)," +

                "('iPhone 15 Pro Max', 34990000, 'iphone15pm', 'Khung viền Titan, chip A17 Pro mạnh mẽ nhất', 1)," +
                "('Samsung Galaxy S24 Ultra', 29990000, 's24ultra', 'Quyền năng Galaxy AI, camera zoom 100x', 1)," +
                "('Google Pixel 8 Pro', 18500000, 'pixel8pro', 'Trải nghiệm Android thuần khiết, camera AI đỉnh', 1)," +
                "('Xiaomi 14 Ultra', 25500000, 'xiaomi14u', 'Ống kính Leica thế hệ mới, sạc siêu tốc', 1)," +
                "('OPPO Find X7 Ultra', 19000000, 'oppofindx7', 'Thiết kế sang trọng, camera tiềm vọng kép', 1)," +

                "('MacBook Pro M3', 39900000, 'macbookm3', 'Hiệu năng đồ họa vượt trội, màn hình ProMotion', 2)," +
                "('ASUS ROG Strix G16', 31500000, 'rogstrix', 'Laptop Gaming đỉnh cao, tản nhiệt cực mát', 2)," +
                "('MSI Katana 15', 24000000, 'msikatana', 'Vũ khí chiến game mạnh mẽ cho game thủ', 2)," +
                "('Lenovo ThinkPad X1 Carbon', 36000000, 'thinkpadx1', 'Bền bỉ chuẩn quân đội, bàn phím gõ cực sướng', 2)," +
                "('HP Spectre x360', 32000000, 'hpspectre', 'Màn hình OLED xoay gập 360 độ linh hoạt', 2)," +

                "('Tai nghe Sony WH-1000XM5', 7500000, 'sonyxm5', 'Chống ồn tốt nhất thế giới, pin 30 giờ', 3)," +
                "('Chuột Logitech MX Master 3S', 2400000, 'mxmaster3s', 'Cuộn siêu nhanh, hỗ trợ làm việc đa nhiệm', 3)," +
                "('Bàn phím cơ Keychron K2', 1900000, 'keychronk2', 'Kết nối không dây, switch cơ gõ cực đã', 3)," +
                "('Loa Marshall Emberton II', 3900000, 'marshall', 'Âm thanh 360 độ, thiết kế cổ điển sang trọng', 3)," +
                "('Apple Watch Ultra 2', 20500000, 'watchultra', 'Vỏ Titan bền bỉ, màn hình sáng 3000 nits', 3)");
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
    // Lay danh sach ten danh muc de hien thi cho bo loc va form admin.
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

    // Them san pham moi tu form admin voi day du thong tin co the luu duoc.
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

    // Cap nhat san pham hien co theo du lieu moi trong form admin.
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

    // Lay chi tiet san pham de do du lieu len form sua trong man admin.
    public Cursor getProductForEdit(String productName) {
        return this.getReadableDatabase().rawQuery(
                "SELECT p.product_name, p.description, p.price, p.image_url, p.quantity, c.category_name " +
                        "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id WHERE p.product_name = ?",
                new String[]{productName}
        );
    }

    public void deleteProduct(String name) {
        this.getWritableDatabase().delete("products", "product_name = ?", new String[]{name});
    }

    // Tim category_id theo ten danh muc de luu dung khoa ngoai cho san pham.
    private int getCategoryIdByName(String categoryName) {
        Cursor c = this.getReadableDatabase().rawQuery(
                "SELECT category_id FROM categories WHERE category_name = ?",
                new String[]{categoryName}
        );
        try {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } finally {
            c.close();
        }
        return 1;
    }
    // THÊM DANH MỤC MỚI
    public boolean addCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);

        long result = db.insert("categories", null, values);
        db.close();

        return result != -1; // Trả về true nếu thêm thành công
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
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM orders WHERE status = 'Pending' ORDER BY order_id DESC", null);
    }

    // Lay danh sach thong bao don hang da duoc cap nhat cho user hien tai.
    public Cursor getUserNotifications(String username) {
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM orders WHERE username = ? AND status != 'Pending' AND is_hidden = 0 ORDER BY order_id DESC",
                new String[]{username});
    }

    // Danh dau thong bao da bi an thay vi xoa han don hang khoi database.
    public boolean hideUserNotification(int orderId, String username) {
        ContentValues v = new ContentValues();
        v.put("is_hidden", 1);
        return this.getWritableDatabase().update(
                "orders",
                v,
                "order_id = ? AND username = ?",
                new String[]{String.valueOf(orderId), username}
        ) > 0;
    }

    // Lay lich su mua hang da duoc duyet cua user de hien thi trong trang ho so.
    public Cursor getUserPurchaseHistory(String username) {
        return this.getReadableDatabase().rawQuery(
                "SELECT product_name, quantity, price, order_date FROM orders WHERE username = ? AND status = 'Approved' ORDER BY order_date DESC",
                new String[]{username});
    }

    public Cursor getOrderById(int id) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM orders WHERE order_id = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateOrderStatus(int id, String s) {
        ContentValues v = new ContentValues(); v.put("status", s);
        return this.getWritableDatabase().update("orders", v, "order_id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }
    public ArrayList<Product> getProductsByPage(int pageNumber) {
        ArrayList<Product> list = new ArrayList<>();
        int pageSize = 8; // Số lượng mỗi trang
        int offset = (pageNumber - 1) * pageSize; // Vị trí bắt đầu lấy

        SQLiteDatabase db = this.getReadableDatabase();
        // Lấy 8 sản phẩm, bỏ qua (offset) sản phẩm của các trang trước
        Cursor c = db.rawQuery("SELECT product_name, price, image_url, description, product_id FROM products LIMIT ? OFFSET ?",
                new String[]{String.valueOf(pageSize), String.valueOf(offset)});

        while (c.moveToNext()) {
            Product p = new Product(c.getString(0),
                    String.format("%,.0fđ", c.getDouble(1)).replace(",", "."),
                    c.getString(2), c.getString(3));
            p.setId(c.getInt(4));
            list.add(p);
        }
        c.close();
        return list;
    }
}
